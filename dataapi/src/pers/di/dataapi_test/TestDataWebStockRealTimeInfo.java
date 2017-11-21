package pers.di.dataapi_test;

import pers.di.dataapi.common.RealTimeInfo;
import pers.di.dataapi.webapi.DataWebStockRealTimeInfo;
import pers.di.dataapi.common.*;

public class TestDataWebStockRealTimeInfo {

	public static void main(String[] args){
		
		DataWebStockRealTimeInfo cDataWebStockRealTimeInfo = new DataWebStockRealTimeInfo();
		
		String stockID = "300550";
		{
			System.out.println("getRealTimeInfo -----------------------------------");
			RealTimeInfo container = new RealTimeInfo();
			int error = cDataWebStockRealTimeInfo.getRealTimeInfo(stockID, container);
			if(0 == error)
			{ 
				System.out.println(container.name);
				System.out.println(container.curPrice);
				System.out.println(container.date);
		        System.out.println(container.time);
			}
			else
			{
				System.out.println("ERROR:" + error);
			}
		}
	}

}
