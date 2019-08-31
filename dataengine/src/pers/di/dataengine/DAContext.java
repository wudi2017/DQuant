package pers.di.dataengine;

import java.util.List;

// data access context
public class DAContext {
	
	public DAContext()
	{
		m_dataAccessPool = new DAPool(); 
	}
	
	public String date()
	{
		return m_date;
	}
	
	public String time()
	{
		return m_time;
	}
	
	public void addCurrentDayInterestMinuteDataID(String dataID)
	{
		m_dataAccessPool.addCurrentDayInterestMinuteDataID(dataID);
	}
	
	public void removeCurrentDayInterestMinuteDataID(String dataID)
	{
		m_dataAccessPool.removeCurrentDayInterestMinuteDataID(dataID);
	}
	
	public List<String> getCurrentDayInterestMinuteDataIDs()
	{
		return m_dataAccessPool.getCurrentDayInterestMinuteDataIDs();
	}
	
	public void clearCurrentDayInterestMinuteDataCache()
	{
		m_dataAccessPool.clearCurrentDayInterestMinuteDataCache();
	}
	
	
	
	public void setDateTime(String date, String time)
	{
		m_date = date;
		m_time = time;
		m_dataAccessPool.build(m_date, m_time);
	}
	
	public DAPool pool()
	{
		return m_dataAccessPool;
	}
	
	private String m_date;
	private String m_time;
	
	private DAPool m_dataAccessPool;
}
