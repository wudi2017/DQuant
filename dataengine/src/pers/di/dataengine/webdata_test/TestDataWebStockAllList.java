package pers.di.dataengine.webdata_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataengine.common.*;
import pers.di.dataengine.webdata.DataWebStockAllList;

public class TestDataWebStockAllList {
	public static void main(String[] args) {

		List<StockItem> container = new ArrayList<StockItem>();
		int error = DataWebStockAllList.getAllStockList(container);
		if(0 == error)
		{
			for(int i = 0; i < container.size(); i++)  
	        {  
				StockItem cStockItem = container.get(i);  
	            System.out.println(cStockItem.name + "," + cStockItem.id);  
	        } 
			System.out.println("count:" + container.size()); 
		}
		else
		{
			System.out.println("ERROR:" + error);
		}
	}
}
