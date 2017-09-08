package pers.di.dataengine.common;

import java.util.List;

public class DETimePriceListObserver {
	public DETimePriceListObserver()
	{
		m_resultList = null;
		m_iBase = 0;
		m_iSize = 0;
	}
	public void build(List<TimePrice> origin, int iBase, int iSize)
	{
		m_resultList = origin;
		m_iBase = iBase;
		m_iSize = iSize;
	}

	public int size()
	{
		return m_iSize;
	}
	public TimePrice get(int i)
	{
		return m_resultList.get(m_iBase+i);
	}

	private List<TimePrice> m_resultList;
	private int m_iBase;
	private int m_iSize;
}
