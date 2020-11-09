package pers.di.dataengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CUtilsDateTime;
import pers.di.localstock.LocalStock;
import pers.di.localstock.common.RealTimeInfoLite;
import pers.di.localstock.common.TimePrice;

public class DACurrentDayTimePriceCache {

	private static class HistoryCacheStockTimeObj
	{
		public HistoryCacheStockTimeObj()
		{
			bGetSucFlag = true;
			timepriceList = new CListObserver<TimePrice>();
		}
		public boolean bGetSucFlag;
		public CListObserver<TimePrice> timepriceList;
	}
	
	public DACurrentDayTimePriceCache()
	{
		m_CurrentDayInterestMinuteDataIDs = new ArrayList<String>();
		m_bIsHistoryData = false;
		m_date = "0000-00-00";
		m_time = "00:00:00";
		m_realtimeCacheStockTimeMap = new HashMap<String, List<TimePrice>>();
		m_historyCacheStockTimeMap = new HashMap<String, HistoryCacheStockTimeObj>();
	}
	
	public void addCurrentDayInterestMinuteDataID(String dataID)
	{
		if(!m_CurrentDayInterestMinuteDataIDs.contains(dataID))
		{
			m_CurrentDayInterestMinuteDataIDs.add(dataID);
		}
	}
	
	public void removeCurrentDayInterestMinuteDataID(String dataID)
	{
		if(m_CurrentDayInterestMinuteDataIDs.contains(dataID))
		{
			m_CurrentDayInterestMinuteDataIDs.remove(dataID);
			m_realtimeCacheStockTimeMap.remove(dataID);
			m_historyCacheStockTimeMap.remove(dataID);
		}
	}
	
	public List<String> getCurrentDayInterestMinuteDataIDs()
	{
		List<String> retList = new ArrayList<String>();
		retList.addAll(m_CurrentDayInterestMinuteDataIDs);
		return retList;
	}
	
	public void buildAll(String date, String time)
	{
		// 确认数据是历史还是实时
		String curRealDate = CUtilsDateTime.GetCurDateStr();
		if(curRealDate.equals(date))
		{
			m_bIsHistoryData = false;
		}
		else
		{
			m_bIsHistoryData = true;
		}
		m_date = date;
		m_time = time;
		
		build(m_CurrentDayInterestMinuteDataIDs);
	}
	
