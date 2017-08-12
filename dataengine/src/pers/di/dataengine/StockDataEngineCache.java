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
	public Map<String,StockBaseInfo> latestStockInfo = null;
}
