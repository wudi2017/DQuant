package pers.di.dataengine;

import pers.di.common.CListObserver;
import pers.di.common.CUtilsDateTime;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.StockDataApi;

public class DAKLines {
	public DAKLines(DAPool pool, String stockID)
	{
		m_obsKLineList = new CListObserver<KLine>();
		
		if(pool.time().compareTo("15:10:00") > 0)
		{
			StockDataApi.instance().buildDayKLineListObserver(
					stockID, "2000-01-01", pool.date(), m_obsKLineList);
		}
		else
		{
			String beforeDate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(pool.date(), -1);
			StockDataApi.instance().buildDayKLineListObserver(
					stockID, "2000-01-01", beforeDate, m_obsKLineList);
		}
	}
	public int size()
	{
		return m_obsKLineList.size();
	}
	public KLine get(int i)
	{
		return m_obsKLineList.get(i);
	}
	public String lastDate()
	{
		return m_obsKLineList.get(m_obsKLineList.size()-1).date;
	}
	public double lastPrice()
	{
		return m_obsKLineList.get(m_obsKLineList.size()-1).close;
	}
	
	private CListObserver<KLine> m_obsKLineList;
}
