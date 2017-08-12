package pers.di.dataengine.webdata_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataengine.webdata.CommonDef.*;
import pers.di.dataengine.webdata.DataWebStockAllList;
import pers.di.dataengine.webdata.DataWebStockAllList.ResultAllStockList;

public class TestDataWebStockAllList {
	public static void main(String[] args) {

		ResultAllStockList cResultAllStockList = DataWebStockAllList.getAllStockList();
		if(0 == cResultAllStockList.error)
		{
			for(int i = 0; i < cResultAllStockList.resultList.size(); i++)  
	        {  
				StockSimpleItem cStockSimpleItem = cResultAllStockList.resultList.get(i);  
	            System.out.println(cStockSimpleItem.name + "," + cStockSimpleItem.id);  
	        } 
			System.out.println("count:" + cResultAllStockList.resultList.size()); 
		}
		else
		{
			System.out.println("ERROR:" + cResultAllStockList.error);
		}
	}
}
