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
	
	public QuantSession(String triggerCfgStr, AccoutDriver accoutDriver, QuantStrategy strategy)
	{
		// init m_listener and StockDataEngine
		m_listener = StockDataEngine.instance().createListener();
		m_listener.subscribe(EEID.INITIALIZE, this, "onInitialize");
		m_listener.subscribe(EEID.UNINITIALIZE, this, "onUnInitialize");
		m_listener.subscribe(EEID.TRADINGDAYSTART, this, "onTradingDayStart");
		m_listener.subscribe(EEID.MINUTETIMEPRICES, this, "onMinuteTimePrices");
		m_listener.subscribe(EEID.TRADINGDAYFINISH, this, "onTradingDayFinish");
		StockDataEngine.instance().config("TriggerMode", triggerCfgStr);

		// init m_accountDriver m_accountProxy
		m_accountDriver = accoutDriver;
		m_accountProxy = new AccountProxy(accoutDriver);
		
		// init m_stratety
		m_stratety = strategy;
		
		// init default object
		if(null == m_context)
		{
			m_context = new QuantContext(m_accountProxy);
		}
	}
	
	/*
	 * ��������·��
	 */
	public boolean resetDataRoot(String dateRoot)
	{
		return StockDataEngine.instance().resetDataRoot(dateRoot);
	}
	
	public boolean run()
	{
		CLog.output("QENGINE", "The QuantSession is running now...");
		StockDataEngine.instance().run();
		StockDataEngine.instance().clearListener(m_listener);
		return true;
	}
	
	public void onInitialize(EEObject ev)
	{
		CLog.output("QENGINE", "QuantSession.onInitialize");
		
		if(null != m_stratety)
		{
			m_stratety.onInit(m_context);
		}
	}
	
	public void onUnInitialize(EEObject ev)
	{
		CLog.output("QENGINE", "QuantSession.onUnInitialize");
		
		if(null != m_stratety)
		{
			m_stratety.onUnInit(m_context);
		}
	}
	
	public void onTradingDayStart(EEObject ev)
	{
		EETradingDayStart e = (EETradingDayStart)ev;
		DAContext ctx = e.ctx;
		
		CLog.output("QENGINE", "[%s %s] QuantSession.onTradingDayStart", ctx.date(), ctx.time());
		
		m_context.set(ctx.date(), ctx.time(), ctx.pool());
		
		// update account stock price info
		m_accountDriver.setDateTime(ctx.date(), ctx.time());
		m_accountDriver.newDayBegin();
		List<String> ctnHoldStockIDList = new ArrayList<String>();
		m_accountDriver.getHoldStockIDList(ctnHoldStockIDList);
		for(int i=0; i<ctnHoldStockIDList.size(); i++)
		{
			String sHoldStockID = ctnHoldStockIDList.get(i);
			double price = ctx.pool().get(sHoldStockID).price();
			m_accountDriver.flushCurrentPrice(sHoldStockID, price);
		}
		
		// callback for strategy onDayStart
		m_stratety.onDayStart(m_context);
		
		// for CurrentDayInterestMinuteDataIDs
		m_listener.addCurrentDayInterestMinuteDataIDs(m_stratety.getCurrentDayInterestMinuteDataIDs());
		String StrategyInterestIDs = "";
		for(int i=0; i<m_stratety.getCurrentDayInterestMinuteDataIDs().size(); i++)
		{
			String StockID = m_stratety.getCurrentDayInterestMinuteDataIDs().get(i);
			StrategyInterestIDs += StockID  + " ";
		}
		
		CLog.output("QENGINE", "[%s %s] QuantSession.onTradingDayStart MinuteDataIDs StrategyInterest[%s]", 
				ctx.date(), ctx.time(), StrategyInterestIDs);
	}
	
	public void onMinuteTimePrices(EEObject ev)
	{
		EETimePricesData e = (EETimePricesData)ev;
		DAContext ctx = e.ctx;
		
		CLog.output("QENGINE", "[%s %s] QuantSession.onMinuteTimePrices ", ctx.date(), ctx.time());
		
		if(null != m_stratety)
		{
			m_context.set(ctx.date(), ctx.time(), ctx.pool());
			
			// update account stock price info
			m_accountDriver.setDateTime(ctx.date(), ctx.time());
			List<String> ctnHoldStockIDList = new ArrayList<String>();
			m_accountDriver.getHoldStockIDList(ctnHoldStockIDList);
			for(int i=0; i<ctnHoldStockIDList.size(); i++)
			{
				String sHoldStockID = ctnHoldStockIDList.get(i);
				double price = ctx.pool().get(sHoldStockID).price();
				m_accountDriver.flushCurrentPrice(sHoldStockID, price);
			}
			
			m_stratety.clearCurrentDayInterestMinuteDataIDs();
			
			// callback for strategy onMinuteData
			m_stratety.onMinuteData(m_context);
		}
	}
	
	public void onTradingDayFinish(EEObject ev)
	{
		EETradingDayFinish e = (EETradingDayFinish)ev;
		DAContext ctx = e.ctx;
		
		CLog.output("QENGINE", "[%s %s] QuantSession.onTradingDayFinish ", ctx.date(), ctx.time());
		
		m_context.set(ctx.date(), ctx.time(), ctx.pool());
		
		// update account stock price info
		m_accountDriver.setDateTime(ctx.date(), ctx.time());
		List<String> ctnHoldStockIDList = new ArrayList<String>();
		m_accountDriver.getHoldStockIDList(ctnHoldStockIDList);
		for(int i=0; i<ctnHoldStockIDList.size(); i++)
		{
			String sHoldStockID = ctnHoldStockIDList.get(i);
			double price = ctx.pool().get(sHoldStockID).price();
			m_accountDriver.flushCurrentPrice(sHoldStockID, price);
		}
	
		// callback for strategy onDayFinish
		m_stratety.onDayFinish(m_context);
		
		m_accountDriver.newDayEnd();
	}
	
	private EngineListener m_listener;
	private QuantContext m_context;
	private AccountProxy m_accountProxy;
	private AccoutDriver m_accountDriver;
	private QuantStrategy m_stratety;
}
