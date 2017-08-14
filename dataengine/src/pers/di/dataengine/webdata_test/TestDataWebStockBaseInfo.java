package pers.di.dataengine.webdata_test;

import pers.di.dataengine.webdata.DataWebStockBaseInfo.ResultStockBaseInfo;
import pers.di.dataengine.webdata.DataWebStockBaseInfo;

public class TestDataWebStockBaseInfo {
		public static void main(String[] args){
		{
			{
				String stockID = "300550";
				
				System.out.println("getRealTimeInfoMore -----------------------------------");
				ResultStockBaseInfo cResultStockBaseInfo = DataWebStockBaseInfo.getStockBaseInfo(stockID);
				if(0 == cResultStockBaseInfo.error)
				{ 
					System.out.println(cResultStockBaseInfo.stockBaseInfo.name);
					System.out.println(cResultStockBaseInfo.stockBaseInfo.date);
					System.out.println(cResultStockBaseInfo.stockBaseInfo.time);
					System.out.println(cResultStockBaseInfo.stockBaseInfo.allMarketValue);
					System.out.println(cResultStockBaseInfo.stockBaseInfo.circulatedMarketValue);
					System.out.println(cResultStockBaseInfo.stockBaseInfo.peRatio);
				}
				else
				{
					System.out.println("ERROR:" + cResultStockBaseInfo.error);
				}
			}
		}
	}
}
