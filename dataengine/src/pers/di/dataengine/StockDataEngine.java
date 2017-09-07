package pers.di.dataengine;

import java.util.*;

import pers.di.common.*;
import pers.di.dataengine.BaseDataDownload.ResultUpdateStock;
import pers.di.dataengine.BaseDataLayer.ResultMinKLineOneDay;
import pers.di.dataengine.BaseDataStorage.ResultAllStockFullDataTimestamps;
import pers.di.dataengine.BaseDataStorage.ResultStockBaseData;
import pers.di.dataengine.common.*;
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
	 * 注意： 内部为引用存储，数据更新后返回值无效
	 */
	public static class DEStockIDs
	{
		public DEStockIDs(int error, List<String> origin)
		{
			m_error = error;
			m_resultList = origin;
		}
		public int error() 
		{ 
			return m_error; 
		}
		public int size() 
		{ 
			return m_resultList.size();
		}
		public String get(int i)
		{
			return m_resultList.get(i);
		}
		private int m_error;
		private List<String> m_resultList;
	}
	public DEStockIDs getAllStockIDs()
	{
		DEStockIDs cDEStockIDs = null;
		
		if(null != m_cCache.AllStockID)
		{
			cDEStockIDs = new DEStockIDs(0, m_cCache.AllStockID);
		}
		else
		{
			m_cCache.AllStockID = new ArrayList<String>();
			
			ArrayList<StockItem> container = new ArrayList<StockItem>();
			int error = m_cBaseDataLayer.getLocalAllStock(container);
			if(0 == error)
			{
				for(int i=0; i<container.size();i++)
				{
					String stockId = container.get(i).id;
					m_cCache.AllStockID.add(stockId);
				}
				cDEStockIDs = new DEStockIDs(0, m_cCache.AllStockID);
			}
			else
			{
				CLog.error("STOCK", "DataEngine.getLocalAllStock error(%d) \n", error);
			}
		}
		return cDEStockIDs;
	}
	
	/*
	 * 获取本地某只股票基本信息
	 * 注意： 内部为引用存储，数据更新后返回值无效
	 */
	public static class DEStockBaseInfo
	{
		public DEStockBaseInfo(int error, StockBaseInfo origin)
		{
			m_error = error;
			m_stockBaseInfo = origin;
		}
		public int error() 
		{ 
			return m_error; 
		}
		public StockBaseInfo get()
		{
			return m_stockBaseInfo;
		}
		private int m_error;
		private StockBaseInfo m_stockBaseInfo;
	}
	public DEStockBaseInfo getStockBaseInfo(String id)
	{
		DEStockBaseInfo cDEStockBaseInfo = null;
		
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
			cDEStockBaseInfo = new DEStockBaseInfo(0, m_cCache.latestStockBaseInfo.get(id));
		}
		else
		{
			cDEStockBaseInfo = new DEStockBaseInfo(-1, null);
		}
		
		return cDEStockBaseInfo;
	}
	
	/*
	 * 获取本地某只股票的历史日K数据
	 * 注意： 内部为引用存储，数据更新后返回值无效
	 */
	public static class DEKLines {
		public DEKLines(int error, List<KLine> origin, int iBase, int iSize)
		{
			m_error = error;
			m_resultList = origin;
			m_iBase = iBase;
			m_iSize= iSize;
		}
		public int error()
		{
			return m_error;
		}
		public int size()
		{
			return m_iSize;
		}
		public KLine get(int i)
		{
			return m_resultList.get(m_iBase+i);
		}
		public List<KLine> origin()
		{
			return m_resultList;
		}
		private int m_error;
		private List<KLine> m_resultList;
		private int m_iBase;
		private int m_iSize;
	}
	public DEKLines getDayKLines(String stockID, String fromDate, String toDate)
	{
		DEKLines cDEKLines = null;
		
		// 首次进行历史数据缓存
		if(null == m_cCache.dayKLineList || !m_cCache.dayKLineList.containsKey(stockID))
		{
			if(null == m_cCache.dayKLineList)
			{
				m_cCache.dayKLineList = new HashMap<String,List<KLine>>();
			}
			
			List<KLine> tmpDayKLineList = new ArrayList<KLine>();
			
			ResultKLine cResultKLine = m_cBaseDataLayer.getDayKLinesForwardAdjusted(stockID);
			
			if(0 == cResultKLine.error && cResultKLine.resultList.size() != 0)
			{
				m_cCache.dayKLineList.put(stockID, cResultKLine.resultList);
			}
			else
			{
				CLog.error("STOCKDATA", "DataEngine.getDayKDataQianFuQuan(%s %s %s) error(%d) \n", 
						stockID, fromDate, toDate, cResultKLine.error);
			}
		}
		
		// 从缓存中取数据
		if(null != m_cCache.dayKLineList && m_cCache.dayKLineList.containsKey(stockID))
		{
			int iBase = -1;
			int iSize = 0;
			List<KLine> cur_dayKLineList = m_cCache.dayKLineList.get(stockID);
			if(fromDate.compareTo(toDate) <= 0)
			{
				for(int i = 0; i <cur_dayKLineList.size(); i++)  
		        {  
					KLine cKLine = cur_dayKLineList.get(i);  
					if(-1 == iBase && cKLine.date.compareTo(fromDate) >= 0)
					{
						iBase = i;
					}
					if(cKLine.date.compareTo(fromDate) >= 0 && cKLine.date.compareTo(toDate) <= 0)
					{
						iSize++;
					}
		        }
			}
			cDEKLines = new DEKLines(0, cur_dayKLineList, iBase, iSize);
		}
		else
		{
			cDEKLines = new DEKLines(-1, null, 0, 0);
		}
		
		return cDEKLines;
	}
	
	/*
	 * 获取本地某只股票某日内分钟级分时数据
	 * 
	 * 注：日内细节数据构成将从网络获取
	 * 注意： 内部为引用存储，数据更新后返回值无效
	 */
	public static class DETimePrices
	{
		public DETimePrices(int error, List<TimePrice> origin, int iBase, int iSize)
		{
			m_error = error;
			m_resultList = origin;
			m_iBase = iBase;
			m_iSize = iSize;
		}
		public int error()
		{
			return m_error;
		}
		public int size()
		{
			return m_iSize;
		}
		public TimePrice get(int i)
		{
			return m_resultList.get(m_iBase+i);
		}
		private int m_error;
		private List<TimePrice> m_resultList;
		private int m_iBase;
		private int m_iSize;
	}
	public DETimePrices getMinTimePrices(String id, String date, String beginTime, String endTime)
	{
		DETimePrices cDETimePrices = null;
		
		// 首次进行历史数据缓存
		String findKey = id + "_" + date;
		if(null == m_cCache.stockTimeData || !m_cCache.stockTimeData.containsKey(findKey))
		{
			if(null == m_cCache.stockTimeData)
			{
				m_cCache.stockTimeData = new HashMap<String,List<TimePrice>>();
			}
			
			List<TimePrice> detailDataList = new ArrayList<TimePrice>();
			
			DEKLines cDEKLines = this.getDayKLines(id, date, date);
			if(0 == cDEKLines.error() && cDEKLines.size()==1)
			{
				KLine cKLine = cDEKLines.get(0);
				
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
			int iBase = -1;
			int iSize = 0;
			List<TimePrice> cur_stockTimeData = m_cCache.stockTimeData.get(findKey);
			if(beginTime.compareTo(endTime) <= 0)
			{
				for(int i = 0; i <cur_stockTimeData.size(); i++)  
		        {  
					TimePrice cTimePrice = cur_stockTimeData.get(i);  
					if(-1 == iBase && cTimePrice.time.compareTo(beginTime) >= 0)
					{
						iBase = i;
					}
					if(cTimePrice.time.compareTo(beginTime) >= 0 && cTimePrice.time.compareTo(endTime) <= 0)
					{
						iSize++;
					}
		        }
			}
			cDETimePrices = new DETimePrices(0 , cur_stockTimeData, iBase, iSize);
		}
		else
		{
			cDETimePrices = new DETimePrices(0 , null, 0, 0);
		}
		
		return cDETimePrices;
	}
	
	/*
	 * 获取某只股票某天某时间的价格
	 * 数据缓存机制依赖于getDayDetail接口
	 */
	public static class DETimePrice
	{
		public DETimePrice(int error, String date, TimePrice origin)
		{
			m_error = error;
			m_date = date;
			m_resultTimePrice = origin;
		}
		public int error()
		{
			return m_error;
		}
		public String date()
		{
			return m_date;
		}
		public String time()
		{
			return m_resultTimePrice.time;
		}
		public float price()
		{
			return m_resultTimePrice.price;
		}
		public int m_error;
		public String m_date;
		private TimePrice m_resultTimePrice;
	}
	public DETimePrice getRealTimePrice(String id)
	{
		DETimePrice cDETimePrice = null;
		
		RealTimeInfo ctnRealTimeInfo = new RealTimeInfo();
		int error = m_cBaseDataLayer.getRealTimeInfo(id, ctnRealTimeInfo);
		
		if(0 == error)
		{
			TimePrice cTimePrice = new TimePrice();
			cTimePrice.time = ctnRealTimeInfo.time;
			cTimePrice.price = ctnRealTimeInfo.curPrice;

			if(0 == Float.compare(cTimePrice.price, 0.00f))
			{
				cDETimePrice = new DETimePrice(-2, null, null);
				CLog.error("STOCKDATA", "getStockTime %s price 0.00f!", id); // 修正取得实时价格为0则认为错误
			}
			else
			{
				cDETimePrice = new DETimePrice(0,
						ctnRealTimeInfo.date,
						cTimePrice);
			}
		}
		else
		{
			cDETimePrice = new DETimePrice(-1, null, null);
		}
		
		return cDETimePrice;
	}
	
	private BaseDataLayer m_cBaseDataLayer;
	private StockDataEngineCache m_cCache;
}
