package pers.di.quantengine.dataaccessor;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.*;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.StockDataEngine.*;
import pers.di.dataengine.common.*;


/*
 * ���ݷ�����
 * ϸ�ڣ��ڲ�ֻ��ʱ�����ݣ�����Ҫ����ʱ���е���
 */
public class DAPool {
	public DAPool(String date, String time, RealtimeCache rtc)
	{
		m_date = date;
		m_time = time;
		m_realtimeCache = rtc;
		
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
	public RealtimeCache realtimeCache()
	{
		return m_realtimeCache;
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
	
	private String m_date;
	private String m_time;
	private RealtimeCache m_realtimeCache;
	
	private CListObserver<String> m_obsStockIDList;
}
