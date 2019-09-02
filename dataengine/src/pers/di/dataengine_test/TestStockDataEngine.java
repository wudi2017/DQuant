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
import pers.di.dataapi_test.CommonTestHelper;
import pers.di.dataengine.*;
import pers.di.dataapi.*;

public class TestStockDataEngine {
	
	@CTest.setup
	public static void setup()
	{
		String newestDate = "2017-08-10";
		 List<String> stockIDs = new ArrayList<String>()
			{{add("999999");add("600000");add("300163");add("002468");}};
			CommonTestHelper.InitLocalData(newestDate, stockIDs);
	}

	public static class EngineListenerTesterX extends IEngineListener
	{
		@Override
		public void onTradingDayFinish(DAContext context)
		{
			String stockID = "300163";
			DAKLines cDAKLines = context.pool().get(stockID).dayKLines();
			KLine cCurrentKLine =  cDAKLines.get(cDAKLines.size()-1);
			CLog.output("TEST", "AllStockCnt:%d ID:%s ALLKLineSize:%d Date:%s Close:%.3f", 
					context.pool().size(),
					stockID,
					cDAKLines.size(),
					cCurrentKLine.date,
					cCurrentKLine.close);
		}
	}
	
	public static class EngineListenerTesterY extends IEngineListener
	{
		@Override
		public void onInitialize(DAContext context)
		{
		}
		@Override
		public void onUnInitialize(DAContext context)
		{
		}
		@Override
		public void onTradingDayStart(DAContext context)
		{
			if(context.date().equals("2017-01-11"))
			{
				context.addCurrentDayInterestMinuteDataID("002468");
			}
		}
		@Override
		public void onMinuteTimePrices(DAContext context)
		{
			if(context.date().equals("2017-01-11"))
			{
				String stockID = "002468";
				DATimePrices cDATimePrices = context.pool().get(stockID).timePrices();
				TimePrice cCurrentTimePrice =  cDATimePrices.get(cDATimePrices.size()-1);
				CLog.output("TEST", "EngineListenerTesterY.onDayDataPush ID:%s Time:%s PriceCount:%d Price:%.3f", 
						stockID,
						cCurrentTimePrice.time,
						cDATimePrices.size(),
						cCurrentTimePrice.price);
			}
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
	
	
	public static class EngineListenerTesterZ extends IEngineListener
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
