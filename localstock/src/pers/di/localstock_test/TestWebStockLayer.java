package pers.di.localstock_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.webstock.WebStock;
import pers.di.common.CSystem;
import pers.di.common.CTest;
import pers.di.localstock.common.*;
import pers.di.localstock.impl.WebStockLayer;

public class TestWebStockLayer {
	
	@CTest.test
	public void test_getAllStockList() {
		
		for(int iTime=0; iTime<2; iTime++)
		{
			List<StockItem> ctnStockItem = new ArrayList<StockItem>();
			int error = WebStockLayer.getAllStockList(ctnStockItem);
			if(0 == error)
			{
				for(int i = 0; i < ctnStockItem.size(); i++)  
		        {  
					StockItem cStockItem = ctnStockItem.get(i);  
		            // System.out.println(cStockItem.name + "," + cStockItem.id);  
		        } 
				System.out.println("count:" + ctnStockItem.size()); 
			}
			else
			{
				System.out.println("ERROR:" + error);
			}
			CTest.EXPECT_LONG_EQ(error, 0);
			CTest.EXPECT_TRUE(ctnStockItem.size() > 3000);
		}
	}
	
	@CTest.test
	public void test_getStockInfo() {
		String stockID = "000488";
		StockInfo ctnStockInfo = new StockInfo();
		int error = WebStockLayer.getStockInfo(stockID, ctnStockInfo);
		if(0 == error)
		{ 
			System.out.println(ctnStockInfo.name);
			System.out.println(ctnStockInfo.date);
			System.out.println(ctnStockInfo.time);
			System.out.println(ctnStockInfo.allMarketValue);
			System.out.println(ctnStockInfo.circulatedMarketValue);
			System.out.println(ctnStockInfo.peRatio);
		}
		else
		{
			System.out.println("ERROR:" + error);
		}
		CTest.EXPECT_LONG_EQ(error, 0);
		CTest.EXPECT_TRUE(ctnStockInfo.name.length() > 2);
		CTest.EXPECT_TRUE(ctnStockInfo.date.length() == 10);
		CTest.EXPECT_TRUE(ctnStockInfo.time.length() == 8);
	}
	
	@CTest.test
	public void test_getDividendPayout()
	{
		String StockID = "000488";
		List<DividendPayout> container = new ArrayList<DividendPayout>();
		int error = WebStockLayer.getDividendPayout(StockID, container);
		if(0 == error)
		{
			System.out.println(String.format("DataWebStockDividendPayout.getDividendPayout %s OK!", StockID));
			for(int i = 0; i < container.size(); i++)  
	        {  
				DividendPayout cDividendPayout = container.get(i);  
				System.out.println(String.format("%s %.1f %.1f %.1f",
						cDividendPayout.date,
						cDividendPayout.songGu,
						cDividendPayout.zhuanGu,
						cDividendPayout.paiXi));
	        } 
		}
		CTest.EXPECT_LONG_EQ(error, 0);
		CTest.EXPECT_TRUE(container.size() > 10);
		CTest.EXPECT_DOUBLE_EQ(container.get(1).songGu, 2.0);
		CTest.EXPECT_DOUBLE_EQ(container.get(1).zhuanGu, 6.0);
		CTest.EXPECT_DOUBLE_EQ(container.get(1).paiXi, 0.5);
	}
	
	@CTest.test
	public void test_getKLine()
	{
		List<KLine> ctnKLine = new ArrayList<KLine>();
		int error = WebStockLayer.getKLine("600000", "20110523", "20190831", ctnKLine);
		if(0 == error)
		{
			System.out.println("List<TradeDetail> size=" + ctnKLine.size());
			if(ctnKLine.size() > 11)
			{
				for(int i = 0; i < 5; i++)  
		        { 
					KLine cKLine = ctnKLine.get(i);  
		            System.out.println(cKLine.date + "," 
		            		+ cKLine.open + "," + cKLine.close
		            		 + "," + cKLine.low + "," + cKLine.high);  
		        }
				System.out.println("...");
				for(int i = ctnKLine.size()-5; i < ctnKLine.size(); i++)  
		        { 
					KLine cKLine = ctnKLine.get(i);  
		            System.out.println(cKLine.date + "," 
		            		+ cKLine.open + "," + cKLine.close
		            		 + "," + cKLine.low + "," + cKLine.high);  
		        }
			}
			else
			{
				for(int i = 0; i < ctnKLine.size(); i++)  
		        {  
					KLine cKLine = ctnKLine.get(i);  
		            System.out.println(cKLine.date + "," 
		            		+ cKLine.open + "," + cKLine.close
		            		 + "," + cKLine.low + "," + cKLine.high);  
		        }
			}
		}
		else
		{
			System.out.println("ERROR:" + error);
		}
		CTest.EXPECT_LONG_EQ(error, 0);
		CTest.EXPECT_LONG_EQ(ctnKLine.size(), 1983);
		CTest.EXPECT_DOUBLE_EQ(ctnKLine.get(0).open, 14.17);
		CTest.EXPECT_DOUBLE_EQ(ctnKLine.get(1983-1).open, 11.34);
	}
	
