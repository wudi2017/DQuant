package pers.di.dataengine.common;

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
		if(null == m_innerRefStockInfo)
			return null;
		return m_innerRefStockInfo.name;
	}
	
	public Float PE()
	{
		if(null == m_innerRefStockInfo)
			return null;
		return m_innerRefStockInfo.peRatio;
	}
	
	public Float circulatedMarketValue()
	{
		if(null == m_innerRefStockInfo)
			return null;
		return m_innerRefStockInfo.circulatedMarketValue;
	}
	
	public Float allMarketValue()
	{
		if(null == m_innerRefStockInfo)
			return null;
		return m_innerRefStockInfo.allMarketValue;
	}

	private StockInfo m_innerRefStockInfo;
}
