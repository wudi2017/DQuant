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
	
	public DAPool()
	{
		m_date = "";
		m_time = "";
		m_obsStockIDList = null;
		m_currentDayTimePriceCache = new CurrentDayTimePriceCache();
	}
	
	public void build(String date, String time)
	{
		// 构建所有股票ID
		m_obsStockIDList = new CListObserver<String>();
		StockDataEngine.instance().buildAllStockIDObserver(m_obsStockIDList);
		
		// 构建当日缓存数据
		if(!m_date.equals(date))
		{
			m_currentDayTimePriceCache.clear(); // 构建天数不一致，清空实时缓存
		}
		m_currentDayTimePriceCache.buildAll(date, time);
		
		
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
	
	public boolean subscribeMinuteData(String StockID)
	{
		return m_currentDayTimePriceCache.subscribeMinuteData(StockID);
	}
	
	public CurrentDayTimePriceCache currentDayTimePriceCache()
	{
		return m_currentDayTimePriceCache;
	}
	
	// 数据池 日期 时间
	private String m_date;
	private String m_time;
	
	// 所有股票ID
	private CListObserver<String> m_obsStockIDList;
	
	// 当天分时数据缓存
	private CurrentDayTimePriceCache m_currentDayTimePriceCache;
	
}
