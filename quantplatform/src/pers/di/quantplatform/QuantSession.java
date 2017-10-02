package pers.di.quantplatform;
import java.util.*;

import pers.di.accountengine.*;
import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataapi.StockDataApi;
import pers.di.dataapi.common.*;
import pers.di.dataengine.*;
import pers.di.quantplatform.accountproxy.*;

public class QuantSession {
	
	public QuantSession(String triggerCfgStr, Account accout, QuantStrategy strategy)
	{
		// init m_listener and StockDataEngine
		m_listener = StockDataEngine.instance().createListener();
		m_listener.subscribe(EEID.INITIALIZE, this, "onInitialize");
		m_listener.subscribe(EEID.TRADINGDAYSTART, this, "onTradingDayStart");
		m_listener.subscribe(EEID.MINUTETIMEPRICES, this, "onMinuteTimePrices");
		m_listener.subscribe(EEID.TRADINGDAYFINISH, this, "onTradingDayFinish");
		StockDataEngine.instance().config("TriggerMode", triggerCfgStr);

		// init m_accountProxy
		m_accountProxy = new AccountProxy(accout);
		
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
			m_stratety.onMinuteData(m_context);
		}
	}
	
	public void onTradingDayFinish(EEObject ev)
	{
		EETradingDayFinish e = (EETradingDayFinish)ev;
		DAContext ctx = e.ctx;
		
		CLog.output("QEngine", "[%s %s] QuantSession.onTradingDayFinish ", ctx.date(), ctx.time());
		
		m_context.set(ctx.date(), ctx.time(), ctx.pool());
		m_stratety.onDayFinish(m_context);
	}
	
	private EngineListener m_listener;
	private QuantContext m_context;
	private AccountProxy m_accountProxy;
	private QuantStrategy m_stratety;
}
