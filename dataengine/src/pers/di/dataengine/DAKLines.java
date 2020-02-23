package pers.di.dataengine;

import pers.di.common.CListObserver;
import pers.di.common.CUtilsDateTime;
import pers.di.localstock.LocalStock;
import pers.di.localstock.common.KLine;

public class DAKLines {
	public DAKLines(DAPool pool, String stockID)
	{
		m_obsKLineList = new CListObserver<KLine>();
		
		if(pool.time().compareTo("15:10:00") > 0)
		{
			LocalStock.instance().buildDayKLineListObserver(
					stockID, "2000-01-01", pool.date(), m_obsKLineList);
		}
		else
		{
			String beforeDate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(pool.date(), -1);
			LocalStock.instance().buildDayKLineListObserver(
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
