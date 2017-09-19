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
		// �������й�ƱID
		m_obsStockIDList = new CListObserver<String>();
		StockDataApi.instance().buildAllStockIDObserver(m_obsStockIDList);

		// ����pool����
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
	
	// ���ݳ� ���� ʱ��
	private String m_date;
	private String m_time;
	
	// ���й�ƱID
	private CListObserver<String> m_obsStockIDList;
}
