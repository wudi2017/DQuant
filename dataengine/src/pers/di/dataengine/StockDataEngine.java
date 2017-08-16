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
	 * 更新所有股票数据
	 * 
	 * localLatestDate 时间戳在全部更新完毕后即更新
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
	 * 强制更新单只股票数据
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
			// 更新单只股票数据 不影响m_cCache.localLatestDate
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
	 * 获取本地所有股票Id列表
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
	 * 获取本地某只股票基本信息
	 */
	public static class ResultStockBaseInfo
	{
		public ResultStockBaseInfo()
		{
			error = -1000;
			stockBaseInfo = new StockBaseInfo();
		}
		public int error;
		public StockBaseInfo stockBaseInfo;
	}
	public ResultStockBaseInfo getStockBaseInfo(String id)
	{
		ResultStockBaseInfo cResultStockBaseInfo = new ResultStockBaseInfo();
		
		// 首次进行缓存
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
			
		// 从缓存中取数据
		if(null != m_cCache.latestStockBaseInfo && m_cCache.latestStockBaseInfo.containsKey(id))
		{
			cResultStockBaseInfo.error = 0;
			cResultStockBaseInfo.stockBaseInfo = m_cCache.latestStockBaseInfo.get(id);
		}
		else
		{
			cResultStockBaseInfo.error = -1;
		}
		
		return cResultStockBaseInfo;
	}
	
	/*
	 * 获取本地某只股票的历史日K数据
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
		
		// 首次进行历史数据缓存
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
		
		// 从缓存中取数据
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
	 * 获取本地某只股票某日内分钟级分时数据
	 * 
	 * 注：日内细节数据构成将从网络获取
	 */
	public static class ResultMinTimePrice
	{
		public ResultMinTimePrice()
		{
			error = -1000;
			resultList = new ArrayList<TimePrice>();
		}
		public int error;
		public List<TimePrice> resultList;
	}
	public ResultMinTimePrice getMinTimePrice(String id, String date, String beginTime, String endTime)
	{
		ResultMinTimePrice cResultMinTimePrice = new ResultMinTimePrice();
		
		// 首次进行历史数据缓存
		String findKey = id + "_" + date;
		if(null == m_cCache.stockTimeData || !m_cCache.stockTimeData.containsKey(findKey))
		{
			if(null == m_cCache.stockTimeData)
			{
				m_cCache.stockTimeData = new HashMap<String,List<TimePrice>>();
			}
			
			List<TimePrice> detailDataList = new ArrayList<TimePrice>();
			
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
						// 由于可能是复权价位，需要重新计算相对价格
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
							
							// 添加上下午开盘点
							if(cMinKLine.time.compareTo("09:31:00") == 0
									|| cMinKLine.time.compareTo("13:01:00") == 0)
							{
								float actrualprice_open = cMinKLine.open;
								float changeper_open = (actrualprice_open - actruaFirstPrice)/actruaFirstPrice;
								float changedprice_open = baseOpenPrice + baseOpenPrice * changeper_open;
								
								TimePrice cStockDayDetail = new TimePrice();
								cStockDayDetail.price = changedprice_open;
								String openTime = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(cMinKLine.time, -1);
								cStockDayDetail.time = openTime;
								detailDataList.add(cStockDayDetail);
							}
							

							TimePrice cStockDayDetail = new TimePrice();
							cStockDayDetail.price = changedprice;
							cStockDayDetail.time = cMinKLine.time;
							detailDataList.add(cStockDayDetail);
				        } 
						
						m_cCache.stockTimeData.put(findKey, detailDataList);
					}
				}
			}
		}
		
		// 从缓存中取数据
		if(null != m_cCache.stockTimeData && m_cCache.stockTimeData.containsKey(findKey))
		{
			List<TimePrice> cacheList = m_cCache.stockTimeData.get(findKey);
			cResultMinTimePrice.error = 0;
			cResultMinTimePrice.resultList = StockUtils.subTimePriceData(cacheList, beginTime, endTime);
		}
		else
		{
			cResultMinTimePrice.error = -1;
		}
		
		return cResultMinTimePrice;
	}
	
	
	private BaseDataLayer m_cBaseDataLayer;
	private StockDataEngineCache m_cCache;
}
