package pers.di.quantengine.dataaccessor;

import pers.di.common.CNewTypeDefine.CListObserver;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.common.*;
import pers.di.common.CNewTypeDefine.*;

public class DAStock {
	
	public DAStock(String stockID, String date, String time)
	{
		m_date = date;
		m_time = time;
		m_stockID = stockID;
		obsStockInfo = new CObjectObserver<StockInfo>();
		StockDataEngine.instance().buildStockInfoObserver(stockID, obsStockInfo);
	}
	
	public String ID()
	{
		return m_stockID;
	}
	
	public String name()
	{
		return obsStockInfo.get().name;
	}
	
	public float PE()
	{
		return obsStockInfo.get().peRatio;
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
	private CObjectObserver<StockInfo> obsStockInfo;
}
