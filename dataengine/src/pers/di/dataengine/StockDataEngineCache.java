package pers.di.dataengine;

import java.util.*;

import pers.di.dataengine.webdata.CommonDef.*;

public class StockDataEngineCache {
	
	public void clearAllCache()
	{
		localLatestDate = null;
		AllStockID = null;
	}
	
	// 当前总数据最新更新日期缓存
	public String localLatestDate = null;
	
	// 本地股票列表缓存
	public List<String> AllStockID = null;
	
	// 本地股票最新基本信息缓存
	public Map<String,StockBaseInfo> latestStockBaseInfo = null;
	
	// 日K历史数据缓存
	// key:600001
	public Map<String,List<KLine>> dayKLineList = null;
	
	// 日内分时缓存  
	// key:600001_2016-01-01
	public Map<String,List<TimePrice>> stockTimeData = null;
}
