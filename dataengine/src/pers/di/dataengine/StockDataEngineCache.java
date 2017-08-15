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
	public Map<String,StockBaseInfo> latestStockBaseInfo = null;
	
	// ��K��ʷ���ݻ���
	// key:600001
	public Map<String,List<KLine>> dayKLineList = null;
	
	// ���ڷ�ʱ����  
	// key:600001_2016-01-01
	public Map<String,List<TimePrice>> stockTimeData = null;
}
