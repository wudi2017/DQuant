package pers.di.quantengine.dataaccessor;

import java.util.*;

import pers.di.common.*;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.common.*;

public class RealtimeCache {
	
	public RealtimeCache()
	{
		m_curDate = "0000-00-00";
		m_cacheStockTimeMap = 
				new HashMap<String, List<TimePrice>>();
	}
	
	public void build(String date, String time)
	{
		
	}
	
	public void subscribe(String stockID)
	{
		
	}
	
	public void unSubscribeAll()
	{
		
	}

	public boolean clear()
	{
		m_curDate = "0000-00-00";
		m_cacheStockTimeMap.clear();
		return true;
	}
	
	public int buildMinTimePriceListObserver(String id, String date,
			CListObserver<TimePrice> observer)
	{
		// ��������
		if(date.compareTo(m_curDate) != 0)
		{
			clear();
		}
		if(!m_cacheStockTimeMap.containsKey(id))
		{
			m_cacheStockTimeMap.put(id, new ArrayList<TimePrice>());
		}
		List<TimePrice> cTimePriceList = m_cacheStockTimeMap.get(id);
		
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
		
		// ���ʵʱ����
		RealTimeInfo ctnRealTimeInfo = new RealTimeInfo();
		int error = StockDataEngine.instance().loadRealTimeInfo(id, ctnRealTimeInfo);
		if(0 == error)
		{
			TimePrice cTimePrice = new TimePrice();
			cTimePrice.time = ctnRealTimeInfo.time;
			cTimePrice.price = ctnRealTimeInfo.curPrice;
			cTimePriceList.add(cTimePrice);
		}
		
		// ��������
		m_curDate = date;
		
		// build�۲���
		observer.build(cTimePriceList);
	
		return 0;
	}
	
	private String m_curDate;
	private Map<String, List<TimePrice>> m_cacheStockTimeMap;
}
