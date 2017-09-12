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
			// ��ȡ�����ǲ�������֮ǰ���죬�����������ݣ�ȫ������build
			errObsTimePriceList = StockDataEngine.instance().buildMinTimePriceListObserver(
					stockID, date, 
					"09:30:00", "15:00:00", m_obsTimePriceList);
		}
		else if(cmp == 0)
		{
			// ��ȡ�����ڲ������ڵ��죬ֻbuild����ʱ��֮ǰ�Ĳ��� 
			errObsTimePriceList = StockDataEngine.instance().buildMinTimePriceListObserver(
					stockID, date, 
					"09:30:00", pool.time(), m_obsTimePriceList);
			
			// ������û�����ݣ�������Ҫ��ȡ������ʵ�������ݣ���ʵʱ�����й���
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
