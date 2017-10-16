package pers.di.accountengine;

import java.util.List;

import pers.di.accountengine.common.*;
import pers.di.accountengine.detail.*;
import pers.di.common.CLog;

public class AccoutDriver {
	
	public AccoutDriver()
	{
		m_accountEntity = null;
	}

	public int load(ACCOUNTTYPE type, String accID, String accPassword)
	{
		// create IAccountOpe
		IAccountOpe cIAccOpe = null;
		if(type == ACCOUNTTYPE.MOCK)
		{
			MockAccountOpe cMockAccountOpe = new MockAccountOpe();
			int iRet = cMockAccountOpe.initialize(accID, accPassword);
			if(0 == iRet)
			{
				cIAccOpe = cMockAccountOpe;
			}
		}
		else
		{
			RealAccountOpe cRealAccountOpe = new RealAccountOpe();
			int iRet = cRealAccountOpe.initialize(accID, accPassword);
			if(0 == iRet)
			{
				cIAccOpe = cRealAccountOpe;
			}
		}

		// init m_accountEntity
		if(null != cIAccOpe)
		{
			m_accountEntity = new AccountEntity();
			int iRet = m_accountEntity.initialize(cIAccOpe);
			if(0 != iRet)
			{
				CLog.error("ACCOUNT", "AccoutDriver init AccountEntity failed\n");
			}
		}
		else
		{
			CLog.error("ACCOUNT", "AccoutDriver init IAccountOpe failed\n");
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
	
	// set date time
	public int setDateTime(String date, String time)
	{
		return 0;
	}
	
	// update current hold stock price
	public int getHoldStockList(List<String> ctnHoldList)
	{
		return 0;
	}
	public int flushCurrentPrice(String stockID, float price)
	{
		return 0;
	}
	
	// new day begin and end
	public int newDayBegin()
	{
		return 0;
	}
	public int newDayEnd()
	{
		return 0;
	}
	
	private AccountEntity m_accountEntity;
}
