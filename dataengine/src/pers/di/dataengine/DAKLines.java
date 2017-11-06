package pers.di.dataengine;

import pers.di.common.CListObserver;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.StockDataApi;

public class DAKLines {
	public DAKLines(DAPool pool, String stockID)
	{
		m_obsKLineList = new CListObserver<KLine>();
		StockDataApi.instance().buildDayKLineListObserver(
				stockID, "2000-01-01", pool.date(), m_obsKLineList);
		
	}
	public int size()
	{
		return m_obsKLineList.size();
	}
	public KLine get(int i)
	{
		return m_obsKLineList.get(i);
	}
	public String latestDate()
	{
		if(m_obsKLineList.size() > 0)
		{
			return m_obsKLineList.get(m_obsKLineList.size()-1).date;
		}
		else
		{
			return "0000-00-00";
		}
	}
	
	private CListObserver<KLine> m_obsKLineList;
}
