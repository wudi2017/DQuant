package pers.di.dataapi;

import java.util.*;

import pers.di.common.CLRUMapCache;
import pers.di.dataapi.common.*;

public class StockDataApiCache {
	
	public StockDataApiCache()
	{
		localLatestDate = null;
		m_AllStockID = new ArrayList<String>();
		m_latestStockInfo = new HashMap<String,StockInfo>();
		m_dayKLineList = new HashMap<String,List<KLine>>();
		m_stockTimeData = new CLRUMapCache<String, List<TimePrice>>(100);
	}
	
	public void clearAllCache()
	{
		localLatestDate = null;
		m_AllStockID.clear();
		m_latestStockInfo.clear();
		m_dayKLineList.clear();
		m_stockTimeData.clear();
	}
	
	//----------------------------------------------------------------------------------------
	// AllStockID
	
	public List<String> AllStockID()
	{
		return m_AllStockID;
	}
	
	//----------------------------------------------------------------------------------------
	// stockTimeDat
		
	public StockInfo get_latestStockInfo(String key)
	{
		return m_latestStockInfo.get(key);
	}
	public void set_latestStockInfo(String key, StockInfo value)
	{
		m_latestStockInfo.put(key, value);
	}
	public boolean contains_latestStockInfo(String key)
	{
		return m_latestStockInfo.containsKey(key);
	}
		
	
	//----------------------------------------------------------------------------------------
	// stockTimeDat
	
	public List<KLine> get_dayKLine(String key)
	{
		return m_dayKLineList.get(key);
	}
	public void set_dayKLine(String key, List<KLine> value)
	{
		m_dayKLineList.put(key, value);
	}
	public boolean contains_dayKLine(String key)
	{
		return m_dayKLineList.containsKey(key);
	}
		
	//----------------------------------------------------------------------------------------
	// stockTimeDat
	
	public List<TimePrice> get_stockTimeData(String key)
	{
		return m_stockTimeData.get(key);
	}
	public void set_stockTimeData(String key, List<TimePrice> value)
	{
		m_stockTimeData.put(key, value);
	}
	public boolean contains_stockTimeData(String key)
	{
		return m_stockTimeData.containsKey(key);
	}
	
	//----------------------------------------------------------------------------------------
	
	// 当前总数据最新更新日期缓存
	public String localLatestDate;
	
	// 本地股票列表缓存
	private List<String> m_AllStockID;
	
	// 本地股票最新基本信息缓存
	private Map<String,StockInfo> m_latestStockInfo;
	
	// 日K历史数据缓存
	// key:600001
	private Map<String,List<KLine>> m_dayKLineList;
	
	// 日内分时缓存  
	// key:600001_2016-01-01
	CLRUMapCache<String, List<TimePrice>> m_stockTimeData;
}
