package pers.di.quantengine.dataaccessor;

import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.common.DEStockInfoObserver;

public class DAStock {
	
	public DAStock(String stockID, String date, String time)
	{
		m_date = date;
		m_time = time;
		m_stockID = stockID;
		obsStockInfo = new DEStockInfoObserver();
		StockDataEngine.instance().buildStockInfoObserver(stockID, obsStockInfo);
	}
	
	public String ID()
	{
		return m_stockID;
	}
	
	public String name()
	{
		return obsStockInfo.name();
	}
	
	public float PE()
	{
		return obsStockInfo.PE();
	}
	
	/*
	 * 获取日K线
	 */
	public DAKLines dayKLines()
	{
		return new DAKLines(m_stockID, m_date);
	}
	
	/*
	 * 获取某日分时线
	 */
	public DATimePrices timePrices(String date)
	{
		return new DATimePrices(m_stockID, m_date);
	}
	
	private String m_date;
	private String m_time;
	private String m_stockID;
	private DEStockInfoObserver obsStockInfo;
}
