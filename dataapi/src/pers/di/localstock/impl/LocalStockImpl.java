package pers.di.localstock.impl;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CObjectContainer;
import pers.di.common.CObjectObserver;
import pers.di.common.CUtilsDateTime;
import pers.di.common.CUtilsMath;
import pers.di.localstock.ILocalStock;
import pers.di.localstock.common.KLine;
import pers.di.localstock.common.RealTimeInfoLite;
import pers.di.localstock.common.StockInfo;
import pers.di.localstock.common.StockItem;
import pers.di.localstock.common.TimePrice;

public class LocalStockImpl implements ILocalStock {
	public LocalStockImpl(String rootDir) 
	{
		m_cBaseDataLayer = new BaseDataLayer(rootDir);
		m_cCache = new StockDataApiCache();
	}
	
	@Override
	public boolean resetDataRoot(String dateRoot)
	{
		m_cCache.clearAllCache();
		return m_cBaseDataLayer.resetDataRoot(dateRoot);
	}
	
	@Override
	public String dataRoot()
	{
		return m_cBaseDataLayer.dataRoot();
	}
	
	/*
	 * 更新所有股票数据
	 * 
	 * localLatestDate 时间戳在全部更新完毕后即更新
	 */
	@Override
	public int updateAllLocalStocks(String dateStr)
	{
		if(null == m_cCache.localLatestDate)
		{
			CLog.output("DATAAPI","StockDataApi.getUpdatedStocksDate\n");
			CObjectContainer<String> ctnAllStockFullDataTimestamps = new CObjectContainer<String>();
			int errAllStockFullDataTimestamps = m_cBaseDataLayer.getAllStockFullDataTimestamps(ctnAllStockFullDataTimestamps);
			if(0 == errAllStockFullDataTimestamps)
			{
				m_cCache.localLatestDate = ctnAllStockFullDataTimestamps.get();
			}
			else
			{
				m_cCache.localLatestDate = "0000-00-00";
				CLog.output("DATAAPI", "StockDataApi.getUpdatedStocksDate failed, reset to %s \n", m_cCache.localLatestDate);
			}
		}
		
		if(m_cCache.localLatestDate.compareTo(dateStr) >= 0)
		{
			CLog.output("DATAAPI", "update success! (current is newest, local: %s)\n", m_cCache.localLatestDate);
		}
		else
		{
			int iUpdateCnt = m_cBaseDataLayer.updateLocalAllStocKLine(dateStr);
			CLog.output("DATAAPI", "update success to date: %s (count: %d)\n", m_cCache.localLatestDate, iUpdateCnt);
			m_cCache.clearAllCache();
		}
		
		return 0;
	}
	
	/*
	 * 强制更新单只股票数据
	 */
	@Override
	public int updateLocalStocks(String stockID, String dateStr)
	{
		if(null == m_cCache.localLatestDate)
		{
			CLog.output("DATAAPI","StockDataApi.getUpdatedStocksDate\n");
			CObjectContainer<String> ctnAllStockFullDataTimestamps = new CObjectContainer<String>();
			int errAllStockFullDataTimestamps = m_cBaseDataLayer.getAllStockFullDataTimestamps(ctnAllStockFullDataTimestamps);
			if(0 == errAllStockFullDataTimestamps)
			{
				m_cCache.localLatestDate = ctnAllStockFullDataTimestamps.get();
			}
			else
			{
				CLog.output("DATAAPI", "StockDataApi.getUpdatedStocksDate failed! \n", m_cCache.localLatestDate);
			}
		}
		
		if(m_cCache.localLatestDate.compareTo(dateStr) >= 0)
		{
			CLog.output("DATAAPI", "update %s success! (current is newest, local: %s)\n",stockID, m_cCache.localLatestDate);
		}
		else
		{
			// 更新单只股票数据 不影响m_cCache.localLatestDate
			CObjectContainer<Integer> ctnCount = new CObjectContainer<Integer>();
			int errCount = m_cBaseDataLayer.updateLocaStocKLine(stockID, ctnCount);
			
			if(0 == errCount)
			{
				CLog.output("DATAAPI", "update %s success to date: %s (count: %d)\n", stockID, ctnCount.get());
			}
			else
			{
				CLog.error("STOCK", "update %s failed \n", errCount);
			}
		}
		return 0;
	}
	