	@CTest.test
	public void test_getTransactionRecordHistory()
	{
		List<TransactionRecord> ctnTradeDetails = new ArrayList<TransactionRecord>();
		int error = WebStockLayer.getTransactionRecordHistory("300163", "2012-08-21", ctnTradeDetails);
		if(0 == error)
		{
			System.out.println("List<TradeDetail> size=" + ctnTradeDetails.size());
			if(ctnTradeDetails.size() > 11)
			{
				for(int i = 0; i < 5; i++)  
		        { 
					TransactionRecord cTransactionRecord = ctnTradeDetails.get(i); 
					System.out.println(cTransactionRecord.time + "," 
		            		+ cTransactionRecord.price + "," + cTransactionRecord.volume);
		        }
				System.out.println("...");
				for(int i = ctnTradeDetails.size()-5; i < ctnTradeDetails.size(); i++)  
		        { 
					TransactionRecord cTransactionRecord = ctnTradeDetails.get(i); 
					System.out.println(cTransactionRecord.time + "," 
		            		+ cTransactionRecord.price + "," + cTransactionRecord.volume);
		        }
			}
			else
			{
				for(int i = 0; i < ctnTradeDetails.size(); i++)  
		        {  
					TransactionRecord cTransactionRecord = ctnTradeDetails.get(i);  
					System.out.println(cTransactionRecord.time + "," 
		            		+ cTransactionRecord.price + "," + cTransactionRecord.volume); 
		        }
			}
		}
		else
		{
			System.out.println("ERROR:" + error);
		}
		CTest.EXPECT_LONG_EQ(error, 0);
		CTest.EXPECT_LONG_EQ(ctnTradeDetails.size(), 203);
		CTest.EXPECT_DOUBLE_EQ(ctnTradeDetails.get(0).price, 12.37);
		CTest.EXPECT_DOUBLE_EQ(ctnTradeDetails.get(203-1).price, 12.83);
	}
	
	@CTest.test
	public void test_getRealTimeInfo()
	{
		{
			List<String> ids = new ArrayList<String>();
			ids.add("300163");ids.add("300164");ids.add("600004");
			List<RealTimeInfoLite> ctnRTInfos = new ArrayList<RealTimeInfoLite>();
			int error = WebStockLayer.getRealTimeInfo(ids, ctnRTInfos);
			CTest.EXPECT_LONG_EQ(error, 0);
			CTest.EXPECT_LONG_EQ(ctnRTInfos.size(), 3);
			
			for(int i=0; i<ctnRTInfos.size(); i++)
			{
				System.out.println("---------------------------------");
				System.out.println(ctnRTInfos.get(i).stockID);
				System.out.println(ctnRTInfos.get(i).name);
				System.out.println(ctnRTInfos.get(i).curPrice);
				System.out.println(ctnRTInfos.get(i).date);
		        System.out.println(ctnRTInfos.get(i).time);
			}
		}
		
		{
			List<String> ids = new ArrayList<String>();
			ids.add("300163");ids.add("300164");ids.add("000003");
			List<RealTimeInfoLite> ctnRTInfos = new ArrayList<RealTimeInfoLite>();
			int error = WebStockLayer.getRealTimeInfo(ids, ctnRTInfos);
			CTest.EXPECT_LONG_NE(error, 0);
			CTest.EXPECT_LONG_EQ(ctnRTInfos.size(), 2);
		}
	}
	
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(TestWebStockLayer.class);
		CTest.RUN_ALL_TESTS("TestWebStockLayer.");
		CSystem.stop();
	}
}
