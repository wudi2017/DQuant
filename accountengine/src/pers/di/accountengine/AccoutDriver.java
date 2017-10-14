package pers.di.accountengine;

import pers.di.accountengine.common.*;
import pers.di.accountengine.detail.*;

public class AccoutDriver {
	
	public AccoutDriver()
	{
		m_accountEntity = null;
	}

	public int load(ACCOUNTTYPE type, String accID, String accPassword)
	{
		if(type == ACCOUNTTYPE.MOCK)
		{
			MockAccountOpe cMockAccountOpe = new MockAccountOpe(accID, accPassword);
		}
		return 0;
	}
	
	public Account account()
	{
		Account acc = null;
		if(null != m_accountEntity)
		{
			acc = m_accountEntity;
		}
		return acc;
	}
	
	private AccountEntity m_accountEntity;
}
