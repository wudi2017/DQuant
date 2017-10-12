package pers.di.accountengine;

import pers.di.common.*;

public class AccountPool {
	
	private static AccountPool s_instance = new AccountPool(); 
	public static AccountPool instance() {  
		return s_instance;  
	} 
	private AccountPool() 
	{
		m_dateTimeThruster = null;
		m_dataSource = null;
	}
	
	public boolean initialize(CDateTimeThruster dateTimeThruster, IDataSource dataSource)
	{
		m_dateTimeThruster = dateTimeThruster;
		m_dataSource = dataSource;
		return true;
	}
	
	public Account account(String accID, String accPassword)
	{
		return null;
	}
	
	private CDateTimeThruster m_dateTimeThruster;
	private IDataSource m_dataSource;
}
