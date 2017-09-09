package pers.di.quantengine.dataaccessor;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.*;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.StockDataEngine.*;
import pers.di.dataengine.common.*;


/*
 * 数据访问器
 * 细节：内部只有时间数据，当需要访问时进行调用
 */
public class DAPool {
	public DAPool(String date, String time)
	{
		m_date = date;
		m_time = time;
		m_obsStockIDList = new CListObserver<String>();
		StockDataEngine.instance().buildAllStockIDObserver(m_obsStockIDList);
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
		return new DAStock(stockID, this);
	}
	public DAStock get(String stockID)
	{
		return new DAStock(stockID, this);
	}
	
	private String m_date;
	private String m_time;
	private CListObserver<String> m_obsStockIDList;
}
