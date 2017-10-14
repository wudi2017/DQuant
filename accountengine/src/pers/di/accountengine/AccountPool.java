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
	
	/*
	 * ******************************************************************
	 */
	
	public int flush(String date, String time)
	{
		return 0;
	}
	
	public int loadAccount(String accID, String accPassword)
	{
		return 0;
	}

	public Account account(String accID)
	{
		return null;
	}
	
	private CDateTimeThruster m_dateTimeThruster;
	private IDataSource m_dataSource;
}
