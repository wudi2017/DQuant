package pers.di.quantengine.dataaccessor;

import pers.di.common.CNewTypeDefine.CListObserver;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.common.*;

public class DATimePrices {
	
	public DATimePrices(String stockID, DAPool pool)
	{
		m_obsTimePriceList = new CListObserver<TimePrice>();
		int errObsTimePriceList = StockDataEngine.instance().buildMinTimePriceListObserver(
				stockID, pool.date(), 
				"09:00:00", pool.time(), m_obsTimePriceList);
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
