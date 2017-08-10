package pers.di.dataengine;

import java.util.Formatter;
import java.util.Random;

import pers.di.dataengine.DataDownload.ResultUpdateStock;
import pers.di.dataengine.webdata.DataWebCommonDef.StockSimpleItem;
import pers.di.dataengine.webdata.DataWebStockAllList;
import pers.di.dataengine.webdata.DataWebStockAllList.ResultAllStockList;

public class DataEngine {
	private static DataEngine s_instance = new DataEngine();  
	private DataEngine () 
	{
		
	}
	public static DataEngine instance() {  
		return s_instance;  
	}  
	
	public int initialize(String workDir)
	{
		m_cDataStorage = new DataStorage(workDir);
		m_cDataDownload = new DataDownload(m_cDataStorage);
		return 0;
	}
	
	public int updateLocalAllStockData()
	{
		// 更新指数k
		String ShangZhiId = "999999";
		String ShangZhiName = "上证指数";
		
		ResultUpdateStock cResultUpdateStockShangZhi = m_cDataDownload.downloadStockFullData(ShangZhiId);
		fmt.format("update success: %s (%s) item:%d\n", ShangZhiId, ShangZhiName, cResultUpdateStockShangZhi.updateCnt);
		
		// 更新所有k
		ResultAllStockList cResultAllStockList = DataWebStockAllList.getAllStockList();
		if(0 == cResultAllStockList.error)
		{
			for(int i = 0; i < cResultAllStockList.resultList.size(); i++)  
	        {  
				StockSimpleItem cStockSimpleItem = cResultAllStockList.resultList.get(i);
				
				String stockID = cStockSimpleItem.id;
				
				ResultUpdateStock cResultUpdateStock = m_cDataDownload.downloadStockFullData(stockID);
	           
				fmt.format("update success: %s (%s) item:%d \n", cStockSimpleItem.id, cStockSimpleItem.name, cResultUpdateStock.updateCnt);
	        }	
		}
		else
		{
			System.out.println("ERROR:" + cResultAllStockList.error);
		}
		
		return 0;
	}
	
	
	private DataDownload m_cDataDownload;
	private DataStorage m_cDataStorage;
	
	public static Random random = new Random();
	public static Formatter fmt = new Formatter(System.out);
}
