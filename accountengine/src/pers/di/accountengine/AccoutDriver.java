package pers.di.accountengine;

import java.util.*;

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
			AccountEntity cAccountEntity =  new AccountEntity();
			int iRet = cAccountEntity.initialize(cIAccOpe);
			if(0 == iRet)
			{
				m_accountEntity = cAccountEntity;
			}
			else
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
		if(null == m_accountEntity) return -1;
		return m_accountEntity.setDateTime(date, time);
	}
	
	// update current hold stock price
	public int getHoldStockList(List<String> ctnHoldList)
	{
		if(null == m_accountEntity) return -1;
		if(null == ctnHoldList) return -1;
		List<HoldStock> ctnList = new ArrayList<HoldStock>();
		int iRet = m_accountEntity.getHoldStockList(ctnList);
		if(0 == iRet)
		{
			for(int i=0; i<ctnList.size(); i++)
			{
				HoldStock cHoldStock = ctnList.get(i);
				ctnHoldList.add(cHoldStock.stockID);
			}
			return 0;
		}
		else
		{
			return -1;
		}
	}
	public int flushCurrentPrice(String stockID, float price)
	{
		if(null == m_accountEntity) return -1;
		return m_accountEntity.flushCurrentPrice(stockID, price);
	}
	
	// new day begin and end
	public int newDayBegin()
	{
		if(null == m_accountEntity) return -1;
		return m_accountEntity.newDayBegin();
	}
	public int newDayEnd()
	{
		if(null == m_accountEntity) return -1;
		return m_accountEntity.newDayEnd();
	}
	
	private AccountEntity m_accountEntity;
}
