package pers.di.dataengine.webdata.test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataengine.webdata.CommonDef.*;
import pers.di.dataengine.webdata.DataWebStockDividendPayout;
import pers.di.dataengine.webdata.DataWebStockDividendPayout.ResultDividendPayout;

public class TestDataWebStockDividendPayout {

	public static void main(String[] args){
		String StockID = "300428";
		
		ResultDividendPayout cResultDividendPayout = DataWebStockDividendPayout.getDividendPayout(StockID);
		if(0 == cResultDividendPayout.error)
		{
			for(int i = 0; i < cResultDividendPayout.resultList.size(); i++)  
	        {  
				DividendPayout cDividendPayout = cResultDividendPayout.resultList.get(i);  
	            System.out.println(cDividendPayout.date 
	            		+ "," + cDividendPayout.songGu
	            		+ "," + cDividendPayout.zhuanGu
	            		+ "," + cDividendPayout.paiXi);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + cResultDividendPayout.error);
		}
	}
}
