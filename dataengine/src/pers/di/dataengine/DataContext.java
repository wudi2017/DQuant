package pers.di.dataengine;

public class DataContext {
	
	public DataContext()
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
