package pers.di.dataapi_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataapi.common.TradeDetail;
import pers.di.dataapi.webapi.DataWebStockDayDetail;
import pers.di.common.CLog;
import pers.di.dataapi.common.*;

public class TestDataWebStockDayDetail {
	
	public static void test_getDayDetail()
	{
		List<TradeDetail> ctnTradeDetails = new ArrayList<TradeDetail>();
		int error = DataWebStockDayDetail.getDayDetail("300163", "2015-02-16", ctnTradeDetails);
		if(0 == error)
		{
			for(int i = 0; i < ctnTradeDetails.size(); i++)  
	        {  
				TradeDetail cTradeDetail = ctnTradeDetails.get(i);  
	            System.out.println(cTradeDetail.time + "," 
	            		+ cTradeDetail.price + "," + cTradeDetail.volume);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + error);
		}
	}
	
	public static void main(String[] args){
		
		for(int i=0; i<200; i++)
		{
			CLog.output("TEST","call getDayDetail times: %d" , i);
			test_getDayDetail();
		}
	}
}
