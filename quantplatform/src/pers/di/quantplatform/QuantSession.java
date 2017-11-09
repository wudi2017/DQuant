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
	
	public static boolean run()
	{
		CLog.output("QuantSession", "The QuantSession is running now...");
		StockDataEngine.instance().run();
		return true;
	}
	
	public void onInitialize(EEObject ev)
	{
		CLog.output("QEngine", "QuantSession.onInitialize");
		
		if(null != m_stratety)
		{
			m_stratety.onInit(m_context);
		}
	}
	
	public void onTradingDayStart(EEObject ev)
	{
		EETradingDayStart e = (EETradingDayStart)ev;
		DAContext ctx = e.ctx;
		
		CLog.output("QEngine", "[%s %s] QuantSession.onTradingDayStart", ctx.date(), ctx.time());
		
		m_context.set(ctx.date(), ctx.time(), ctx.pool());
		
		// for account holdstock InterestMinuteDataID
		m_accountDriver.setDateTime(ctx.date(), ctx.time());
		m_accountDriver.newDayBegin();
		List<String> ctnHoldStockIDList = new ArrayList<String>();
		m_accountDriver.getHoldStockList(ctnHoldStockIDList);
		m_listener.addCurrentDayInterestMinuteDataIDs(ctnHoldStockIDList);
		for(int i=0; i<ctnHoldStockIDList.size(); i++)
		{
			String sHoldStockID = ctnHoldStockIDList.get(i);
			double price = ctx.pool().get(sHoldStockID).price();
			m_accountDriver.flushCurrentPrice(sHoldStockID, price);
		}
		
		// for stratety InterestMinuteDataID
		m_stratety.onDayStart(m_context);
		m_listener.addCurrentDayInterestMinuteDataIDs(m_stratety.getCurrentDayInterestMinuteDataIDs());
	}
	
	public void onMinuteTimePrices(EEObject ev)
	{
		EETimePricesData e = (EETimePricesData)ev;
		DAContext ctx = e.ctx;
		
		CLog.output("QEngine", "[%s %s] QuantSession.onMinuteTimePrices ", ctx.date(), ctx.time());
		
		if(null != m_stratety)
		{
			m_context.set(ctx.date(), ctx.time(), ctx.pool());
			
			// for account holdstock
			m_accountDriver.setDateTime(ctx.date(), ctx.time());
			List<String> ctnHoldStockIDList = new ArrayList<String>();
			m_accountDriver.getHoldStockList(ctnHoldStockIDList);
			for(int i=0; i<ctnHoldStockIDList.size(); i++)
			{
				String sHoldStockID = ctnHoldStockIDList.get(i);
				double price = ctx.pool().get(sHoldStockID).price();
				m_accountDriver.flushCurrentPrice(sHoldStockID, price);
			}
			
			m_stratety.clearCurrentDayInterestMinuteDataIDs();
			m_stratety.onMinuteData(m_context);
		}
	}
	
	public void onTradingDayFinish(EEObject ev)
	{
		EETradingDayFinish e = (EETradingDayFinish)ev;
		DAContext ctx = e.ctx;
		
		CLog.output("QEngine", "[%s %s] QuantSession.onTradingDayFinish ", ctx.date(), ctx.time());
		
		m_context.set(ctx.date(), ctx.time(), ctx.pool());
		
		// for account holdstock
		m_accountDriver.setDateTime(ctx.date(), ctx.time());
		List<String> ctnHoldStockIDList = new ArrayList<String>();
		m_accountDriver.getHoldStockList(ctnHoldStockIDList);
		for(int i=0; i<ctnHoldStockIDList.size(); i++)
		{
			String sHoldStockID = ctnHoldStockIDList.get(i);
			double price = ctx.pool().get(sHoldStockID).price();
			m_accountDriver.flushCurrentPrice(sHoldStockID, price);
		}
	
		m_stratety.onDayFinish(m_context);
		
		m_accountDriver.newDayEnd();
	}
	
	private EngineListener m_listener;
	private QuantContext m_context;
	private AccountProxy m_accountProxy;
	private AccoutDriver m_accountDriver;
	private QuantStrategy m_stratety;
}
