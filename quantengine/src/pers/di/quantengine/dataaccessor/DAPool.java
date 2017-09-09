package pers.di.quantengine.dataaccessor;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CLog;
import pers.di.common.CNewTypeDefine.CListObserver;
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
	
	public int size()
	{
		return m_obsStockIDList.size();
	}
	
	public DAStock get(int i)
	{
		String stockID = m_obsStockIDList.get(i);
		return new DAStock(stockID, m_date, m_time);
	}
	
	public DAStock get(String stockID)
	{
		return new DAStock(stockID, m_date, m_time);
	}
	
	private String m_date;
	private String m_time;
	private CListObserver<String> m_obsStockIDList;
}
