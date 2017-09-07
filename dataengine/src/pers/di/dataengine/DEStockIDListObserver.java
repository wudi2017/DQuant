package pers.di.dataengine;

import java.util.List;

public class DEStockIDListObserver {
	public DEStockIDListObserver()
	{
		m_innerRefStockIDList = null;
	}
	public void build(List<String> origin)
	{
		m_innerRefStockIDList = origin;
	}
	
	public int size() 
	{ 
		return m_innerRefStockIDList.size();
	}
	public String get(int i)
	{
		return m_innerRefStockIDList.get(i);
	}
	private List<String> m_innerRefStockIDList;
}
