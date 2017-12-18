package pers.di.dataapi_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataapi.common.DividendPayout;
import pers.di.dataapi.webapi.DataWebStockDividendPayout;
import pers.di.common.CLog;
import pers.di.common.CRandom;
import pers.di.dataapi.common.*;

public class TestDataWebStockDividendPayout {
	
	public static void test_getDividendPayout_1()
	{
		String StockID = "000555";
		DataWebStockDividendPayout cDataWebStockDividendPayout = new DataWebStockDividendPayout();
		List<DividendPayout> container = new ArrayList<DividendPayout>();
		int error = cDataWebStockDividendPayout.getDividendPayout(StockID, container);
		if(0 == error)
		{
			CLog.output("TEST", "DataWebStockDividendPayout.getDividendPayout %s OK!", StockID);
			for(int i = 0; i < container.size(); i++)  
	        {  
				DividendPayout cDividendPayout = container.get(i);  
				CLog.output("TEST", "%s %.1f %.1f %.1f",
						cDividendPayout.date,
						cDividendPayout.songGu,
						cDividendPayout.zhuanGu,
						cDividendPayout.paiXi);
	        } 
		}
	}
	
	public static void test_getDividendPayout()
	{
		DataWebStockDividendPayout cDataWebStockDividendPayout = new DataWebStockDividendPayout();
			
		int iR = CRandom.randomUnsignedInteger()%1000;
		int StockIDInt = 600000+iR;
		
		String StockID = String.format("%d", StockIDInt);
		
		List<DividendPayout> container = new ArrayList<DividendPayout>();
		int error = cDataWebStockDividendPayout.getDividendPayout(StockID, container);
		if(0 == error)
		{
			CLog.output("TEST", "DataWebStockDividendPayout.getDividendPayout %s OK!", StockID);
			for(int i = 0; i < container.size(); i++)  
	        {  
				DividendPayout cDividendPayout = container.get(i);  
				CLog.output("TEST", "%s %.1f %.1f %.1f",
						cDividendPayout.date,
						cDividendPayout.songGu,
						cDividendPayout.zhuanGu,
						cDividendPayout.paiXi);
	        } 
		}
		else
		{
			CLog.output("TEST", "ERROR:%d", error);
		}
	}

	public static void main(String[] args){
		
		test_getDividendPayout_1();
		
//		for(int i=0; i<200; i++)
//		{
//			CLog.output("TEST","call test_getDividendPayout times: %d" , i);
//			test_getDividendPayout();
//		}
		
	}
}
