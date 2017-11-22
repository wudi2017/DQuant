package pers.di.dataengine_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CFile;
import pers.di.common.CLog;
import pers.di.common.CFileSystem;
import pers.di.common.CSystem;
import pers.di.common.CTest;
import pers.di.common.CUtilsDateTime;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.TimePrice;
import pers.di.dataapi_test.TestCommonHelper;
import pers.di.dataengine.*;
import pers.di.dataapi.*;

public class TestStockDataEngine {
	
	@CTest.setup
	public static void setup()
	{
		String newestDate = "2017-08-10";
		 List<String> stockIDs = new ArrayList<String>()
			{{add("999999");add("600000");add("300163");add("002468");}};
		TestCommonHelper.InitLocalData(newestDate, stockIDs);
	}

	public static class EngineListenerTesterX
	{
		public EngineListenerTesterX()
		{
			m_listener = StockDataEngine.instance().createListener();
			m_listener.subscribe(EEID.INITIALIZE, this, "onInitialize");
			m_listener.subscribe(EEID.TRADINGDAYSTART, this, "onTradingDayStart");
			m_listener.subscribe(EEID.MINUTETIMEPRICES, this, "onMinuteTimePrices");
			m_listener.subscribe(EEID.TRADINGDAYFINISH, this, "onTradingDayFinish");
		}
		
		public void onInitialize(EEObject ev)
		{
			EEInitialize e = (EEInitialize)ev;
			CLog.output("TEST", "EngineListenerTesterX.onInitialize");
		}
		public void onTradingDayStart(EEObject ev)
		{
			EETradingDayStart e = (EETradingDayStart)ev;
			DAContext ctx = e.ctx;
			
			CLog.output("TEST", "EngineListenerTesterX.onNewDayStart %s %s", ctx.date(), ctx.time());
			
			String logstr = "";
			for(int i=0; i<ctx.pool().size(); i++)
			{
				logstr = logstr + " " + ctx.pool().get(i).ID();
			}
			CLog.output("TEST", "    pool.size %d %s", ctx.pool().size(), logstr);
			
			m_listener.addCurrentDayInterestMinuteDataID("600000");
			m_listener.addCurrentDayInterestMinuteDataID("300163");
		}
		
		public void onMinuteTimePrices(EEObject ev)
		{
			EETimePricesData e = (EETimePricesData)ev;
			DAContext ctx = e.ctx;
			
			CLog.output("TEST", "EngineListenerTesterX.onDayDataPush %s %s", ctx.date(), ctx.time());
		
			CLog.output("TEST", "    600000 cDATimePrices size %d", 
					ctx.pool().get("600000").timePrices().size());
			CLog.output("TEST", "    300163 cDATimePrices size %d", 
					ctx.pool().get("300163").timePrices().size());
			CLog.output("TEST", "    002468 cDATimePrices size %d", 
					ctx.pool().get("002468").timePrices().size());
		}
		
		public void onTradingDayFinish(EEObject ev)
		{
			EETradingDayFinish e = (EETradingDayFinish)ev;
			DAContext ctx = e.ctx;
			
			CLog.output("TEST", "EngineListenerTesterX.onNewDayFinish %s %s", ctx.date(), ctx.time());
			
			String logstr = "";
			for(int i=0; i<ctx.pool().size(); i++)
			{
				logstr = logstr + " " + ctx.pool().get(i).ID();
			}
			CLog.output("TEST", "    pool.size %d %s", ctx.pool().size(), logstr);
			
		}

		private EngineListener m_listener;
	}
	
	public static class EngineListenerTesterY
	{
		public EngineListenerTesterY()
		{
			m_listener = StockDataEngine.instance().createListener();
			m_listener.subscribe(EEID.TRADINGDAYSTART, this, "onTradingDayStart");
			m_listener.subscribe(EEID.MINUTETIMEPRICES, this, "onMinuteTimePrices");
			m_listener.subscribe(EEID.TRADINGDAYFINISH, this, "onTradingDayFinish");
		}
		
		public void onTradingDayStart(EEObject ev)
		{
			EETradingDayStart e = (EETradingDayStart)ev;
			DAContext ctx = e.ctx;
			CLog.output("TEST", "EngineListenerTesterY.onNewDayStart %s %s", ctx.date(), ctx.time());
			
			m_listener.addCurrentDayInterestMinuteDataID("600000");
		}
		
		public void onMinuteTimePrices(EEObject ev)
		{
			EETimePricesData e = (EETimePricesData)ev;
			DAContext ctx = e.ctx;
			
			CLog.output("TEST", "EngineListenerTesterY.onDayDataPush %s %s", ctx.date(), ctx.time());
		
			CLog.output("TEST", "    600000 cDATimePrices size %d", 
					ctx.pool().get("600000").timePrices().size());
			CLog.output("TEST", "    300163 cDATimePrices size %d", 
					ctx.pool().get("300163").timePrices().size());
			CLog.output("TEST", "    002468 cDATimePrices size %d", 
					ctx.pool().get("002468").timePrices().size());
		}
		
