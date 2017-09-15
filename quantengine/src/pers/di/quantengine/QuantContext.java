package pers.di.quantengine;

import pers.di.quantengine.dataaccessor.*;

public class QuantContext {
	
	public QuantContext()
	{
		m_pool = new DAPool(); 
	}
	
	public String date()
	{
		return m_date;
	}
	
	public String time()
	{
		return m_time;
	}
	
	public DAPool pool()
	{
		return m_pool;
	}
	
	public boolean subscribeMinuteData(String StockID)
	{
		return m_pool.subscribeMinuteData(StockID);
	}
	/*
	 * --------------------------------------------------------------
	 */
	
	public void setDateTime(String date, String time)
	{
		m_date = date;
		m_time = time;
		m_pool.build(m_date, m_time);
	}
	
	private String m_date;
	private String m_time;
	private DAPool m_pool;
}
