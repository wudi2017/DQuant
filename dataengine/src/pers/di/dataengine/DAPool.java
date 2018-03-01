package pers.di.dataengine;

import java.util.List;

import pers.di.common.CListObserver;
import pers.di.dataapi.StockDataApi;

public class DAPool {

	public DAPool()
	{
		m_date = "";
		m_time = "";
		m_obsStockIDList = null;
		m_currentDayTimePriceCache = new DACurrentDayTimePriceCache();
	}
	
	public void addCurrentDayInterestMinuteDataID(String dataID)
	{
		m_currentDayTimePriceCache.addCurrentDayInterestMinuteDataID(dataID);
	}
	
	public void removeCurrentDayInterestMinuteDataID(String dataID)
	{
		m_currentDayTimePriceCache.removeCurrentDayInterestMinuteDataID(dataID);
	}
	
	public void clearCurrentDayInterestMinuteDataCache()
	{
		m_currentDayTimePriceCache.clear();
	}
	
	public DACurrentDayTimePriceCache currentDayTimePriceCache()
	{
		return m_currentDayTimePriceCache;
	}
	
	public void build(String date, String time)
	{
		// �������ջ�������
		if(!m_date.equals(date))
		{
			m_currentDayTimePriceCache.clear(); // ����������һ�£����ʵʱ����
		}
		
		// ����pool����
		m_date = date;
		m_time = time;

		// �������й�ƱID
		m_obsStockIDList = new CListObserver<String>();
		StockDataApi.instance().buildAllStockIDObserver(m_obsStockIDList);	
		
		m_currentDayTimePriceCache.buildAll(date, time);
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
	
	// ���ݳ� ���� ʱ��
	private String m_date;
	private String m_time;
	
	// ���й�ƱID
	private CListObserver<String> m_obsStockIDList;
	
	// �����ʱ���ݻ���
	private DACurrentDayTimePriceCache m_currentDayTimePriceCache;
}
