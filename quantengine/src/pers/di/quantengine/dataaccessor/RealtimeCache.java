package pers.di.quantengine.dataaccessor;

import java.util.*;

import pers.di.common.*;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.common.*;

public class RealtimeCache {
	
	public RealtimeCache()
	{
		m_cacheStockTimeMap = new HashMap<String, List<TimePrice>>();
	}
	
	public void buildAll()
	{
		for (Map.Entry<String, List<TimePrice>> entry : m_cacheStockTimeMap.entrySet()) {  
			String stockID = entry.getKey();
			build(stockID);
		}
	}
	
	private void build(String stockID)
	{
		List<TimePrice> cTimePriceList = m_cacheStockTimeMap.get(stockID);
		
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

	public boolean clear()
	{
		m_cacheStockTimeMap.clear();
		return true;
	}
	
	public int buildMinTimePriceListObserver(String stockID,
			CListObserver<TimePrice> observer)
	{
		if(!m_cacheStockTimeMap.containsKey(stockID))
		{
			m_cacheStockTimeMap.put(stockID, new ArrayList<TimePrice>());
			build(stockID);
		}
		List<TimePrice> cTimePriceList = m_cacheStockTimeMap.get(stockID);
		
		// build�۲���
		observer.build(cTimePriceList);
	
		return 0;
	}
	
	private Map<String, List<TimePrice>> m_cacheStockTimeMap;
}
