package pers.di.dataapi_test;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import pers.di.common.*;
import pers.di.common.CImageCurve.CurvePoint;
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
		{{add("600056");add("600000");add("300163");}};
	
	@CTest.setup
	public static void setup()
	{
		CommonTestHelper.InitLocalData(s_newestDate, s_stockIDs);
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
		String stockID = "600056";

		CListObserver<KLine> obsKLineList = new CListObserver<KLine>();
		int error = s_StockDataApi.buildDayKLineListObserver(stockID, "1998-01-01", "2019-08-31", obsKLineList);
		// CLog.output("TEST", "KLine count: %d", obsKLineList.size());
		CTest.EXPECT_LONG_EQ(obsKLineList.size(), 5042);
		int iCheckCnt = 0;
		for(int i=0; i<obsKLineList.size(); i++)
		{
			KLine cKLine = obsKLineList.get(i);
			if(0==i) 
			{
				//CLog.output("TEST", "begindate:%s", cKLine.date);
			}
			if(cKLine.date.equals("2004-10-12")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.open, -0.95, 2);
				iCheckCnt++;
			}
			if(cKLine.date.equals("2008-01-02")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.open, 7.43, 2);
				iCheckCnt++;
			}
			if(cKLine.date.equals("2013-06-25")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.low, 5.97, 2);
				iCheckCnt++;
			}
			if(cKLine.date.equals("2017-11-07")) 
			{
				CTest.EXPECT_DOUBLE_EQ(cKLine.high, 28.46, 2);
				iCheckCnt++;
			}
		}
		CTest.EXPECT_LONG_EQ(iCheckCnt, 4);
	}
	
	@CTest.test
	public static void test_buildMinTimePriceListObserver()
	{
		// test single
		{
			String stockID = "600000";
			
//			List<DividendPayout> container = new ArrayList<DividendPayout>();
//			int error = WebStockAPILayer.getDividendPayout(stockID, container);
//			if(0 == error)
//			{
//				for(int i = 0; i < container.size(); i++)  
//		        {  
//					DividendPayout cDividendPayout = container.get(i);  
//					CLog.output("TEST", String.format("%s %.1f %.1f %.1f",
//							cDividendPayout.date,
//							cDividendPayout.songGu,
//							cDividendPayout.zhuanGu,
//							cDividendPayout.paiXi));
//		        } 
//			}
//			
//			List<KLine> ctnKLine = new ArrayList<KLine>();
//			error = WebStockAPILayer.getKLine(stockID, "20170512", "20170512", ctnKLine);
//			if(0 == error)
//			{
//				KLine cKLine = ctnKLine.get(0);
//				CLog.output("TEST", "WebOrigin DayK date:%s O:%.3f C:%.3f H:%.3f L:%.3f", cKLine.date, cKLine.open, cKLine.close, cKLine.high, cKLine.low);
//			}
//			
//			CListObserver<KLine> obsKLineList = new CListObserver<KLine>();
//			error = s_StockDataApi.buildDayKLineListObserver(stockID, "2017-05-12", "2017-05-12", obsKLineList);
//			if(0 == error)
//			{
//				KLine cKLine = obsKLineList.get(0);
//				CLog.output("TEST", "AdjustPre DayK date:%s O:%.3f C:%.3f H:%.3f L:%.3f", cKLine.date, cKLine.open, cKLine.close, cKLine.high, cKLine.low);  
//			}
			
				
			CImageCurve cCImageCurve = new CImageCurve(1600,900,"test_buildMinTimePriceListObserver.jpg");
			List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
			
			CListObserver<TimePrice> obsTimePriceList = new CListObserver<TimePrice>();
			int errObsTimePriceList = s_StockDataApi.buildMinTimePriceListObserver(stockID, "2017-05-12", 
					"09:25:00", "15:00:00", obsTimePriceList);
			CTest.EXPECT_LONG_EQ(obsTimePriceList.size(), 242);
			int iCheckCnt = 0;
			for(int i=0; i<obsTimePriceList.size(); i++)
			{
				TimePrice cTimePrice = obsTimePriceList.get(i);
//				if(i < 5)
//				{
//					CLog.output("TEST", "time: %s close: %f", cTimePrice.time, cTimePrice.price);
//				}
//				if(i > obsTimePriceList.size()-5)
//				{
//					CLog.output("TEST", "time: %s close: %f", cTimePrice.time, cTimePrice.price);
//				}

				
				if(cTimePrice.time.equals("09:26:00")) 
				{
					CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 12.00, 2);
					iCheckCnt++;
				}
				if(cTimePrice.time.equals("09:30:00")) 
				{
					CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 10.74, 2);
					iCheckCnt++;
				}
				if(cTimePrice.time.equals("15:00:00")) 
				{
					CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 11.08, 2);
					iCheckCnt++;
				}
				PoiList.add(new CurvePoint(i,cTimePrice.price));
			}
			
			cCImageCurve.setColor(Color.BLACK);
			cCImageCurve.writeLogicCurve(PoiList);
			cCImageCurve.GenerateImage();
			
			
			CTest.EXPECT_LONG_EQ(iCheckCnt, 2);
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
		CommonTestHelper.InitLocalData(s_newestDate, s_stockIDs);
		
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
