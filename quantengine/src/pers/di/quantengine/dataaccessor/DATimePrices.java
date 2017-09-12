package pers.di.quantengine.dataaccessor;

import pers.di.common.*;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.common.*;

public class DATimePrices {
	
	public DATimePrices(DAPool pool, String stockID, String date)
	{
		m_obsTimePriceList = new CListObserver<TimePrice>();
		int errObsTimePriceList = -1;
		int cmp = date.compareTo(pool.date());
		if(cmp < 0)
		{
			// 获取日期是测试日期之前的天，加载引擎数据，全天数据build
			errObsTimePriceList = StockDataEngine.instance().buildMinTimePriceListObserver(
					stockID, date, 
					"09:30:00", "15:00:00", m_obsTimePriceList);
		}
		else if(cmp == 0)
		{
			// 获取日期在测试日期当天，只build测试时间之前的部分 
			errObsTimePriceList = StockDataEngine.instance().buildMinTimePriceListObserver(
					stockID, date, 
					"09:30:00", pool.time(), m_obsTimePriceList);
			
			// 引擎中没有数据，并且需要获取的是真实当日数据，从实时缓存中构建
			if(0 != errObsTimePriceList)
			{
				String curRealDate = CUtilsDateTime.GetCurDateStr();
				if(date.equals(curRealDate))
				{
					pool.realtimeCache().buildMinTimePriceListObserver(
							stockID, 
							m_obsTimePriceList);
				}
			}
		}
		else
		{
			// 获取日期在测试日期之后，不进行build
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
