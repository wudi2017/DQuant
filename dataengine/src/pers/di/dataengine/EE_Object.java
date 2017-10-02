package pers.di.dataengine;

public class EE_Object {
	
	public EE_Object(EE_ID eID)
	{
		m_eID = eID;
		m_date = "0000-00-00";
		m_time = "00:00:00";
	}
	
	public EE_ID eID()
	{
		return m_eID;
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
	
	private EE_ID m_eID;
	private String m_date;
	private String m_time;
}
