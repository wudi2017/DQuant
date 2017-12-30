package pers.di.dataapi_test;

import pers.di.dataapi.common.RealTimeInfoLite;
import pers.di.dataapi.webapi.DataWebStockRealTimeInfo;

import java.util.*;

import pers.di.common.CSystem;
import pers.di.common.CTest;
import pers.di.dataapi.common.*;

public class TestDataWebStockRealTimeInfo {
	
	@CTest.test
	public static void test_getRealTimeInfoSingle()
	{
//		DataWebStockRealTimeInfo cDataWebStockRealTimeInfo = new DataWebStockRealTimeInfo();
//		
//		String stockID = "300550";
//		{
//			RealTimeInfo container = new RealTimeInfo();
//			int error = cDataWebStockRealTimeInfo.getRealTimeInfo(stockID, container);
//			if(0 == error)
//			{ 
//				System.out.println(container.stockID);
//				System.out.println(container.name);
//				System.out.println(container.curPrice);
//				System.out.println(container.date);
//		        System.out.println(container.time);
//			}
//			else
//			{
//				System.out.println("ERROR:" + error);
//			}
//			CTest.EXPECT_LONG_EQ(error, 0);
//		}
	}
	
	@CTest.test
	public static void test_getRealTimeInfoMulti()
	{
		DataWebStockRealTimeInfo cDataWebStockRealTimeInfo = new DataWebStockRealTimeInfo();
		
//		{
//			
//			cDataWebStockRealTimeInfo.setInterestStockID("300550");
//			List<RealTimeInfo> ctnRTInfos = new ArrayList<RealTimeInfo>();
//			int error = cDataWebStockRealTimeInfo.getRealTimeInfo(ctnRTInfos);
//			CTest.EXPECT_LONG_EQ(error, 0);
//			CTest.EXPECT_LONG_EQ(ctnRTInfos.size(), 1);
//			System.out.println(ctnRTInfos.get(0).stockID);
//			System.out.println(ctnRTInfos.get(0).name);
//			System.out.println(ctnRTInfos.get(0).curPrice);
//			System.out.println(ctnRTInfos.get(0).date);
//	        System.out.println(ctnRTInfos.get(0).time);
//		}
		
		{
			List<String> ids = new ArrayList<String>();
			ids.add("300163");ids.add("300164");ids.add("600004");
			cDataWebStockRealTimeInfo.setInterestStockIDs(ids);
			List<RealTimeInfoLite> ctnRTInfos = new ArrayList<RealTimeInfoLite>();
			int error = cDataWebStockRealTimeInfo.getRealTimeInfo(ctnRTInfos);
			CTest.EXPECT_LONG_EQ(error, 0);
			CTest.EXPECT_LONG_EQ(ctnRTInfos.size(), 3);
			
//			for(int i=0; i<ctnRTInfos.size(); i++)
//			{
//				System.out.println("---------------------------------");
//				System.out.println(ctnRTInfos.get(i).stockID);
//				System.out.println(ctnRTInfos.get(i).name);
//				System.out.println(ctnRTInfos.get(i).curPrice);
//				System.out.println(ctnRTInfos.get(i).date);
//		        System.out.println(ctnRTInfos.get(i).time);
//			}
		}
		
		{
			List<String> ids = new ArrayList<String>();
			ids.add("300163");ids.add("300164");ids.add("000003");
			cDataWebStockRealTimeInfo.setInterestStockIDs(ids);
			List<RealTimeInfoLite> ctnRTInfos = new ArrayList<RealTimeInfoLite>();
			int error = cDataWebStockRealTimeInfo.getRealTimeInfo(ctnRTInfos);
			CTest.EXPECT_LONG_NE(error, 0);
			CTest.EXPECT_LONG_EQ(ctnRTInfos.size(), 2);
			
//			for(int i=0; i<ctnRTInfos.size(); i++)
//			{
//				System.out.println("---------------------------------");
//				System.out.println(ctnRTInfos.get(i).stockID);
//				System.out.println(ctnRTInfos.get(i).name);
//				System.out.println(ctnRTInfos.get(i).curPrice);
//				System.out.println(ctnRTInfos.get(i).date);
//		        System.out.println(ctnRTInfos.get(i).time);
//			}
		}
		
	}

	public static void main(String[] args){
		CSystem.start();
		CTest.ADD_TEST(TestDataWebStockRealTimeInfo.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
