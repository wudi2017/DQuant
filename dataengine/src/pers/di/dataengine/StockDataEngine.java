package pers.di.dataengine;

import java.util.*;

import pers.di.common.*;
import pers.di.dataengine.BaseDataDownload.ResultUpdateStock;
import pers.di.dataengine.BaseDataLayer.ResultMinKLineOneDay;
import pers.di.dataengine.BaseDataStorage.ResultAllStockFullDataTimestamps;
import pers.di.dataengine.BaseDataStorage.ResultStockBaseData;
import pers.di.dataengine.webdata.CommonDef.*;
import pers.di.dataengine.webdata.DataWebStockAllList.ResultAllStockList;
import pers.di.dataengine.webdata.DataWebStockDayK.ResultKLine;

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
	public int updateAllLocalStocks(String dateStr)
	{
		if(null == m_cCache.localLatestDate)
		{
			CLog.output("STOCK","DataEngine.getUpdatedStocksDate\n");
			ResultAllStockFullDataTimestamps cResultAllStockFullDataTimestamps = m_cBaseDataLayer.getAllStockFullDataTimestamps();
			if(0 == cResultAllStockFullDataTimestamps.error)
			{
				m_cCache.localLatestDate = cResultAllStockFullDataTimestamps.date;
			}
			else
			{
				m_cCache.localLatestDate = "0000-00-00";
				CLog.output("STOCK", "DataEngine.getUpdatedStocksDate failed, reset to %s \n", m_cCache.localLatestDate);
			}
		}
		
		if(m_cCache.localLatestDate.compareTo(dateStr) >= 0)
		{
			CLog.output("STOCK", "update success! (current is newest, local: %s)\n", m_cCache.localLatestDate);
		}
		else
		{
			int iUpdateCnt = m_cBaseDataLayer.updateLocalAllStocKLine(dateStr);
			CLog.output("STOCK", "update success to date: %s (count: %d)\n", m_cCache.localLatestDate, iUpdateCnt);
			m_cCache.clearAllCache();
		}
		
		return 0;
	}
	
	/*
	 * ���µ�ֻ��Ʊ����
	 * �˷������ı�m_cCache.localLatestDate
	 */
	public int updateLocalStocks(String stockID, String dateStr)
	{
		if(null == m_cCache.localLatestDate)
		{
			CLog.output("STOCK","DataEngine.getUpdatedStocksDate\n");
			ResultAllStockFullDataTimestamps cResultAllStockFullDataTimestamps = m_cBaseDataLayer.getAllStockFullDataTimestamps();
			if(0 == cResultAllStockFullDataTimestamps.error)
			{
				m_cCache.localLatestDate = cResultAllStockFullDataTimestamps.date;
			}
			else
			{
				CLog.output("STOCK", "DataEngine.getUpdatedStocksDate failed! \n", m_cCache.localLatestDate);
			}
		}
		
		if(m_cCache.localLatestDate.compareTo(dateStr) >= 0)
		{
			CLog.output("STOCK", "update %s success! (current is newest, local: %s)\n",stockID, m_cCache.localLatestDate);
		}
		else
		{
			// ���µ�ֻ��Ʊ���� ��Ӱ��m_cCache.localLatestDate
			ResultUpdateStock cResultUpdateStock = m_cBaseDataLayer.updateLocaStocKLine(stockID);
			
			if(0 == cResultUpdateStock.error)
			{
				CLog.output("STOCK", "update %s success to date: %s (count: %d)\n", stockID, cResultUpdateStock.updateCnt);
			}
			else
			{
				CLog.error("STOCK", "update %s failed \n", cResultUpdateStock.error);
			}
		}
		return 0;
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
				CLog.error("STOCK", "DataEngine.getLocalAllStock error(%d) \n", cResultAllStockList.error);
			}
		}
		return cResultAllStockID;
	}
	
	/*
	 * ��ȡĳֻ��Ʊ������Ϣ
	 * ����֤�����£����������ݸ��£�
	 * �˽ӿڴ������ݻ������
	 */
	public static class ResultLatestStockBaseInfo
	{
		public ResultLatestStockBaseInfo()
		{
			error = -1000;
			stockBaseInfo = new StockBaseInfo();
		}
		public int error;
		public StockBaseInfo stockBaseInfo;
	}
	public ResultLatestStockBaseInfo getLatestStockBaseInfo(String id)
	{
		ResultLatestStockBaseInfo cResultLatestStockBaseInfo = new ResultLatestStockBaseInfo();
		
		// �״ν��л���
		if(null == m_cCache.latestStockBaseInfo || !m_cCache.latestStockBaseInfo.containsKey(id))
		{
			if(null == m_cCache.latestStockBaseInfo)
			{
				m_cCache.latestStockBaseInfo = new HashMap<String,StockBaseInfo>();
			}
			
			StockBaseInfo cStockInfo = new StockBaseInfo();
			
			ResultStockBaseData cResultStockBaseData = m_cBaseDataLayer.getBaseInfo(id);
			
			if(0 == cResultStockBaseData.error)
			{
				cStockInfo.name = cResultStockBaseData.stockBaseInfo.name;
				cStockInfo.allMarketValue = cResultStockBaseData.stockBaseInfo.allMarketValue; 
				cStockInfo.circulatedMarketValue = cResultStockBaseData.stockBaseInfo.circulatedMarketValue; 
				cStockInfo.peRatio = cResultStockBaseData.stockBaseInfo.peRatio;
				
				m_cCache.latestStockBaseInfo.put(id, cStockInfo);
			}
			else
			{
				//BLog.error("STOCK", "DataEngine.getBaseInfo error(%d) \n", cResultStockBaseData.error);
			}
		}
			
		// �ӻ�����ȡ����
		if(null != m_cCache.latestStockBaseInfo && m_cCache.latestStockBaseInfo.containsKey(id))
		{
			cResultLatestStockBaseInfo.error = 0;
			cResultLatestStockBaseInfo.stockBaseInfo = m_cCache.latestStockBaseInfo.get(id);
		}
		else
		{
			cResultLatestStockBaseInfo.error = -1;
		}
		
		return cResultLatestStockBaseInfo;
	}
	
	/*
	 * ��ȡĳֻ��Ʊ����ʷ��K����
	 * ����֤�����£����������ݸ��£�
	 * �˽ӿڴ������ݻ������
	 */
	public static class ResultDayKLine {
		public ResultDayKLine()
		{
			error = -1000;
			resultList = new ArrayList<KLine>();
		}
		public int error;
		public List<KLine> resultList;
	}
	public ResultDayKLine getDayKLine(String stockID, String fromDate, String endDate)
	{
		ResultDayKLine cResultDayKLine = new ResultDayKLine();
		
		// �״ν�����ʷ���ݻ���
		if(null == m_cCache.dayKLineList || !m_cCache.dayKLineList.containsKey(stockID))
		{
			if(null == m_cCache.dayKLineList)
			{
				m_cCache.dayKLineList = new HashMap<String,List<KLine>>();
			}
			
			List<KLine> tmpDayKLineList = new ArrayList<KLine>();
			
			ResultKLine cResultKLine = m_cBaseDataLayer.getDayKLineForwardAdjusted(stockID);
			
			if(0 == cResultKLine.error && cResultKLine.resultList.size() != 0)
			{
				m_cCache.dayKLineList.put(stockID, cResultKLine.resultList);
			}
			else
			{
				CLog.error("STOCKDATA", "DataEngine.getDayKDataQianFuQuan(%s %s %s) error(%d) \n", 
						stockID, fromDate, endDate, cResultDayKLine.error);
			}
		}
		
		// �ӻ�����ȡ����
		if(null != m_cCache.dayKLineList && m_cCache.dayKLineList.containsKey(stockID))
		{
			cResultDayKLine.error = 0;
			cResultDayKLine.resultList = StockUtils.subKLineData(m_cCache.dayKLineList.get(stockID),fromDate, endDate);
		}
		else
		{
			cResultDayKLine.error = -1;
		}
		
		return cResultDayKLine;
	}
	
	/*
	 * ��ȡĳֻ��Ʊĳ��ĳʱ���ϸ������
	 * ����֤�����£����������ݸ��£�
	 * �˽ӿڴ������ݻ������
	 */
	public static class ResultDayDetail
	{
		public ResultDayDetail()
		{
			error = -1000;
			resultList = new ArrayList<StockTime>();
		}
		public int error;
		public List<StockTime> resultList;
	}
	public ResultDayDetail getDayDetail(String id, String date, String beginTime, String endTime)
	{
		ResultDayDetail cResultDayDetail = new ResultDayDetail();
		
		// �״ν�����ʷ���ݻ���
		String findKey = id + "_" + date;
		if(null == m_cCache.stockTimeData || !m_cCache.stockTimeData.containsKey(findKey))
		{
			if(null == m_cCache.stockTimeData)
			{
				m_cCache.stockTimeData = new HashMap<String,List<StockTime>>();
			}
			
			List<StockTime> detailDataList = new ArrayList<StockTime>();
			
			ResultDayKLine cResultDayKLine = this.getDayKLine(id, date, date);
			if(0 == cResultDayKLine.error && cResultDayKLine.resultList.size()==1)
			{
				KLine cKLine = cResultDayKLine.resultList.get(0);
				
				if(null != cKLine && date.length() == "0000-00-00".length())
				{
					// load new detail data
					ResultMinKLineOneDay cResultMinKLineOneDay = m_cBaseDataLayer.get1MinKLineOneDay(id, date);
					
					if(0 == cResultMinKLineOneDay.error && cResultMinKLineOneDay.KLineList.size() != 0)
					{
						// ���ڿ����Ǹ�Ȩ��λ����Ҫ���¼�����Լ۸�
						float baseOpenPrice = cKLine.open;
			            //System.out.println("baseOpenPrice:" + baseOpenPrice);  
			            
						float actruaFirstPrice = cResultMinKLineOneDay.KLineList.get(0).open;
						//System.out.println("actruaFirstPrice:" + actruaFirstPrice); 
						
						for(int i = 0; i < cResultMinKLineOneDay.KLineList.size(); i++)  
				        {  
							KLine cMinKLine = cResultMinKLineOneDay.KLineList.get(i);  
//				            System.out.println(cExKData.datetime + "," 
//				            		+ cExKData.open + "," + cExKData.close + "," 
//				            		+ cExKData.low + "," + cExKData.high + "," 
//				            		+ cExKData.volume);  
							
							float actrualprice = cMinKLine.close;
							float changeper = (actrualprice - actruaFirstPrice)/actruaFirstPrice;
							float changedprice = baseOpenPrice + baseOpenPrice * changeper;
							
							// ���������翪�̵�
							if(cMinKLine.time.compareTo("09:31:00") == 0
									|| cMinKLine.time.compareTo("13:01:00") == 0)
							{
								float actrualprice_open = cMinKLine.open;
								float changeper_open = (actrualprice_open - actruaFirstPrice)/actruaFirstPrice;
								float changedprice_open = baseOpenPrice + baseOpenPrice * changeper_open;
								
								StockTime cStockDayDetail = new StockTime();
								cStockDayDetail.price = changedprice_open;
								String openTime = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(cMinKLine.time, -1);
								cStockDayDetail.time = openTime;
								detailDataList.add(cStockDayDetail);
							}
							

							StockTime cStockDayDetail = new StockTime();
							cStockDayDetail.price = changedprice;
							cStockDayDetail.time = cMinKLine.time;
							detailDataList.add(cStockDayDetail);
				        } 
						
						m_cCache.stockTimeData.put(findKey, detailDataList);
					}
				}
			}
		}
		
		// �ӻ�����ȡ����
		if(null != m_cCache.stockTimeData && m_cCache.stockTimeData.containsKey(findKey))
		{
			List<StockTime> cacheList = m_cCache.stockTimeData.get(findKey);
			cResultDayDetail.error = 0;
			cResultDayDetail.resultList = StockUtils.subStockTimeData(cacheList, beginTime, endTime);
		}
		else
		{
			cResultDayDetail.error = -1;
		}
		
		return cResultDayDetail;
	}
	
	
	private BaseDataLayer m_cBaseDataLayer;
	private StockDataEngineCache m_cCache;
}