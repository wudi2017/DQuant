package pers.di.dataengine.webdata_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataengine.common.*;
import pers.di.dataengine.webdata.DataWebStockDayDetail;

public class TestDataWebStockDayDetail {
	public static void main(String[] args){
		
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
}
