package pers.di.dataengine_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CFile;
import pers.di.common.CLog;
import pers.di.common.CFileSystem;
import pers.di.common.CSystem;
import pers.di.common.CTest;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.*;
import pers.di.localstock.*;
import pers.di.localstock.common.KLine;
import pers.di.localstock.common.TimePrice;
import pers.di.localstock_test.CommonTestHelper;

public class TestStockDataEngine {
	
	public static int s_testCount_listenerX = 0;
	public static int s_testCount_listenerY = 0;
	
	@CTest.setup
	public static void setup()
	{
		String newestDate = "2017-12-31";
		 List<String> stockIDs = new ArrayList<String>()
			{{add("999999");add("600000");;add("600056");add("300163");add("002468");}};
			CommonTestHelper.InitLocalData(newestDate, stockIDs);
	}

	public static class EngineListenerTesterX extends IEngineListener
	{
		public int testDayCount = 0;
		@Override
		public void onTradingDayFinish(DAContext context)
		{
			String stockID = "300163";
			DAKLines cDAKLines = context.pool().get(stockID).dayKLines();
			if(cDAKLines.size() > 0)
			{
				KLine cCurrentKLine =  cDAKLines.get(cDAKLines.size()-1);
//				CLog.output("TEST", "AllStockCnt:%d ID:%s ALLKLineSize:%d Date:%s Open:%.3f Close:%.3f", 
//						context.pool().size(),
//						stockID,
//						cDAKLines.size(),
//						cCurrentKLine.date,
//						cCurrentKLine.open,
//						cCurrentKLine.close);
				testDayCount++;
				if(cCurrentKLine.date.equals("2017-01-03"))
				{
					CTest.EXPECT_DOUBLE_EQ(cCurrentKLine.open, 10.22, 2);
					s_testCount_listenerX++;
				}
				if(cCurrentKLine.date.equals("2017-02-03"))
				{
					CTest.EXPECT_DOUBLE_EQ(cCurrentKLine.close, 10.12, 2);
					CTest.EXPECT_LONG_EQ(testDayCount, 1466);
					s_testCount_listenerX++;
				}
			}
		}
	}
	
	public static class EngineListenerTesterY extends IEngineListener
	{
		private String stockID = "600056";
		
		@Override
		public void onInitialize(DAContext context)
		{
			s_testCount_listenerY++;
		}
		@Override
		public void onUnInitialize(DAContext context)
		{
			s_testCount_listenerY++;
		}
		@Override
		public void onTradingDayStart(DAContext context)
		{
			if(context.date().equals("2007-11-12"))
			{
				// day start, 
				// only call addCurrentDayInterestMinuteDataID, 
				// you could get min data in onMinuteTimePrices with IF context.pool().get(stockID).timePrices()
				// else context.pool().get(stockID).timePrices().size() == 0
				context.addCurrentDayInterestMinuteDataID(stockID);
				CLog.output("TEST", "EngineListenerTesterY.onTradingDayStart ID:%s Date:%s", 
						stockID,
						context.date());
				s_testCount_listenerY++;
			}
		}
		@Override
		public void onMinuteTimePrices(DAContext context)
		{
			DATimePrices cDATimePrices = context.pool().get(stockID).timePrices();
			if(cDATimePrices.size() > 0) {
				TimePrice cCurrentTimePrice =  cDATimePrices.get(cDATimePrices.size()-1);
				CLog.output("TEST", "EngineListenerTesterY.onDayDataPush ID:%s Time:%s PriceCount:%d Price:%.3f", 
						stockID,
						cCurrentTimePrice.time,
						cDATimePrices.size(),
						cCurrentTimePrice.price);
				
				s_testCount_listenerY++;
			}
		}
		@Override
		public void onTradingDayFinish(DAContext context)
		{
			if(context.date().equals("2004-10-22"))
			{
				String stockID = "600056";
				DAKLines cDAKLines = context.pool().get(stockID).dayKLines();
				if(cDAKLines.size() > 0)
				{
					KLine cCurrentKLine =  cDAKLines.get(cDAKLines.size()-1);
//					CLog.output("TEST", "AllStockCnt:%d ID:%s ALLKLineSize:%d Date:%s O:%.3f C:%.3f L:%.3f H:%.3f", 
//							context.pool().size(),
//							stockID,
//							cDAKLines.size(),
//							cCurrentKLine.date,
//							cCurrentKLine.open,
//							cCurrentKLine.close,
//							cCurrentKLine.low,
//							cCurrentKLine.high);
					CTest.EXPECT_DOUBLE_EQ(cCurrentKLine.low, -1.185, 2);
					s_testCount_listenerY++;
				}
			}
			if(context.date().equals("2007-11-12"))
			{
				String stockID = "600056";
				DAKLines cDAKLines = context.pool().get(stockID).dayKLines();
				if(cDAKLines.size() > 0)
				{
					KLine cCurrentKLine =  cDAKLines.get(cDAKLines.size()-1);
//					CLog.output("TEST", "AllStockCnt:%d ID:%s ALLKLineSize:%d Date:%s O:%.3f C:%.3f L:%.3f H:%.3f", 
//							context.pool().size(),
//							stockID,
//							cDAKLines.size(),
//							cCurrentKLine.date,
//							cCurrentKLine.open,
//							cCurrentKLine.close,
//							cCurrentKLine.low,
//							cCurrentKLine.high);
					CTest.EXPECT_DOUBLE_EQ(cCurrentKLine.open, 4.58, 2);
					CTest.EXPECT_DOUBLE_EQ(cCurrentKLine.close, 4.52, 2);
					s_testCount_listenerY++;
				}
			}
		}
	}
	

	@CTest.test
	public void test_StockDataEngine_History()
	{
		EngineListenerTesterX cEngineListenerTesterX = new EngineListenerTesterX();
		EngineListenerTesterY cEngineListenerTesterY = new EngineListenerTesterY();
		
		//StockDataEngine.instance().config("TriggerMode", "HistoryTest 2004-01-01 2017-02-03");
		StockDataEngine.instance().config("TriggerMode", "HistoryTest 2004-09-01 2017-12-30");
		StockDataEngine.instance().registerListener(cEngineListenerTesterX);
		StockDataEngine.instance().registerListener(cEngineListenerTesterY);
		StockDataEngine.instance().run();
		
		CTest.EXPECT_LONG_EQ(s_testCount_listenerX, 2);
		CTest.EXPECT_LONG_EQ(s_testCount_listenerY, 5 + 242);
	}
	
	
	public static class EngineListenerTesterZ extends IEngineListener
	{
		public void onTradingDayStart(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterZ.onNewDayStart %s %s", context.date(), context.time());
			CLog.output("TEST", "    pool.size %d", context.pool().size());
		}
		
		public void onMinuteTimePrices(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterZ.onDayDataPush %s %s", context.date(), context.time());
		}
		
		public void onTradingDayFinish(DAContext context)
		{
			CLog.output("TEST", "EngineListenerTesterZ.onNewDayFinish %s %s", context.date(), context.time());
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
