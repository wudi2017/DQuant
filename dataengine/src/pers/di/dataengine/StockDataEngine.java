package pers.di.dataengine;

import java.util.*;

import pers.di.common.*;
import pers.di.dataengine.BaseDataDownload.ResultUpdateStock;
import pers.di.dataengine.BaseDataStorage.ResultAllStockFullDataTimestamps;
import pers.di.dataengine.webdata.CommonDef.*;
import pers.di.dataengine.webdata.DataWebStockAllList.ResultAllStockList;

public class StockDataEngine {
	private static StockDataEngine s_instance = new StockDataEngine();  
	private StockDataEngine () 
	{
		m_cBaseDataLayer = new BaseDataLayer("data");
		m_cCache = new StockDataEngineCache();
	}
	public static StockDataEngine instance() {  
		return s_instance;  
	}  
	
	/*
	 * 更新所有股票数据
	 * m_cCache.localLatestDate为缓存最新数据日期，此日期大于等于要更新日时，不做事情
	 * 真正数据更新执行后，所有缓存清空
	 */
	public boolean updateAllLocalStocks(String dateStr)
	{
		if(null == m_cCache.localLatestDate)
		{
			CLog.output("STOCKDATA","DataEngine.getUpdatedStocksDate\n");
			ResultAllStockFullDataTimestamps cResultAllStockFullDataTimestamps = m_cBaseDataLayer.getAllStockFullDataTimestamps();
			if(0 == cResultAllStockFullDataTimestamps.error)
			{
				m_cCache.localLatestDate = cResultAllStockFullDataTimestamps.date;
			}
			else
			{
				m_cCache.localLatestDate = "0000-00-00";
				CLog.output("STOCKDATA", "DataEngine.getUpdatedStocksDate failed, reset to %s \n", m_cCache.localLatestDate);
			}
		}
		
		if(m_cCache.localLatestDate.compareTo(dateStr) >= 0)
		{
			CLog.output("STOCKDATA", "update success! (current is newest, local: %s)\n", m_cCache.localLatestDate);
		}
		else
		{
			int iUpdateCnt = m_cBaseDataLayer.updateLocalAllStockData(dateStr);
			CLog.output("STOCKDATA", "update success to date: %s (count: %d)\n", m_cCache.localLatestDate, iUpdateCnt);
			m_cCache.clearAllCache();
		}
		
		return true;
	}
	
	/*
	 * 更新单只股票数据
	 * 此方法不改变m_cCache.localLatestDate
	 */
	public boolean updateLocalStocks(String stockID, String dateStr)
	{
		if(null == m_cCache.localLatestDate)
		{
			CLog.output("STOCKDATA","DataEngine.getUpdatedStocksDate\n");
			ResultAllStockFullDataTimestamps cResultAllStockFullDataTimestamps = m_cBaseDataLayer.getAllStockFullDataTimestamps();
			if(0 == cResultAllStockFullDataTimestamps.error)
			{
				m_cCache.localLatestDate = cResultAllStockFullDataTimestamps.date;
			}
			else
			{
				CLog.output("STOCKDATA", "DataEngine.getUpdatedStocksDate failed! \n", m_cCache.localLatestDate);
			}
		}
		
		if(m_cCache.localLatestDate.compareTo(dateStr) >= 0)
		{
			CLog.output("STOCKDATA", "update %s success! (current is newest, local: %s)\n",stockID, m_cCache.localLatestDate);
		}
		else
		{
			// 更新单只股票数据 不影响m_cCache.localLatestDate
			ResultUpdateStock cResultUpdateStock = m_cBaseDataLayer.updateLocaStockData(stockID);
			
			if(0 == cResultUpdateStock.error)
			{
				CLog.output("STOCKDATA", "update %s success to date: %s (count: %d)\n", stockID, cResultUpdateStock.updateCnt);
			}
			else
			{
				CLog.error("STOCKDATA", "update %s failed \n", cResultUpdateStock.error);
			}
		}
		return true;
	}
	
