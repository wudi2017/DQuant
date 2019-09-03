package pers.di.quantplatform;
import java.util.*;

import pers.di.account.*;
import pers.di.account.common.HoldStock;
import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CTest;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataapi.StockDataApi;
import pers.di.dataapi.common.*;
import pers.di.dataengine.*;

public class QuantSession {
	
	public static class QuantListener extends IEngineListener
	{
		public QuantListener(QuantSession qs)
		{
			m_QuantSession = qs;
		}
		public void onInitialize(DAContext context){
			m_QuantSession.onInitialize(context);
		}
		public void onUnInitialize(DAContext context){
			m_QuantSession.onUnInitialize(context);
		};
		public void onTradingDayStart(DAContext context){
			m_QuantSession.onTradingDayStart(context);
		};
		public void onTradingDayFinish(DAContext context){
			m_QuantSession.onTradingDayFinish(context);
		};
		public void onMinuteTimePrices(DAContext context){
			m_QuantSession.onMinuteTimePrices(context);
		};
		public QuantSession m_QuantSession;
	}
	
	public QuantSession(String triggerCfgStr, AccoutDriver accoutDriver, QuantStrategy strategy)
	{
		// init m_listener and StockDataEngine
		m_listener = new QuantListener(this);
		StockDataEngine.instance().registerListener(m_listener);
		StockDataEngine.instance().config("TriggerMode", triggerCfgStr);

		// init m_accountDriver m_accountProxy
		m_accountDriver = accoutDriver;
		m_accountProxy = new AccountProxy(accoutDriver);
		
		// init m_stratety
		m_stratety = strategy;

		// init default object
		if(null == m_context)
		{
			m_context = new QuantContext();
			m_context.setAccountProxy(m_accountProxy);
		}
	}
	
	/*
	 * 重置数据路径
	 */
	public boolean resetDataRoot(String dateRoot)
	{
		return StockDataEngine.instance().resetDataRoot(dateRoot);
	}
	
	public boolean run()
	{
		CLog.output("QENGINE", "The QuantSession is running now...");
		StockDataEngine.instance().run();
		StockDataEngine.instance().unRegisterListener(m_listener);
		return true;
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
		m_accountDriver.setDateTime(context.date(), context.time());
		m_accountDriver.newDayBegin();
		List<String> ctnHoldStockIDList = new ArrayList<String>();
		m_accountDriver.getHoldStockIDList(ctnHoldStockIDList);
		for(int i=0; i<ctnHoldStockIDList.size(); i++)
		{
			String sHoldStockID = ctnHoldStockIDList.get(i);
			double price = context.pool().get(sHoldStockID).price();
			m_accountDriver.flushCurrentPrice(sHoldStockID, price);
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
			m_accountDriver.setDateTime(context.date(), context.time());
			List<String> ctnHoldStockIDList = new ArrayList<String>();
			m_accountDriver.getHoldStockIDList(ctnHoldStockIDList);
			for(int i=0; i<ctnHoldStockIDList.size(); i++)
			{
				String sHoldStockID = ctnHoldStockIDList.get(i);
				double price = context.pool().get(sHoldStockID).price();
				m_accountDriver.flushCurrentPrice(sHoldStockID, price);
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
		m_accountDriver.setDateTime(context.date(), context.time());
		List<String> ctnHoldStockIDList = new ArrayList<String>();
		m_accountDriver.getHoldStockIDList(ctnHoldStockIDList);
		for(int i=0; i<ctnHoldStockIDList.size(); i++)
		{
			String sHoldStockID = ctnHoldStockIDList.get(i);
			double price = context.pool().get(sHoldStockID).price();
			m_accountDriver.flushCurrentPrice(sHoldStockID, price);
		}
	
		// callback for strategy onDayFinish
		m_stratety.onDayFinish(m_context);
		
		m_accountDriver.newDayEnd();
	}
	
	private IEngineListener m_listener;
	private QuantContext m_context;
	private AccountProxy m_accountProxy;
	private AccoutDriver m_accountDriver;
	private QuantStrategy m_stratety;
}
