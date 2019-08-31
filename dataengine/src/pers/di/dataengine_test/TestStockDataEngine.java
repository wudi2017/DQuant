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

	public static class EngineListenerTesterX implements IEngineListener
	{
		public void onInitialize(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterX.onInitialize");
		}
		public void onUnInitialize(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterX.onUnInitialize");
		}
		public void onTradingDayStart(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterX.onNewDayStart %s %s", context.date(), context.time());
			
			String logstr = "";
			int xx = context.pool().size();
			CLog.output("TEST", "    pool.size %d", context.pool().size());
			
			context.addCurrentDayInterestMinuteDataID("600000");
			context.addCurrentDayInterestMinuteDataID("300163");
		}
		public void onTradingDayFinish(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterX.onNewDayFinish %s %s", context.date(), context.time());
			CLog.output("TEST", "    pool.size %d", context.pool().size());
		}
		public void onMinuteTimePrices(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterX.onDayDataPush %s %s", context.date(), context.time());
		}
	}
	
	public static class EngineListenerTesterY implements IEngineListener
	{
		public void onInitialize(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterY.onInitialize");
		}
		public void onUnInitialize(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterY.onUnInitialize");
		}
		public void onTradingDayStart(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterY.onNewDayStart %s %s", context.date(), context.time());
			
			//context.addCurrentDayInterestMinuteDataID("600000");
		}
		public void onTradingDayFinish(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterY.onNewDayFinish %s %s", context.date(), context.time());
			CLog.output("TEST", "    pool.size %d", context.pool().size());
		}
		public void onMinuteTimePrices(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterY.onDayDataPush %s %s", context.date(), context.time());
		}
	}
	

	@CTest.test
	public void test_StockDataEngine_History()
	{
		EngineListenerTesterX cEngineListenerTesterX = new EngineListenerTesterX();
		EngineListenerTesterY cEngineListenerTesterY = new EngineListenerTesterY();
		
		StockDataEngine.instance().config("TriggerMode", "HistoryTest 2017-01-01 2017-02-03");
		StockDataEngine.instance().registerListener(cEngineListenerTesterX);
		StockDataEngine.instance().registerListener(cEngineListenerTesterY);
		StockDataEngine.instance().run();
	}
	
	
	public static class EngineListenerTesterZ implements IEngineListener
	{
		public void onInitialize(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterZ.onInitialize");
//			context.addCurrentDayInterestMinuteDataID("600000");
//			context.addCurrentDayInterestMinuteDataID("000002");
		}
		public void onUnInitialize(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterZ.onUnInitialize");
		}
		public void onTradingDayStart(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterZ.onNewDayStart %s %s", context.date(), context.time());
			CLog.output("TEST", "    pool.size %d", context.pool().size());
			
//			context.addCurrentDayInterestMinuteDataID("600000");
//			context.addCurrentDayInterestMinuteDataID("000002");
		}
		
		public void onMinuteTimePrices(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterZ.onDayDataPush %s %s", context.date(), context.time());
		}
		
		public void onTradingDayFinish(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterZ.onNewDayFinish %s %s", context.date(), context.time());
			CLog.output("TEST", "    pool.size %d", context.pool().size());
			
		}
	}
	
	@CTest.test
	public void test_StockDataEngine_RealTime()
	{
		EngineListenerTesterZ cEngineListenerTesterZ = new EngineListenerTesterZ();

		StockDataEngine.instance().config("TriggerMode", "Realtime");
		StockDataEngine.instance().registerListener(cEngineListenerTesterZ);
		StockDataEngine.instance().run();
	}
	
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(TestStockDataEngine.class);
		CTest.RUN_ALL_TESTS("TestStockDataEngine.test_StockDataEngine_History");
		CSystem.stop();
	}
}
