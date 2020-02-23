package pers.di.quantplatform;
import java.util.*;

import pers.di.account.*;
import pers.di.account.common.HoldStock;
import pers.di.common.CDateTimeThruster;
import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CTest;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.*;
import pers.di.dataengine.tasks.SharedSession;
import pers.di.localstock.StockDataApi;
import pers.di.localstock.common.*;

public class Quant {
	
	public static class QuantListener extends IEngineListener
	{
		public QuantListener(Quant quant)
		{
			m_quant = quant;
		}
		public void onInitialize(DAContext context){
			m_quant.onInitialize(context);
		}
		public void onUnInitialize(DAContext context){
			m_quant.onUnInitialize(context);
		};
		public void onTradingDayStart(DAContext context){
			m_quant.onTradingDayStart(context);
		};
		public void onTradingDayFinish(DAContext context){
			m_quant.onTradingDayFinish(context);
		};
		public void onMinuteTimePrices(DAContext context){
			m_quant.onMinuteTimePrices(context);
		};
		public Quant m_quant;
	}
	
	private static Quant s_instance = new Quant(); 
	private Quant ()
	{
	}
	public static Quant instance() {  
		return s_instance;  
	} 
	
	public void run(String triggerCfgStr, AccountController cAccountController, QuantStrategy strategy)
	{
		// init m_listener and StockDataEngine
		m_listener = new QuantListener(this);
		StockDataEngine.instance().registerListener(m_listener);
		
		StockDataEngine.instance().config("TriggerMode", triggerCfgStr);

		// init m_cAccountController m_accountProxy
		m_cAccountController = cAccountController;
		m_accountProxy = new AccountProxy(cAccountController);
		
		// init m_stratety
		m_stratety = strategy;

		// init default object
		if(null == m_context)
		{
			m_context = new QuantContext();
			m_context.setAccountProxy(m_accountProxy);
		}
		
		StockDataEngine.instance().run();
		StockDataEngine.instance().unRegisterListener(m_listener);
	}
	
	/*
	 * ��������·��
	 */
	public boolean resetDataRoot(String dateRoot)
	{
		return StockDataEngine.instance().resetDataRoot(dateRoot);
	}
	
	public void onInitialize(DAContext context)
	{
		CLog.output("QENGINE", "QuantSession.onInitialize");
		
		m_context.setDAContext(context);
		
		if(null != m_stratety)
		{
			m_stratety.onInit(m_context);
		}
	}
	
	public void onUnInitialize(DAContext context)
	{
		CLog.output("QENGINE", "QuantSession.onUnInitialize");
		
		m_context.setDAContext(context);
		
		if(null != m_stratety)
		{
			m_stratety.onUnInit(m_context);
		}
	}
	
	public void onTradingDayStart(DAContext context)
	{
		CLog.output("QENGINE", "[%s %s] QuantSession.onTradingDayStart", context.date(), context.time());
		
		m_context.setDAContext(context);
		
		// update account stock price info
		m_cAccountController.setDateTime(context.date(), context.time());
		m_cAccountController.newDayBegin();
		List<String> ctnHoldStockIDList = new ArrayList<String>();
		m_cAccountController.getHoldStockIDList(ctnHoldStockIDList);
		for(int i=0; i<ctnHoldStockIDList.size(); i++)
		{
			String sHoldStockID = ctnHoldStockIDList.get(i);
			double price = context.pool().get(sHoldStockID).price();
			m_cAccountController.flushCurrentPrice(sHoldStockID, price);
		}
		
		// callback for strategy onDayStart
		m_stratety.onDayStart(m_context);
		
		// for CurrentDayInterestMinuteDataIDs
		List<String> InterestMinuteDataIDs = m_context.getCurrentDayInterestMinuteDataIDs();
		String StrategyInterestIDs = "";
		for(int i=0; i<InterestMinuteDataIDs.size(); i++)
		{
			String StockID = InterestMinuteDataIDs.get(i);
			StrategyInterestIDs += StockID  + " ";
		}
		
		CLog.output("QENGINE", "[%s %s] QuantSession.onTradingDayStart MinuteDataIDs StrategyInterest[%s]", 
				context.date(), context.time(), StrategyInterestIDs);
	}
	
	public void onMinuteTimePrices(DAContext context)
	{
		CLog.output("QENGINE", "[%s %s] QuantSession.onMinuteTimePrices ", context.date(), context.time());
		
		if(null != m_stratety)
		{
			m_context.setDAContext(context);
			
			// update account stock price info
			m_cAccountController.setDateTime(context.date(), context.time());
			List<String> ctnHoldStockIDList = new ArrayList<String>();
			m_cAccountController.getHoldStockIDList(ctnHoldStockIDList);
			for(int i=0; i<ctnHoldStockIDList.size(); i++)
			{
				String sHoldStockID = ctnHoldStockIDList.get(i);
				double price = context.pool().get(sHoldStockID).price();
				m_cAccountController.flushCurrentPrice(sHoldStockID, price);
			}
			
			// callback for strategy onMinuteData
			m_stratety.onMinuteData(m_context);
		}
	}
	
	public void onTradingDayFinish(DAContext context)
	{
		CLog.output("QENGINE", "[%s %s] QuantSession.onTradingDayFinish ", context.date(), context.time());
		
		m_context.setDAContext(context);
		
		// update account stock price info
		m_cAccountController.setDateTime(context.date(), context.time());
		List<String> ctnHoldStockIDList = new ArrayList<String>();
		m_cAccountController.getHoldStockIDList(ctnHoldStockIDList);
		for(int i=0; i<ctnHoldStockIDList.size(); i++)
		{
			String sHoldStockID = ctnHoldStockIDList.get(i);
			double price = context.pool().get(sHoldStockID).price();
			m_cAccountController.flushCurrentPrice(sHoldStockID, price);
		}
	
		// callback for strategy onDayFinish
		m_stratety.onDayFinish(m_context);
		
		m_cAccountController.newDayEnd();
	}
	
	private IEngineListener m_listener;
	private QuantContext m_context;
	private AccountProxy m_accountProxy;
	private AccountController m_cAccountController;
	private QuantStrategy m_stratety;
}
