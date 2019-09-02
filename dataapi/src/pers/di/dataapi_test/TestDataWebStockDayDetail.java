package pers.di.dataapi_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataapi.webapi.DataWebStockDayDetail;
import pers.di.common.CLog;
import pers.di.dataapi.common.*;

public class TestDataWebStockDayDetail {
	
	public static DataWebStockDayDetail s_cDataWebStockDayDetail = new DataWebStockDayDetail();
	
	public static void test_getDayDetail()
	{
		List<TransactionRecord> ctnTradeDetails = new ArrayList<TransactionRecord>();
		int error = s_cDataWebStockDayDetail.getDayDetail("600004", "2019-08-30", ctnTradeDetails);
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
	}
	
	public static void main(String[] args){
		
		for(int i=0; i<30; i++)
		{
			CLog.output("TEST","call getDayDetail times: %d" , i);
			test_getDayDetail();
		}
	}
}
