package pers.di.dataengine.webdata_test;

import pers.di.dataengine.webdata.DataWebStockBaseInfo.ResultStockBaseInfo;
import pers.di.dataengine.common.*;
import pers.di.dataengine.webdata.DataWebStockBaseInfo;

public class TestDataWebStockBaseInfo {
		public static void main(String[] args){
		{
			{
				String stockID = "300550";
				
				System.out.println("getRealTimeInfoMore -----------------------------------");
				StockBaseInfo ctnStockBaseInfo = new StockBaseInfo();
				int error = DataWebStockBaseInfo.getStockBaseInfo(stockID, ctnStockBaseInfo);
				if(0 == error)
				{ 
					System.out.println(ctnStockBaseInfo.name);
					System.out.println(ctnStockBaseInfo.date);
					System.out.println(ctnStockBaseInfo.time);
					System.out.println(ctnStockBaseInfo.allMarketValue);
					System.out.println(ctnStockBaseInfo.circulatedMarketValue);
					System.out.println(ctnStockBaseInfo.peRatio);
				}
				else
				{
					System.out.println("ERROR:" + error);
				}
			}
		}
	}
}
