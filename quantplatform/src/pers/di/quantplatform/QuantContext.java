package pers.di.quantplatform;

import pers.di.quantplatform.accountproxy.*;
import pers.di.quantplatform.dataaccessor.*;

public class QuantContext {
	
	public String date()
	{
		return m_date;
	}
	
	public String time()
	{
		return m_time;
	}
	
	public AccountProxy accountProxy()
	{
		return null;
	}
	
	public DAPool dataPool()
	{
		return new DAPool();
	}
	
	private String m_date;
	private String m_time;
}
