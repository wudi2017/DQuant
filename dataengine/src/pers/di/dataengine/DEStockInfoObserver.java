package pers.di.dataengine;

import pers.di.dataengine.common.StockInfo;

public class DEStockInfoObserver {
	public DEStockInfoObserver()
	{
		m_innerRefStockInfo = null;
	}
	public void build(StockInfo origin) 
	{ 
		m_innerRefStockInfo = origin; 
	}
	
	public String name()
	{
		return m_innerRefStockInfo.name;
	}
	
	public float PE()
	{
		return m_innerRefStockInfo.peRatio;
	}
	
	public float circulatedMarketValue()
	{
		return m_innerRefStockInfo.circulatedMarketValue;
	}
	
	public float allMarketValue()
	{
		return m_innerRefStockInfo.allMarketValue;
	}

	private StockInfo m_innerRefStockInfo;
}
