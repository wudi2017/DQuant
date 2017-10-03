package pers.di.quantplatform;

import java.util.*;

import pers.di.dataengine.*;

public class QuantContext {
	
	public QuantContext(AccountProxy acountProxy)
	{
		m_dataAccessPool = null; 
		m_accountProxy = acountProxy;
	}
	
	public void set(String date, String time, DAPool dataAccessPool)
	{
		m_date = date;
		m_time = time;
		m_dataAccessPool = dataAccessPool;
	}
	
	public String date()
	{
		return m_date;
	}
	
	public String time()
	{
		return m_time;
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
	private AccountProxy m_accountProxy;
	private DAPool m_dataAccessPool;
}
