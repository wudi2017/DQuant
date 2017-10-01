package pers.di.quantplatform;

import pers.di.quantplatform.accountproxy.*;
import pers.di.quantplatform.dataaccessor.*;

public class QuantContext {
	
	public QuantContext(AccountProxy acountProxy)
	{
		m_dataAccessPool = new DAPool(); 
		m_accountProxy = acountProxy;
	}
	
	public String date()
	{
		return m_date;
	}
	
	public String time()
	{
		return m_time;
	}
	
	public void setDateTime(String date, String time)
	{
		m_date = date;
		m_time = time;
		m_dataAccessPool.build(m_date, m_time);
	}
	
	public AccountProxy ap()
	{
		return m_accountProxy;
	}
	
	public DAPool pool()
	{
		return m_dataAccessPool;
	}
	
	private String m_date;
	private String m_time;
	
	private DAPool m_dataAccessPool;
	private AccountProxy m_accountProxy;
}
