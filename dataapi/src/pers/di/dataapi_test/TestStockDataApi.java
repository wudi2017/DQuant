package pers.di.dataapi_test;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import pers.di.common.*;
import pers.di.common.CImageCurve.CurvePoint;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.RealTimeInfoLite;
import pers.di.dataapi.common.StockInfo;
import pers.di.dataapi.common.TimePrice;
import pers.di.dataapi.*;
import pers.di.dataapi.*;
import pers.di.dataapi.common.*;

public class TestStockDataApi {
	
	private static String s_updateFinish = "updateFinish.txt";
	
	private static String s_daykFile = "dayk.txt";
	private static String s_DividendPayoutFile = "dividendPayout.txt";
	private static String s_BaseInfoFile = "baseInfo.txt";
	
	private static String s_newestDate = "2017-08-10";
	private static List<String> s_stockIDs = new ArrayList<String>()
		{{add("600000");add("300163");add("002468");}};
	
	@CTest.setup
	public static void setup()
	{
		TestCommonHelper.InitLocalData(s_newestDate, s_stockIDs);
	}
	
//	@CTest.test
//	public static void test_updateAllLocalStocks()
//	{
//		s_StockDataApi.updateAllLocalStocks("2017-08-15");
//	}
	
	@CTest.test
	public static void test_updateLocalStocks()
	{
		String workDir = StockDataApi.instance().dataRoot();
		String stockID = s_stockIDs.get(0);
		String dateStr = s_newestDate;
		int ret = s_StockDataApi.updateLocalStocks(stockID, dateStr);
		CTest.EXPECT_LONG_EQ(0, ret);
			
		String checkFileName = "";
		checkFileName= workDir + "\\" + stockID + "\\" + s_daykFile;
		CTest.EXPECT_TRUE(CFileSystem.isFileExist(checkFileName));
		checkFileName = workDir + "\\" + stockID + "\\" + s_DividendPayoutFile;
		CTest.EXPECT_TRUE(CFileSystem.isFileExist(checkFileName));
		checkFileName = workDir + "\\" + stockID + "\\" + s_BaseInfoFile;
		CTest.EXPECT_TRUE(CFileSystem.isFileExist(checkFileName));
	}

	@CTest.test
	public static void test_buildAllStockIDObserver()
	{
		CListObserver<String> observer = new CListObserver<String>();
		int error = s_StockDataApi.buildAllStockIDObserver(observer);
		CTest.EXPECT_LONG_EQ(error, 0);
		CTest.EXPECT_TRUE(observer.size() >= 3);
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
		int error = s_StockDataApi.buildDayKLineListObserver(stockID, "2011-05-23", "2019-08-31", obsKLineList);
		// CLog.output("TEST", "KLine count: %d", obsKLineList.size());
		CTest.EXPECT_LONG_EQ(obsKLineList.size(), 1983);
		int iCheckCnt = 0;
		for(int i=0; i<obsKLineList.size(); i++)
		{
			KLine cKLine = obsKLineList.get(i);
			if(cKLine.date.equals("2011-05-23")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.open, 4.99, 2);
				iCheckCnt++;
			}
			if(cKLine.date.equals("2012-09-27")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.high, 2.81, 2);
				iCheckCnt++;
			}
			if(cKLine.date.equals("2013-06-25")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.low, 3.07, 2);
				iCheckCnt++;
			}
			if(cKLine.date.equals("2017-05-25")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.close, 12.48, 2);
				iCheckCnt++;
			}
			if(cKLine.date.equals("2017-05-26")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.close, 12.39, 2);
				iCheckCnt++;
			}
			if(cKLine.date.equals("2019-08-30")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.close, 11.28, 2);
				iCheckCnt++;
			}
		}
		CTest.EXPECT_LONG_EQ(iCheckCnt, 6);
	}
	
	//@CTest.test
	public static void test_buildMinTimePriceListObserver()
	{
		// test single
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
				//CLog.output("TEST", "time: %s close: %f", cTimePrice.time, cTimePrice.price);
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
			
			cCImageCurve.setColor(Color.BLACK);
			cCImageCurve.writeLogicCurve(PoiList);
			cCImageCurve.GenerateImage();
			CTest.EXPECT_LONG_EQ(iCheckCnt, 5);
		}
	}
	
	@CTest.test
	public static void test_getRealTimePrice()
	{
		List<RealTimeInfoLite> ctnRealTimeInfos = new ArrayList<RealTimeInfoLite>();
		List<String> stocks = new ArrayList<String>();
		stocks.add("600000");
		int error = s_StockDataApi.loadRealTimeInfo(stocks, ctnRealTimeInfos);
		CTest.EXPECT_LONG_EQ(error, 0);
		CTest.EXPECT_TRUE(ctnRealTimeInfos.get(0).curPrice>0);
		CTest.EXPECT_TRUE(ctnRealTimeInfos.get(0).time.length()==8);
	}
	
	@CTest.test
	public static void test_resetDataRoot()
	{
		String newRootDir = "C:\\temp\\NewRoot1";
		CFileSystem.removeDir(newRootDir);
		CTest.EXPECT_FALSE(CFileSystem.isDirExist(newRootDir));
		s_StockDataApi.resetDataRoot(newRootDir);
		CTest.EXPECT_STR_EQ(s_StockDataApi.dataRoot(), newRootDir);
		CTest.EXPECT_TRUE(CFileSystem.isDirExist(newRootDir));
		
		// init test data
		TestCommonHelper.InitLocalData(s_newestDate, s_stockIDs);
		
		String workDir = newRootDir;
		String stockID = s_stockIDs.get(0);
		String dateStr = s_newestDate;
		int ret = s_StockDataApi.updateLocalStocks(stockID, dateStr);
		CTest.EXPECT_LONG_EQ(0, ret);
			
		String checkFileName = "";
		checkFileName= workDir + "\\" + stockID + "\\" + s_daykFile;
		CTest.EXPECT_TRUE(CFileSystem.isFileExist(checkFileName));
		checkFileName = workDir + "\\" + stockID + "\\" + s_DividendPayoutFile;
		CTest.EXPECT_TRUE(CFileSystem.isFileExist(checkFileName));
		checkFileName = workDir + "\\" + stockID + "\\" + s_BaseInfoFile;
		CTest.EXPECT_TRUE(CFileSystem.isFileExist(checkFileName));
		
	}
	
	
	public static StockDataApi s_StockDataApi = StockDataApi.instance();
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(TestStockDataApi.class);
		CTest.RUN_ALL_TESTS("TestStockDataApi.");
		CSystem.stop();
	}
}
