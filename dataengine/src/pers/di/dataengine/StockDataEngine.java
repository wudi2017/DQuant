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
	 * �������й�Ʊ����
	 * m_cCache.localLatestDateΪ���������������ڣ������ڴ��ڵ���Ҫ������ʱ����������
	 * �������ݸ���ִ�к����л������
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
	 * ���µ�ֻ��Ʊ����
	 * �˷������ı�m_cCache.localLatestDate
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
			// ���µ�ֻ��Ʊ���� ��Ӱ��m_cCache.localLatestDate
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
	 * ��ȡ���й�ƱId�б�
	 * ���ڵ�ǰ�������ݻ�ȡ������֤�����£����������ݸ��£�
	 * �˽ӿڴ������ݻ������
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
	 * ��ȡĳֻ��Ʊ������Ϣ
	 * ����֤�����£����������ݸ��£�
	 * �˽ӿڴ������ݻ������
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
		
		// �״ν��л���
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
			
		// �ӻ�����ȡ����
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
