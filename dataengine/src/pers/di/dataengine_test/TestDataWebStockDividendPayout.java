package pers.di.dataengine_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataengine.common.*;

import pers.di.dataengine.webapi.DataWebStockDividendPayout;

public class TestDataWebStockDividendPayout {

	public static void main(String[] args){
		String StockID = "300428";
		
		List<DividendPayout> container = new ArrayList<DividendPayout>();
		int error = DataWebStockDividendPayout.getDividendPayout(StockID, container);
		if(0 == error)
		{
			for(int i = 0; i < container.size(); i++)  
	        {  
				DividendPayout cDividendPayout = container.get(i);  
	            System.out.println(cDividendPayout.date 
	            		+ "," + cDividendPayout.songGu
	            		+ "," + cDividendPayout.zhuanGu
	            		+ "," + cDividendPayout.paiXi);  
	        } 
		}
		else
		{
			System.out.println("ERROR:" + error);
		}
	}
}
