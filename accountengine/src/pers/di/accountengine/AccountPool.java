package pers.di.accountengine;

public class AccountPool {
	
	private static AccountPool s_instance = new AccountPool(); 
	public static AccountPool instance() {  
		return s_instance;  
	} 
	private AccountPool() 
	{
	}

	public Account account(String accID, String accPassword)
	{
		return null;
	}
}
