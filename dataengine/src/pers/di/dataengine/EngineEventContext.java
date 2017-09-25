package pers.di.dataengine;

public class EngineEventContext {
	
	public EngineEventContext(String date, String time)
	{
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
	
	public void setDate(String date)
	{
		m_date = date;
	}
	
	public void setTime(String time)
	{
		m_time = time;
	}
	
	private String m_date;
	private String m_time;
}
