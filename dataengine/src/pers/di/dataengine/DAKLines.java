package pers.di.dataengine;

import pers.di.common.CListObserver;
import pers.di.dataengine.baseapi.StockDataApi;
import pers.di.dataengine.common.KLine;

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
	
	private CListObserver<KLine> m_obsKLineList;
}
