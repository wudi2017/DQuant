package pers.di.dataengine.webdata_test;

import pers.di.dataengine.webdata.DataWebStockRealTimeInfo;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo.ResultRealTimeInfo;

public class TestDataWebStockRealTimeInfo {

	public static void main(String[] args){
		
		String stockID = "300550";
		{
			System.out.println("getRealTimeInfo -----------------------------------");
			ResultRealTimeInfo cResultRealTimeInfo = DataWebStockRealTimeInfo.getRealTimeInfo(stockID);
			if(0 == cResultRealTimeInfo.error)
			{ 
				System.out.println(cResultRealTimeInfo.realTimeInfo.name);
				System.out.println(cResultRealTimeInfo.realTimeInfo.curPrice);
				System.out.println(cResultRealTimeInfo.realTimeInfo.date);
		        System.out.println(cResultRealTimeInfo.realTimeInfo.time);
			}
			else
			{
				System.out.println("ERROR:" + cResultRealTimeInfo.error);
			}
		}
	}

}
