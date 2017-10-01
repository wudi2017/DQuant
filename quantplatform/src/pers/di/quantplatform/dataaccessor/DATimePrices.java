package pers.di.quantplatform.dataaccessor;

import pers.di.common.CListObserver;
import pers.di.dataapi.common.*;
import pers.di.dataapi.*;

public class DATimePrices {

	public DATimePrices(DAPool pool, String stockID, String date)
	{
		m_obsTimePriceList = new CListObserver<TimePrice>();
		int errObsTimePriceList = -1;
		int cmp = date.compareTo(pool.date());
		if(cmp < 0)
		{
			// 获取日期是测试日期之前的天，加载引擎数据，全天数据build
			errObsTimePriceList = StockDataApi.instance().buildMinTimePriceListObserver(
					stockID, date, 
					"09:30:00", "15:00:00", m_obsTimePriceList);
		}
		else if(cmp == 0)
		{
			// 获取日期在测试日期当天，从当天缓存中加载
			pool.currentDayTimePriceCache().buildMinTimePriceListObserver(
					stockID, 
					m_obsTimePriceList);
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
