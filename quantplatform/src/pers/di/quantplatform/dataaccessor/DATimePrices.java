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
			// ��ȡ�����ǲ�������֮ǰ���죬�����������ݣ�ȫ������build
			errObsTimePriceList = StockDataApi.instance().buildMinTimePriceListObserver(
					stockID, date, 
					"09:30:00", "15:00:00", m_obsTimePriceList);
		}
		else if(cmp == 0)
		{
			// ��ȡ�����ڲ������ڵ��죬�ӵ��컺���м���
			pool.currentDayTimePriceCache().buildMinTimePriceListObserver(
					stockID, 
					m_obsTimePriceList);
		}
		else
		{
			// ��ȡ�����ڲ�������֮�󣬲�����build
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
