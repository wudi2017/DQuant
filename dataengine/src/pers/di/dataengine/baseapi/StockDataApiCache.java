package pers.di.dataengine.baseapi;

import java.util.*;

import pers.di.dataengine.common.*;

public class StockDataApiCache {
	
	public void clearAllCache()
	{
		localLatestDate = null;
		AllStockID = null;
		latestStockInfo = null;
		dayKLineList = null;
		stockTimeData = null;
	}
	
	// ��ǰ���������¸������ڻ���
	public String localLatestDate = null;
	
	// ���ع�Ʊ�б���
	public List<String> AllStockID = null;
	
	// ���ع�Ʊ���»�����Ϣ����
	public Map<String,StockInfo> latestStockInfo = null;
	
	// ��K��ʷ���ݻ���
	// key:600001
	public Map<String,List<KLine>> dayKLineList = null;
	
	// ���ڷ�ʱ����  
	// key:600001_2016-01-01
	public Map<String,List<TimePrice>> stockTimeData = null;
}