		public void onTradingDayFinish(EEObject ev)
		{
			EETradingDayFinish e = (EETradingDayFinish)ev;
			DAContext ctx = e.ctx;
			
			CLog.output("TEST", "EngineListenerTesterY.onNewDayFinish %s %s", ctx.date(), ctx.time());
			
			String logstr = "";
			for(int i=0; i<ctx.pool().size(); i++)
			{
				logstr = logstr + " " + ctx.pool().get(i).ID();
			}
			CLog.output("TEST", "    pool.size %d %s", ctx.pool().size(), logstr);
			
		}

		private EngineListener m_listener;
	}
	

	@CTest.test
	public void test_StockDataEngine_History()
	{
		EngineListenerTesterX cEngineListenerTesterX = new EngineListenerTesterX();
		EngineListenerTesterY cEngineListenerTesterY = new EngineListenerTesterY();
		
		StockDataEngine.instance().config("TriggerMode", "HistoryTest 2017-01-01 2017-02-03");
		StockDataEngine.instance().run();
	}
	
	
	public static class EngineListenerTesterZ
	{
		public EngineListenerTesterZ()
		{
			m_listener = StockDataEngine.instance().createListener();
			m_listener.subscribe(EEID.INITIALIZE, this, "onInitialize");
			m_listener.subscribe(EEID.TRADINGDAYSTART, this, "onTradingDayStart");
			m_listener.subscribe(EEID.MINUTETIMEPRICES, this, "onMinuteTimePrices");
			m_listener.subscribe(EEID.TRADINGDAYFINISH, this, "onTradingDayFinish");
		}
		
		public void onInitialize(EEObject ev)
		{
			EEInitialize e = (EEInitialize)ev;
			CLog.output("TEST", "EngineListenerTesterZ.onInitialize");
			
			m_listener.addCurrentDayInterestMinuteDataID("600000");
			m_listener.addCurrentDayInterestMinuteDataID("300163");
		}
		public void onTradingDayStart(EEObject ev)
		{
			EETradingDayStart e = (EETradingDayStart)ev;
			DAContext ctx = e.ctx;
			
			CLog.output("TEST", "EngineListenerTesterZ.onNewDayStart %s %s", ctx.date(), ctx.time());
			
			String logstr = "";
			for(int i=0; i<ctx.pool().size(); i++)
			{
				logstr = logstr + " " + ctx.pool().get(i).ID();
			}
			CLog.output("TEST", "    pool.size %d %s", ctx.pool().size(), logstr);
			
			m_listener.addCurrentDayInterestMinuteDataID("600000");
			m_listener.addCurrentDayInterestMinuteDataID("300163");
		}
		
		public void onMinuteTimePrices(EEObject ev)
		{
			EETimePricesData e = (EETimePricesData)ev;
			DAContext ctx = e.ctx;
			
			CLog.output("TEST", "EngineListenerTesterX.onDayDataPush %s %s", ctx.date(), ctx.time());
		
			CLog.output("TEST", "    600000 cDATimePrices size %d", 
					ctx.pool().get("600000").timePrices().size());
			CLog.output("TEST", "    300163 cDATimePrices size %d", 
					ctx.pool().get("300163").timePrices().size());
			CLog.output("TEST", "    002468 cDATimePrices size %d", 
					ctx.pool().get("002468").timePrices().size());
		}
		
		public void onTradingDayFinish(EEObject ev)
		{
			EETradingDayFinish e = (EETradingDayFinish)ev;
			DAContext ctx = e.ctx;
			
			CLog.output("TEST", "EngineListenerTesterZ.onNewDayFinish %s %s", ctx.date(), ctx.time());
			
			String logstr = "";
			for(int i=0; i<ctx.pool().size(); i++)
			{
				logstr = logstr + " " + ctx.pool().get(i).ID();
			}
			CLog.output("TEST", "    pool.size %d %s", ctx.pool().size(), logstr);
			
		}

		private EngineListener m_listener;
	}
	
	@CTest.test
	public void test_StockDataEngine_RealTime()
	{
		EngineListenerTesterZ cEngineListenerTesterZ = new EngineListenerTesterZ();

		StockDataEngine.instance().config("TriggerMode", "Realtime");
		StockDataEngine.instance().run();
	}
	
	public static void main(String[] args) {
		CSystem.start();
		//CLog.config_setTag("DataEngine", true);
		//CLog.config_setTag("TEST", false);
		CTest.ADD_TEST(TestStockDataEngine.class);
		CTest.RUN_ALL_TESTS("TestStockDataEngine.test_StockDataEngine_RealTime");
		CSystem.stop();
	}
}
