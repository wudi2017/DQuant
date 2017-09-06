package pers.di.dataengine.webdata_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataengine.common.*;
import pers.di.dataengine.webdata.DataWebStockDayDetail;
import pers.di.dataengine.webdata.DataWebStockDayDetail.ResultDayDetail;

public class TestDataWebStockDayDetail {
	public static void main(String[] args){
		ResultDayDetail cResultDayDetail = DataWebStockDayDetail.getDayDetail("300163", "2015-02-16");
		if(0 == cResultDayDetail.error)
		{
			for(int i = 0; i < cResultDayDetail.resultList.size(); i++)  
	        {  
				TradeDetail cTradeDetail = cResultDayDetail.resultList.get(i);  
	            System.out.println(cTradeDetail.time + "," 
	            		+ cTradeDetail.price + "," + cTradeDetail.volume);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + cResultDayDetail.error);
		}
	}
}