	/*
	 * 获取所有股票Id列表
	 * 基于当前本地数据获取，不保证是最新（依赖于数据更新）
	 * 此接口带有数据缓存机制
	 */
	public static class ResultAllStockID
	{
		public ResultAllStockID()
		{
			error = -1000;
			resultList = new ArrayList<String>();
		}
		public int error;
		public List<String> resultList;
	}
	public ResultAllStockID getAllStockID()
	{
		ResultAllStockID cResultAllStockID = new ResultAllStockID();
		
		if(null != m_cCache.AllStockID)
		{
			cResultAllStockID.error = 0;
			cResultAllStockID.resultList = m_cCache.AllStockID;
		}
		else
		{
			m_cCache.AllStockID = new ArrayList<String>();
			
			ResultAllStockList cResultAllStockList = m_cBaseDataLayer.getLocalAllStock();
			if(0 == cResultAllStockList.error)
			{
				for(int i=0; i<cResultAllStockList.resultList.size();i++)
				{
					String stockId = cResultAllStockList.resultList.get(i).id;
					m_cCache.AllStockID.add(stockId);
				}
				cResultAllStockID.error = 0;
				cResultAllStockID.resultList = m_cCache.AllStockID;
			}
			else
			{
				CLog.error("STOCKDATA", "DataEngine.getLocalAllStock error(%d) \n", cResultAllStockList.error);
				cResultAllStockID.error = -1;
			}
		}
		
		return cResultAllStockID;
	}
	
	/*
	 * 获取某只股票基本信息
	 * 不保证是最新（依赖于数据更新）
	 * 此接口带有数据缓存机制
	 */
	public static class ResultLatestStockInfo
	{
		public ResultLatestStockInfo()
		{
			error = -1000;
			stockInfo = new StockBaseInfo();
		}
		public int error;
		public StockBaseInfo stockInfo;
	}
	public ResultLatestStockInfo getLatestStockInfo(String id)
	{
		ResultLatestStockInfo cResultLatestStockInfo = new ResultLatestStockInfo();
		
		// 首次进行缓存
		if(null == m_cCache.latestStockInfo || !m_cCache.latestStockInfo.containsKey(id))
		{
			if(null == m_cCache.latestStockInfo)
			{
				m_cCache.latestStockInfo = new HashMap<String,StockBaseInfo>();
			}
			
			StockBaseInfo cStockInfo = new StockBaseInfo();
			cStockInfo. = id;
			
			ResultStockBaseData cResultStockBaseData = DataEngine.getBaseInfo(id);
			
			if(0 == cResultStockBaseData.error)
			{
				cStockInfo.name = cResultStockBaseData.stockBaseInfo.name;
				cStockInfo.allMarketValue = cResultStockBaseData.stockBaseInfo.allMarketValue; 
				cStockInfo.circulatedMarketValue = cResultStockBaseData.stockBaseInfo.circulatedMarketValue; 
				cStockInfo.peRatio = cResultStockBaseData.stockBaseInfo.peRatio;
				
				m_cache_latestStockInfo.put(id, cStockInfo);
			}
			else
			{
				//BLog.error("STOCKDATA", "DataEngine.getBaseInfo error(%d) \n", cResultStockBaseData.error);
			}
		}
			
		// 从缓存中取数据
		if(null != m_cache_latestStockInfo && m_cache_latestStockInfo.containsKey(id))
		{
			cResultLatestStockInfo.error = 0;
			cResultLatestStockInfo.stockInfo = m_cache_latestStockInfo.get(id);
		}
		else
		{
			cResultLatestStockInfo.error = -1;
		}
		
		return cResultLatestStockInfo;
	}
	
	
	private BaseDataLayer m_cBaseDataLayer;
	private StockDataEngineCache m_cCache;
}
