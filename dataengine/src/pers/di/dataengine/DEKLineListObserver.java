package pers.di.dataengine;

import java.util.List;

import pers.di.dataengine.common.KLine;

public class DEKLineListObserver {
	public DEKLineListObserver()
	{
		m_resultList = null;
		m_iBase = 0;
		m_iSize= 0;
	}
	public void build(List<KLine> origin, int iBase, int iSize)
	{
		m_resultList = origin;
		m_iBase = iBase;
		m_iSize= iSize;
	}

	public int size()
	{
		return m_iSize;
	}
	public KLine get(int i)
	{
		return m_resultList.get(m_iBase+i);
	}

	private List<KLine> m_resultList;
	private int m_iBase;
	private int m_iSize;
}
