package pers.di.dataengine_test;

import pers.di.dataengine.common.*;
import pers.di.dataengine.webapi.DataWebStockInfo;

public class TestDataWebStockInfo {
		public static void main(String[] args){
		{
			{
				String stockID = "300550";
				
				System.out.println("getRealTimeInfoMore -----------------------------------");
				StockInfo ctnStockInfo = new StockInfo();
				int error = DataWebStockInfo.getStockInfo(stockID, ctnStockInfo);
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
			}
		}
	}
}
