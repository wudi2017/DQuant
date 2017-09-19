package pers.di.dataengine_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CFile;
import pers.di.common.CLog;
import pers.di.common.CPath;
import pers.di.common.CSystem;
import pers.di.common.CTest;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.DataContext;
import pers.di.dataengine.DataListener;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.baseapi.StockDataApi;
import pers.di.dataengine.common.KLine;
import pers.di.dataengine.common.TimePrice;

public class TestStockDataEngine {
	
	private static String s_workDir = "data";
	public static StockDataApi s_StockDataApi = StockDataApi.instance();
	private static String s_updateFinish = "updateFinish.txt";
	private static String s_newestDate = "2017-08-10";
	private static List<String> s_stockIDs = new ArrayList<String>()
		{{add("999999");add("300163");add("002468");}};
		
	private static void helpTest_InitData(String newestDate, List<String> stockIDs)
	{
		CPath.removeDir(s_workDir);
		CTest.EXPECT_TRUE(!CPath.isDirExist(s_workDir));
		CPath.createDir(s_workDir);
		CTest.EXPECT_TRUE(CPath.isDirExist(s_workDir));
		
		String fileName = s_workDir + "\\" + s_updateFinish;
		String tmpDate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(newestDate, -5);
		CFile.fileWrite(fileName, tmpDate, false);
		CTest.EXPECT_TRUE(CPath.isFileExist(fileName));
		
		for(int i=0; i<stockIDs.size();i++)
		{
			String stockID = stockIDs.get(i);
			int ret = s_StockDataApi.updateLocalStocks(stockID, newestDate);
			CTest.EXPECT_LONG_EQ(0, ret);
		}

		CFile.fileWrite(fileName, newestDate, false);
		CTest.EXPECT_STR_EQ(newestDate, CFile.fileRead(fileName));
		CTest.EXPECT_TRUE(CPath.isFileExist(fileName));
	}
	
	@CTest.setup
	public static void setup()
	{
		//helpTest_InitData(s_newestDate, s_stockIDs);
	}
	
	public static class TestDataListener extends DataListener
	{

		@Override
		public void onDayBegin(DataContext ctx) {
			//CLog.output("TEST", "TestDataListener.onDayBegin");
			onDayBeginCalled++;
		}

		@Override
		public void onTransactionEveryMinute(DataContext ctx) {
			//CLog.output("TEST", "TestDataListener.onTransactionEveryMinute");
			onEveryMinuteCalled++;
		}

		@Override
		public void onDayEnd(DataContext ctx) {
			//CLog.output("TEST", "TestDataListener.onDayEnd");
			onDayEndCalled++;
		}
		
	}
	
	public static int onDayBeginCalled = 0;
	public static int onDayEndCalled = 0;
	public static int onEveryMinuteCalled = 0;
	public static int onTimePricesCheckCount = 0;
	
	@CTest.test
	public static void test_StockDataEngine()
	{
		StockDataEngine.instance().config("ListenMode", "HistoryTest 2017-01-01 2017-01-03");
		StockDataEngine.instance().registerListener(new TestDataListener());
		StockDataEngine.instance().run();
	}
	
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(TestStockDataEngine.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
