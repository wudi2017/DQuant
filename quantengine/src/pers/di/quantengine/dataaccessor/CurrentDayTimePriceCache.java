package pers.di.quantengine.dataaccessor;

import java.util.*;

import pers.di.common.*;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.common.*;

public class CurrentDayTimePriceCache {
	
	public CurrentDayTimePriceCache()
	{
		m_bIsHistoryData = false;
		m_date = "";
		m_time = "";
		m_realtimeCacheStockTimeMap = new HashMap<String, List<TimePrice>>();
		m_historyCacheStockTimeMap = new HashMap<String, CListObserver<TimePrice>>();
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
		
		
		if(m_bIsHistoryData)
		{
			for (Map.Entry<String, CListObserver<TimePrice>> entry : m_historyCacheStockTimeMap.entrySet()) {  
				String stockID = entry.getKey();
				build(stockID);
			}
		}
		else
		{
			for (Map.Entry<String, List<TimePrice>> entry : m_realtimeCacheStockTimeMap.entrySet()) {  
				String stockID = entry.getKey();
				build(stockID);
			}
		}
		
	}
	
	private void build(String stockID)
	{
		if(m_bIsHistoryData)
		{
			CListObserver<TimePrice> cObsTimePrice = m_historyCacheStockTimeMap.get(stockID);
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
			
			// �ж��Ƿ�Ҫ�����ʵʱ����
			boolean bAddNewVal = false;
			TimePrice latastTimePrice = null;
			if(cTimePriceList.size() > 0)
			{
				latastTimePrice = cTimePriceList.get(cTimePriceList.size()-1);
			}
			if(null != latastTimePrice)
			{
				String curRealTimeHM = CUtilsDateTime.GetCurTimeStrHM();
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
				m_historyCacheStockTimeMap.put(stockID, new CListObserver<TimePrice>());
				build(stockID);
			}
			CListObserver<TimePrice> cObsTimePrice = m_historyCacheStockTimeMap.get(stockID);
			// build�۲���
			observer.build(cObsTimePrice);
		}
		else
		{
			if(!m_realtimeCacheStockTimeMap.containsKey(stockID))
			{
				m_realtimeCacheStockTimeMap.put(stockID, new ArrayList<TimePrice>());
				build(stockID);
			}
			List<TimePrice> cTimePriceList = m_realtimeCacheStockTimeMap.get(stockID);
			
			// build�۲���
			observer.build(cTimePriceList);
		}
		return 0;
	}
	
	private boolean m_bIsHistoryData;
	
	private Map<String, List<TimePrice>> m_realtimeCacheStockTimeMap;
	
	private String m_date;
	private String m_time;
	private Map<String, CListObserver<TimePrice>> m_historyCacheStockTimeMap;
}
