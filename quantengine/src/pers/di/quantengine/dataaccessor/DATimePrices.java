package pers.di.quantengine.dataaccessor;

import pers.di.common.*;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.common.*;

public class DATimePrices {
	
	public DATimePrices(DAPool pool, String stockID, String date)
	{
		m_obsTimePriceList = new CListObserver<TimePrice>();
		int errObsTimePriceList = StockDataEngine.instance().buildMinTimePriceListObserver(
				stockID, pool.date(), 
				"09:00:00", pool.time(), m_obsTimePriceList);
		
		// 引擎中没有数据，并且需要获取的是真实当日数据，从实时缓存中拿
		if(0 != errObsTimePriceList
				&& date.equals(CUtilsDateTime.GetCurDateStr()))
		{
			RealtimeCache rtc = pool.realtimeCache();
		}
	}
	public int size()
	{
		return m_obsTimePriceList.size();
	}
	public TimePrice get(int i)
	{
		return  m_obsTimePriceList.get(i);
	}
	
	private CListObserver<TimePrice> m_obsTimePriceList;
}