	/*
	 * 构建所有股股票ID观察器
	 * 
	 * 返回值：
	 *     返回0为成功，其他值为失败
	 * 参数：
	 *     observer 观察器
	 */
	@Override
	public int buildAllStockIDObserver(CListObserver<String> observer)
	{
		int error = 0;
		if(m_cCache.AllStockID().size() != 0)
		{
			observer.build(m_cCache.AllStockID());
		}
		else
		{
			ArrayList<StockItem> container = new ArrayList<StockItem>();
			int errStockItem = m_cBaseDataLayer.getLocalAllStock(container);
			if(0 == errStockItem)
			{
				for(int i=0; i<container.size();i++)
				{
					String stockId = container.get(i).id;
					m_cCache.AllStockID().add(stockId);
				}
				error = 0;
				observer.build(m_cCache.AllStockID());
			}
			else
			{
				CLog.error("STOCK", "DataEngine.getLocalAllStock error(%d) \n", error);
			}
		}
		return error;
	}
	
	/*
	 * 构建某只股票信息观察器
	 * 
	 * 返回值：
	 *     返回0为成功，其他值为失败
	 * 参数：
	 *     observer 观察器
	 */
	@Override
	public int buildStockInfoObserver(String id, CObjectObserver<StockInfo> observer)
	{
		int error = 0;
		
		// 首次进行缓存
		if(!m_cCache.contains_latestStockInfo(id))
		{
			StockInfo ctnStockInfo = new StockInfo();
			int errStockInfo = m_cBaseDataLayer.getStockInfo(id, ctnStockInfo);
			
			if(0 == errStockInfo)
			{
				m_cCache.set_latestStockInfo(id, ctnStockInfo);
			}
			else
			{
				//BLog.error("STOCK", "DataEngine.getBaseInfo error(%d) \n", cResultStockBaseData.error);
			}
		}
			
		// 从缓存中取数据
		if(m_cCache.contains_latestStockInfo(id))
		{
			observer.build(m_cCache.get_latestStockInfo(id));
		}
		else
		{
			error = -1;
		}
		
		return error;
	}
	
	/*
	 * 构建某只股票日K线列表观察器
	 * 
	 * 返回值：
	 *     返回0为成功，其他值为失败
	 * 参数：
	 *     observer 观察器
	 */
	@Override
	public int buildDayKLineListObserver(String stockID, String fromDate, String toDate, CListObserver<KLine> observer)
	{
		int error = 0;
		
		// 首次进行历史数据缓存
		if(!m_cCache.contains_dayKLine(stockID))
		{
			List<KLine> cntKLine = new ArrayList<KLine>();
			int errKLine = m_cBaseDataLayer.getDayKLinesForwardAdjusted(stockID, cntKLine);
			
			if(0 == errKLine && cntKLine.size() != 0)
			{
				m_cCache.set_dayKLine(stockID, cntKLine);
			}
			else
			{
				CLog.error("STOCKDATA", "DataEngine.getDayKDataQianFuQuan(%s %s %s) error(%d) \n", 
						stockID, fromDate, toDate, errKLine);
			}
		}
		
		// 从缓存中取数据
		if(m_cCache.contains_dayKLine(stockID))
		{
			int iBase = -1;
			int iSize = 0;
			List<KLine> cur_dayKLineList = m_cCache.get_dayKLine(stockID);
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
			observer.build(cur_dayKLineList, iBase, iSize);
		}
		else
		{
			error = -1;
		}
		
		return error;
	}
	
