package pers.di.dataengine.webdata_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataengine.common.*;
import pers.di.dataengine.webdata.DataWebStockAllList;

public class TestDataWebStockAllList {
	public static void main(String[] args) {

		List<StockItem> ctnStockItem = new ArrayList<StockItem>();
		int error = DataWebStockAllList.getAllStockList(ctnStockItem);
		if(0 == error)
		{
			for(int i = 0; i < ctnStockItem.size(); i++)  
	        {  
				StockItem cStockItem = ctnStockItem.get(i);  
	            System.out.println(cStockItem.name + "," + cStockItem.id);  
	        } 
			System.out.println("count:" + ctnStockItem.size()); 
		}
		else
		{
			System.out.println("ERROR:" + error);
		}
	}
}
