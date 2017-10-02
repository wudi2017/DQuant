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

	public static class EngineTester
	{
		public EngineTester()
		{
			m_listener = StockDataEngine.instance().createListener();
			m_listener.subscribe(EE_ID.TRADINGDAYSTART, this, "onTradingDayStart");
			m_listener.subscribe(EE_ID.MINUTETIMEPRICES, this, "onMinuteTimePrices");
			m_listener.subscribe(EE_ID.TRADINGDAYFINISH, this, "onTradingDayFinish");
		}
		
		public void onTradingDayStart(EE_Object ev)
		{
			EE_TradingDayStart e = (EE_TradingDayStart)ev;
			DAContext ctx = e.ctx;
			
			CLog.output("TEST", "onNewDayStart %s %s", ctx.date(), ctx.time());
			
			String logstr = "";
			for(int i=0; i<ctx.pool().size(); i++)
			{
				logstr = logstr + " " + ctx.pool().get(i).ID();
			}
			CLog.output("TEST", "    pool.size %d %s", ctx.pool().size(), logstr);
			
			m_listener.addCurrentDayInterestMinuteDataID("600000");
			m_listener.addCurrentDayInterestMinuteDataID("300163");
		}
		
		public void onMinuteTimePrices(EE_Object ev)
		{
			EE_TimePricesData e = (EE_TimePricesData)ev;
			DAContext ctx = e.ctx;
			
			CLog.output("TEST", "onDayDataPush %s %s", ctx.date(), ctx.time());
		
			CLog.output("TEST", "    600000 cDATimePrices size %d", 
					ctx.pool().get("600000").timePrices().size());
			CLog.output("TEST", "    300163 cDATimePrices size %d", 
					ctx.pool().get("300163").timePrices().size());
		}
		
		public void onTradingDayFinish(EE_Object ev)
		{
			EE_TradingDayFinish e = (EE_TradingDayFinish)ev;
			DAContext ctx = e.ctx;
			
			CLog.output("TEST", "onNewDayFinish %s %s", ctx.date(), ctx.time());
			
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
	public void test_StockDataEngine()
	{
		EngineTester cEngineTester = new EngineTester();
		StockDataEngine.instance().config("TriggerMode", "HistoryTest 2017-01-01 2017-01-03");
		StockDataEngine.instance().run();
	}
	
	public static void main(String[] args) {
		CSystem.start();
		//CLog.config_setTag("DataEngine", true);
		//CLog.config_setTag("TEST", true);
		CTest.ADD_TEST(TestStockDataEngine.class);
		CTest.RUN_ALL_TESTS("");
		CSystem.stop();
	}
}