	private void build(List<String> stockIDs)
	{
		if(m_bIsHistoryData)
		{
			for(int i=0; i<m_CurrentDayInterestMinuteDataIDs.size(); i++)
			{
				String stockID = m_CurrentDayInterestMinuteDataIDs.get(i);
				
				HistoryCacheStockTimeObj cHistoryCacheStockTimeObj =  m_historyCacheStockTimeMap.get(stockID);
				if(null == cHistoryCacheStockTimeObj)
				{
					cHistoryCacheStockTimeObj = new HistoryCacheStockTimeObj();
					m_historyCacheStockTimeMap.put(stockID, cHistoryCacheStockTimeObj);
				}
				
				// if current day detail get failed， not try get data next minute
				if(cHistoryCacheStockTimeObj.bGetSucFlag)
				{
					CListObserver<TimePrice> cObsTimePrice = cHistoryCacheStockTimeObj.timepriceList;
					
					if(cObsTimePrice.size() > 0)
					{
						// 缓存中有数据，加载后面的部分
						String beginTime = cObsTimePrice.get(0).time;
						int errObsTimePriceList = LocalStock.instance().buildMinTimePriceListObserver(
								stockID, m_date, 
								beginTime, m_time, cObsTimePrice);
						if(errObsTimePriceList < 0)
						{
							cHistoryCacheStockTimeObj.bGetSucFlag = false;
							CLog.error("DENGINE", "DACurrentDayTimePriceCache error1! (%s %s)", stockID, m_date);
						}
					}
					else
					{
						// 缓存中没数据, 只从历史数据中加载当时时间的
						int errObsTimePriceList = LocalStock.instance().buildMinTimePriceListObserver(
								stockID, m_date, 
								m_time, m_time, cObsTimePrice);
						if(errObsTimePriceList < 0)
						{
							cHistoryCacheStockTimeObj.bGetSucFlag = false;
							CLog.error("DENGINE", "DACurrentDayTimePriceCache error2! (%s %s)", stockID, m_date);
						}
					}
					
					if(cObsTimePrice.size() > 0)
					{
						TimePrice lastTimePrice =  cObsTimePrice.get(cObsTimePrice.size()-1);
						CLog.debug("DENGINE", "MinuteData %s %s (%d)...%.3f", 
								stockID, lastTimePrice.time,
								cObsTimePrice.size(),lastTimePrice.price);
					}
				}
			}
		}
		else
		{
			List<String> stockIDsNeedUpdate = new ArrayList<String>();
			for(int i=0; i<m_CurrentDayInterestMinuteDataIDs.size(); i++)
			{
				String stockID = m_CurrentDayInterestMinuteDataIDs.get(i);
				
				List<TimePrice> cTimePriceList = m_realtimeCacheStockTimeMap.get(stockID);
				if(null == cTimePriceList)
				{
					cTimePriceList = new ArrayList<TimePrice>();
					m_realtimeCacheStockTimeMap.put(stockID, cTimePriceList);
				}
				
				// 判断是否要添加新实时数据
				boolean bAddNewVal = false;
				TimePrice latastTimePrice = null;
				if(cTimePriceList.size() > 0)
				{
					latastTimePrice = cTimePriceList.get(cTimePriceList.size()-1);
				}
				if(null != latastTimePrice)
				{
					String curRealTimeHM = CUtilsDateTime.GetCurTimeStr().substring(0, 5);
					String latestTime = latastTimePrice.time;
					if(latestTime.startsWith(curRealTimeHM))
					{
						// 当前时间，与最后时间没有分钟变化
						bAddNewVal = false;
					}
					else
					{
						// 当前时间，与最后时间有分钟变化
						bAddNewVal = true;
					}
				}
				else
				{
					// 缓存中没数据
					bAddNewVal = true;
				}
				
				if(bAddNewVal)
				{
					stockIDsNeedUpdate.add(stockID);
				}
			}
			
			// 获取新数据并添加cache
			// 添加实时数据
			List<RealTimeInfoLite> ctnRealTimeInfos = new ArrayList<RealTimeInfoLite>();
			int error = LocalStock.instance().loadRealTimeInfo(stockIDsNeedUpdate, ctnRealTimeInfos);
			if(0 == error)
			{
				for(int i=0; i<ctnRealTimeInfos.size(); i++)
				{
					RealTimeInfoLite ctnRealTimeInfo = ctnRealTimeInfos.get(i);
					
					TimePrice cTimePrice = new TimePrice();
					cTimePrice.time = ctnRealTimeInfo.time;
					cTimePrice.price = ctnRealTimeInfo.curPrice;
					
					List<TimePrice> cTimePriceList = m_realtimeCacheStockTimeMap.get(ctnRealTimeInfo.stockID);
					cTimePriceList.add(cTimePrice);
					
					if(cTimePriceList.size() > 0)
					{
						TimePrice lastTimePrice =  cTimePriceList.get(cTimePriceList.size()-1);
						CLog.debug("DENGINE", "MinuteData %s %s (%d)...%.3f", 
								ctnRealTimeInfo.stockID, lastTimePrice.time,
								cTimePriceList.size(), lastTimePrice.price);
					}
				}
			}
		}
	}

	public boolean clear()
	{
		m_CurrentDayInterestMinuteDataIDs.clear();
		m_bIsHistoryData = false;
		m_date = "0000-00-00";
		m_time = "00:00:00";
		m_realtimeCacheStockTimeMap.clear();
		m_historyCacheStockTimeMap.clear();
		return true;
	}
	
	public int buildMinTimePriceListObserver(String stockID,
			CListObserver<TimePrice> observer)
	{
		if(m_bIsHistoryData)
		{
			if(!m_historyCacheStockTimeMap.containsKey(stockID))
			{
				return -1;
			}
			HistoryCacheStockTimeObj cHistoryCacheStockTimeObj =  m_historyCacheStockTimeMap.get(stockID);
			CListObserver<TimePrice> cObsTimePrice = cHistoryCacheStockTimeObj.timepriceList;
			// build观察器
			observer.build(cObsTimePrice);
		}
		else
		{
			if(!m_realtimeCacheStockTimeMap.containsKey(stockID))
			{
				return -1;
			}
			List<TimePrice> cTimePriceList = m_realtimeCacheStockTimeMap.get(stockID);
			
			// build观察器
			observer.build(cTimePriceList);
		}
		return 0;
	}
	
	private List<String> m_CurrentDayInterestMinuteDataIDs;
	private boolean m_bIsHistoryData;
	
	private Map<String, List<TimePrice>> m_realtimeCacheStockTimeMap;
	
	private String m_date;
	private String m_time;
	private Map<String, HistoryCacheStockTimeObj> m_historyCacheStockTimeMap;
}
