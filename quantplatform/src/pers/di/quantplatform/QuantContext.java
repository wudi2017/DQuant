package pers.di.quantplatform;

import java.util.*;

import pers.di.dataengine.*;

public class QuantContext {
	
	public QuantContext()
	{
		m_context = null; 
		m_accountProxy = null;
	}
	
	public void setAccountProxy(AccountProxy ap)
	{
		m_accountProxy = ap;
	}
	public void setDAContext(DAContext ctx)
	{
		m_context = ctx;
	}
	
	public String date()
	{
		return m_context.date();
	}
	
	public String time()
	{
		return m_context.time();
	}

	public AccountProxy ap()
	{
		return m_accountProxy;
	}
	
	public DAPool pool()
	{
		return m_context.pool();
	}
	
	
	public final boolean addCurrentDayInterestMinuteDataID(String ID)
	{
		m_context.addCurrentDayInterestMinuteDataID(ID);
		return true;
	}
	public final boolean addCurrentDayInterestMinuteDataIDs(List<String> IDs)
	{
		m_context.addCurrentDayInterestMinuteDataIDs(IDs);
		return true;
	}
	public final boolean removeCurrentDayInterestMinuteDataID(String ID)
	{
		m_context.removeCurrentDayInterestMinuteDataID(ID);
		return true;
	}
	public final List<String> getCurrentDayInterestMinuteDataIDs()
	{
		return m_context.getCurrentDayInterestMinuteDataIDs();
	}

	private AccountProxy m_accountProxy;
	private DAContext m_context;
}
