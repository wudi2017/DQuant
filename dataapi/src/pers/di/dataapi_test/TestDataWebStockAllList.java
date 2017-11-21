package pers.di.dataapi_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataapi.common.StockItem;
import pers.di.dataapi.webapi.DataWebStockAllList;
import pers.di.dataapi.common.*;

public class TestDataWebStockAllList {
	public static void main(String[] args) {
		
		DataWebStockAllList cDataWebStockAllList = new DataWebStockAllList();

		for(int iTime=0; iTime<100; iTime++)
		{
			List<StockItem> ctnStockItem = new ArrayList<StockItem>();
			int error = cDataWebStockAllList.getAllStockList(ctnStockItem);
			if(0 == error)
			{
				for(int i = 0; i < ctnStockItem.size(); i++)  
		        {  
					StockItem cStockItem = ctnStockItem.get(i);  
		            // System.out.println(cStockItem.name + "," + cStockItem.id);  
		        } 
				System.out.println("count:" + ctnStockItem.size()); 
			}
			else
			{
				System.out.println("ERROR:" + error);
			}
		}
	}
}
