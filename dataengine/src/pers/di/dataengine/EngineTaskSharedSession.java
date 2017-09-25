package pers.di.dataengine;

public class EngineTaskSharedSession {
	
	public boolean bIsTranDate()
	{
		return bIsTranDate;
	}
	public void setIsTranDate(boolean bFlag){
		bIsTranDate = bFlag;
	}
	private boolean bIsTranDate;
	
	
	public boolean bHistoryTest()
	{
		return m_bHistoryTest;
	}
	public void setHistoryTest(boolean bFlag){
		m_bHistoryTest = bFlag;
	}
	private boolean m_bHistoryTest;
	
	
	public String beginDate()
	{
		return m_beginDate;
	}
	public void setBeginDate(String beginDate){
		m_beginDate = beginDate;
	}
	private String m_beginDate;
	
	
	public String endDate()
	{
		return m_endDate;
	}
	public void setEndDate(String endDate){
		m_endDate = endDate;
	}
	private String m_endDate;
	
	
	public boolean bConfigFailed()
	{
		return m_configFailed;
	}
	public void setConfigFailed(boolean configFailed){
		m_configFailed = configFailed;
	}
	private boolean m_configFailed;
}
