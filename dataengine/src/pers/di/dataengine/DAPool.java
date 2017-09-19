package pers.di.dataengine;

import pers.di.common.CListObserver;
import pers.di.dataengine.baseapi.StockDataApi;

public class DAPool {
	
	public DAPool()
	{
		m_date = "";
		m_time = "";
		m_obsStockIDList = null;
	}
	
	public void build(String date, String time)
	{
		// 构建所有股票ID
		m_obsStockIDList = new CListObserver<String>();
		StockDataApi.instance().buildAllStockIDObserver(m_obsStockIDList);

		// 更新pool日期
		m_date = date;
		m_time = time;
	}
	public String date()
	{
		return m_date;
	}
	public String time()
	{
		return m_time;
	}
	public int size()
	{
		return m_obsStockIDList.size();
	}
	public DAStock get(int i)
	{
		String stockID = m_obsStockIDList.get(i);
		return new DAStock(this, stockID);
	}
	public DAStock get(String stockID)
	{
		return new DAStock(this, stockID);
	}
	
	// 数据池 日期 时间
	private String m_date;
	private String m_time;
	
	// 所有股票ID
	private CListObserver<String> m_obsStockIDList;
}
