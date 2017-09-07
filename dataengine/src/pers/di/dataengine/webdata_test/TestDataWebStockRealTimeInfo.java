package pers.di.dataengine.webdata_test;

import pers.di.dataengine.common.*;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo;

public class TestDataWebStockRealTimeInfo {

	public static void main(String[] args){
		
		String stockID = "300550";
		{
			System.out.println("getRealTimeInfo -----------------------------------");
			RealTimeInfo container = new RealTimeInfo();
			int error = DataWebStockRealTimeInfo.getRealTimeInfo(stockID, container);
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