	/*
	 * 构建某只股票日内分钟分时线观察器
	 * 
	 * 返回值：
	 *     返回0为成功，其他值为失败
	 * 参数：
	 *     observer 观察器
	 */
	@Override
	public int buildMinTimePriceListObserver(String id, String date, 
			String beginTime, String endTime, CListObserver<TimePrice> observer)
	{
		int error = 0;
		
		// 首次进行历史数据缓存
		String findKey = id + "_" + date;
		if(!m_cCache.contains_stockTimeData(findKey))
		{
			List<TimePrice> detailDataList = new ArrayList<TimePrice>();
			
			CListObserver<KLine> obsDayKLineList = new CListObserver<KLine>();
			int errObsDayKLineList = this.buildDayKLineListObserver(id, date, date, obsDayKLineList);
			if(0 == errObsDayKLineList && obsDayKLineList.size()==1)
			{
				KLine cKLine = obsDayKLineList.get(0);
				
				if(null != cKLine && date.length() == "0000-00-00".length())
				{
					// load new detail data
					List<KLine> ctnKLine = new ArrayList<KLine>();
					int errKLine= m_cBaseDataLayer.get1MinKLineOneDay(id, date, ctnKLine);
					
					if(0 == errKLine && ctnKLine.size() != 0)
					{
						// 由于可能是复权价位，需要重新计算相对价格
						double baseOpenPrice = cKLine.open;
			            //System.out.println("baseOpenPrice:" + baseOpenPrice);  
			            
						double actruaFirstPrice = ctnKLine.get(0).open;
						//System.out.println("actruaFirstPrice:" + actruaFirstPrice); 
						
						for(int i = 0; i < ctnKLine.size(); i++)  
				        {  
							KLine cMinKLine = ctnKLine.get(i);  
//				            System.out.println(cExKData.datetime + "," 
//				            		+ cExKData.open + "," + cExKData.close + "," 
//				            		+ cExKData.low + "," + cExKData.high + "," 
//				            		+ cExKData.volume);  
							
							double actrualprice = cMinKLine.close;
							double changeper = (actrualprice - actruaFirstPrice)/actruaFirstPrice;
							double changedprice = baseOpenPrice + baseOpenPrice * changeper;
							
							// 添加上下午开盘点
							if(cMinKLine.time.compareTo("09:31:00") == 0
									|| cMinKLine.time.compareTo("13:01:00") == 0)
							{
								double actrualprice_open = cMinKLine.open;
								double changeper_open = (actrualprice_open - actruaFirstPrice)/actruaFirstPrice;
								double changedprice_open = baseOpenPrice + baseOpenPrice * changeper_open;
								
								TimePrice cStockDayDetail = new TimePrice();
								cStockDayDetail.price = changedprice_open;
								String openTime = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(cMinKLine.time, -1*60);
								cStockDayDetail.time = openTime;
								detailDataList.add(cStockDayDetail);
							}
							

							TimePrice cStockDayDetail = new TimePrice();
							cStockDayDetail.price = CUtilsMath.saveNDecimal(changedprice, 2);
							cStockDayDetail.time = cMinKLine.time;
							detailDataList.add(cStockDayDetail);
				        } 
						
						m_cCache.set_stockTimeData(findKey, detailDataList);
					}
				}
			}
		}
		
		// 从缓存中取数据
		if( m_cCache.contains_stockTimeData(findKey))
		{
			int iBase = -1;
			int iSize = 0;
			List<TimePrice> cur_stockTimeData = m_cCache.get_stockTimeData(findKey);
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
			
			observer.build(cur_stockTimeData, iBase, iSize);
		}
		else
		{
			error = -1;
		}
		
		return error;
	}
	
	/*
	 * 装载某只股票实时信息容器
	 * 
	 * 返回值：
	 *     返回0为成功，其他值为失败
	 * 参数：
	 *     container 
	 */
	@Override
	public int loadRealTimeInfo(List<String> stockIDs, List<RealTimeInfoLite> container)
	{
		return m_cBaseDataLayer.getRealTimeInfo(stockIDs, container);
	}
	
	private BaseDataLayer m_cBaseDataLayer;
	private StockDataApiCache m_cCache;
}
