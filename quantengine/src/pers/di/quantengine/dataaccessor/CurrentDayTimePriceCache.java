package pers.di.quantengine.dataaccessor;

import java.util.*;

import pers.di.common.*;
import pers.di.dataapi.common.RealTimeInfo;
import pers.di.dataapi.common.TimePrice;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.common.*;

public class CurrentDayTimePriceCache {
	
	public CurrentDayTimePriceCache()
	{
		m_subscribeMinuteDataStockIDList = new ArrayList<String>();
		m_bIsHistoryData = false;
		m_date = "0000-00-00";
		m_time = "00:00:00";
		m_realtimeCacheStockTimeMap = new HashMap<String, List<TimePrice>>();
		m_historyCacheStockTimeMap = new HashMap<String, CListObserver<TimePrice>>();
	}
	
	public boolean subscribeMinuteData(String StockID)
	{
		return m_subscribeMinuteDataStockIDList.add(StockID);
	}
	
	public void buildAll(String date, String time)
	{
		// ȷ����������ʷ����ʵʱ
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
		
		for(int i=0; i<m_subscribeMinuteDataStockIDList.size(); i++)
		{
			String stockID = m_subscribeMinuteDataStockIDList.get(i);
			build(stockID);
		}
	}
	
	private void build(String stockID)
	{
		if(m_bIsHistoryData)
		{
			CListObserver<TimePrice> cObsTimePrice = m_historyCacheStockTimeMap.get(stockID);
			if(null == cObsTimePrice)
			{
				cObsTimePrice = new CListObserver<TimePrice>();
				m_historyCacheStockTimeMap.put(stockID, cObsTimePrice);
			}
			
			if(cObsTimePrice.size() > 0)
			{
				// �����������ݣ����غ���Ĳ���
				String beginTime = cObsTimePrice.get(0).time;
				int errObsTimePriceList = StockDataEngine.instance().buildMinTimePriceListObserver(
						stockID, m_date, 
						beginTime, m_time, cObsTimePrice);
			}
			else
			{
				// ������û����, ֻ����ʷ�����м��ص�ʱʱ���
				int errObsTimePriceList = StockDataEngine.instance().buildMinTimePriceListObserver(
						stockID, m_date, 
						m_time, m_time, cObsTimePrice);
			}
		}
		else
		{
			List<TimePrice> cTimePriceList = m_realtimeCacheStockTimeMap.get(stockID);
			if(null == cTimePriceList)
			{
				cTimePriceList = new ArrayList<TimePrice>();
				m_realtimeCacheStockTimeMap.put(stockID, cTimePriceList);
			}
			
			// �ж��Ƿ�Ҫ�����ʵʱ����
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
					// ��ǰʱ�䣬�����ʱ��û�з��ӱ仯
					bAddNewVal = false;
				}
				else
				{
					// ��ǰʱ�䣬�����ʱ���з��ӱ仯
					bAddNewVal = true;
				}
			}
			else
			{
				// ������û����
				bAddNewVal = true;
			}
			
			if(bAddNewVal)
			{
				// ���ʵʱ����
				RealTimeInfo ctnRealTimeInfo = new RealTimeInfo();
				int error = StockDataEngine.instance().loadRealTimeInfo(stockID, ctnRealTimeInfo);
				if(0 == error)
				{
					TimePrice cTimePrice = new TimePrice();
					cTimePrice.time = ctnRealTimeInfo.time;
					cTimePrice.price = ctnRealTimeInfo.curPrice;
					cTimePriceList.add(cTimePrice);
				}
			}
		}
	}

	public boolean clear()
	{
		m_subscribeMinuteDataStockIDList.clear();
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
			CListObserver<TimePrice> cObsTimePrice = m_historyCacheStockTimeMap.get(stockID);
			// build�۲���
			observer.build(cObsTimePrice);
		}
		else
		{
			if(!m_realtimeCacheStockTimeMap.containsKey(stockID))
			{
				return -1;
			}
			List<TimePrice> cTimePriceList = m_realtimeCacheStockTimeMap.get(stockID);
			
			// build�۲���
			observer.build(cTimePriceList);
		}
		return 0;
	}
	
	private List<String> m_subscribeMinuteDataStockIDList;
	private boolean m_bIsHistoryData;
	
	private Map<String, List<TimePrice>> m_realtimeCacheStockTimeMap;
	
	private String m_date;
	private String m_time;
	private Map<String, CListObserver<TimePrice>> m_historyCacheStockTimeMap;
}
