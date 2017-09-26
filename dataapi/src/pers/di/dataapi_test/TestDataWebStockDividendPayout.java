package pers.di.dataapi_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataapi.common.DividendPayout;
import pers.di.dataapi.webapi.DataWebStockDividendPayout;
import pers.di.dataapi.common.*;

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
