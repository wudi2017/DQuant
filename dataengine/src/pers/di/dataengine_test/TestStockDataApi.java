package pers.di.dataengine_test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import pers.di.common.*;
import pers.di.common.CImageCurve.CurvePoint;
import pers.di.dataengine.baseapi.*;
import pers.di.dataengine.*;
import pers.di.dataengine.common.*;

public class TestStockDataApi {
	
	private static String s_workDir = "data";
	private static String s_updateFinish = "updateFinish.txt";
	
	private static String s_daykFile = "dayk.txt";
	private static String s_DividendPayoutFile = "dividendPayout.txt";
	private static String s_BaseInfoFile = "baseInfo.txt";
	
	private static String s_newestDate = "2017-08-10";
	private static List<String> s_stockIDs = new ArrayList<String>()
		{{add("600000");add("300163");add("002468");}};
		
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
		helpTest_InitData(s_newestDate, s_stockIDs);
	}
	
	public static void test_updateAllLocalStocks()
	{
		s_StockDataApi.updateAllLocalStocks("2017-08-15");
	}
	
	@CTest.test
	public static void test_updateLocalStocks()
	{
		String stockID = s_stockIDs.get(0);
		String dateStr = s_newestDate;
		int ret = s_StockDataApi.updateLocalStocks(stockID, dateStr);
		CTest.EXPECT_LONG_EQ(0, ret);
			
		String checkFileName = "";
		checkFileName= s_workDir + "\\" + stockID + "\\" + s_daykFile;
		CTest.EXPECT_TRUE(CPath.isFileExist(checkFileName));
		checkFileName = s_workDir + "\\" + stockID + "\\" + s_DividendPayoutFile;
		CTest.EXPECT_TRUE(CPath.isFileExist(checkFileName));
		checkFileName = s_workDir + "\\" + stockID + "\\" + s_BaseInfoFile;
		CTest.EXPECT_TRUE(CPath.isFileExist(checkFileName));
	}

	@CTest.test
	public static void test_buildAllStockIDObserver()
	{
		CListObserver<String> observer = new CListObserver<String>();
		int error = s_StockDataApi.buildAllStockIDObserver(observer);
		CTest.EXPECT_LONG_EQ(error, 0);
		CTest.EXPECT_LONG_EQ(observer.size(), s_stockIDs.size());
	}
	
	@CTest.test
	public static void test_buildStockInfoObserver()
	{
		String stockID = "600000";
		
		CObjectObserver<StockInfo> observer = new CObjectObserver<StockInfo>();
		int error = s_StockDataApi.buildStockInfoObserver(stockID, observer);
		CTest.EXPECT_LONG_EQ(error, 0);
		CTest.EXPECT_STR_EQ(observer.get().name, "ÆÖ·¢ÒøÐÐ");
	}
	
	@CTest.test
	public static void test_buildDayKLineListObserver()
	{
		String stockID = "600000";

		CListObserver<KLine> obsKLineList = new CListObserver<KLine>();
		int error = s_StockDataApi.buildDayKLineListObserver(stockID, "2011-05-23", "2017-05-25", obsKLineList);
		CLog.output("TEST", "KLine count: %d", obsKLineList.size());
		CTest.EXPECT_LONG_EQ(obsKLineList.size(), 1428);
		int iCheckCnt = 0;
		for(int i=0; i<obsKLineList.size(); i++)
		{
			KLine cKLine = obsKLineList.get(i);
			if(cKLine.date.equals("2011-05-23")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.open, 5.44, 2);
				iCheckCnt++;
			}
			if(cKLine.date.equals("2012-09-27")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.high, 3.26, 2);
				iCheckCnt++;
			}
			if(cKLine.date.equals("2013-06-25")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.low, 3.52, 2);
				iCheckCnt++;
			}
			if(cKLine.date.equals("2017-05-25")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.close, 12.93, 2);
				iCheckCnt++;
			}
			if(cKLine.date.equals("2017-05-26")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.close, 12.93, 2);
				iCheckCnt++;
			}
		}
		CTest.EXPECT_LONG_EQ(iCheckCnt, 4);
	}
	
	@CTest.test
	public static void test_buildMinTimePriceListObserver()
	{
		String stockID = "600000";
		
		CImageCurve cCImageCurve = new CImageCurve(1600,900,"test_buildMinTimePriceListObserver.jpg");
		List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
		
		CListObserver<TimePrice> obsTimePriceList = new CListObserver<TimePrice>();
		int errObsTimePriceList = s_StockDataApi.buildMinTimePriceListObserver(stockID, "2016-04-20", 
				"09:00:00", "14:55:00", obsTimePriceList);
		CTest.EXPECT_LONG_EQ(obsTimePriceList.size(), 237);
		int iCheckCnt = 0;
		for(int i=0; i<obsTimePriceList.size(); i++)
		{
			TimePrice cTimePrice = obsTimePriceList.get(i);
			CLog.output("TEST", "time: %s close: %f", cTimePrice.time, cTimePrice.price);
			if(cTimePrice.time.equals("09:30:00")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 12.00, 2);
				iCheckCnt++;
			}
			if(cTimePrice.time.equals("13:10:00")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 11.63, 2);
				iCheckCnt++;
			}
			if(cTimePrice.time.equals("14:33:00")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 11.83, 2);
				iCheckCnt++;
			}
			if(cTimePrice.time.equals("14:47:00")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 11.94, 2);
				iCheckCnt++;
			}
			if(cTimePrice.time.equals("14:55:00")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 12, 2);
				iCheckCnt++;
			}
			if(cTimePrice.time.equals("14:56:00")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 12, 2);
				iCheckCnt++;
			}
			PoiList.add(new CurvePoint(i,cTimePrice.price));
		}
		
		cCImageCurve.writeLogicCurve(PoiList, 1);
		cCImageCurve.GenerateImage();
		CTest.EXPECT_LONG_EQ(iCheckCnt, 5);
	}
	
	@CTest.test
	public static void test_getRealTimePrice()
	{
		RealTimeInfo ctnRealTimeInfo = new RealTimeInfo();
		int error = s_StockDataApi.loadRealTimeInfo("600000", ctnRealTimeInfo);
		CTest.EXPECT_LONG_EQ(error, 0);
		CTest.EXPECT_TRUE(ctnRealTimeInfo.curPrice>0);
		CTest.EXPECT_TRUE(ctnRealTimeInfo.time.length()==8);
	}
	
	
	public static StockDataApi s_StockDataApi = new StockDataApi("data");
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(TestStockDataApi.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
