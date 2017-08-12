package pers.di.dataengine;

import java.util.*;

import pers.di.dataengine.webdata.CommonDef.*;

public class StockDataEngineCache {
	
	public void clearAllCache()
	{
		localLatestDate = null;
		AllStockID = null;
	}
	
	// ��ǰ���������¸������ڻ���
	public String localLatestDate = null;
	// ���ع�Ʊ�б���
	public List<String> AllStockID = null;
	// ���ع�Ʊ���»�����Ϣ����
	public Map<String,StockBaseInfo> latestStockInfo = null;
}
