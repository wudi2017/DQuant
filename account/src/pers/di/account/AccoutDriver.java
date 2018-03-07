package pers.di.account;

import java.util.*;

import pers.di.account.common.*;
import pers.di.account.detail.*;
import pers.di.common.CLog;
import pers.di.common.CSyncObj;

public class AccoutDriver {
	
	public static class InnerMarketReplier extends IMarketDealReplier
	{
		public InnerMarketReplier(AccoutDriver cAccoutDriver)
		{
			m_cAccoutDriver = cAccoutDriver;
		}
		@Override
		public void onDeal(TRANACT tranact, String id, int amount, double price, double cost) {
			m_cAccoutDriver.onDeal(tranact, id, amount, price, cost);
		}	
		private AccoutDriver m_cAccoutDriver;
	}
	
	public AccoutDriver(String dataRoot)
	{
		m_cSync = new CSyncObj();
		m_dataRoot = dataRoot;
		m_accountEntity = null;
		m_innerMarketReplier = null;
	}
	
	// load account entity, 
	// if bCreate is true, will try create new account when open account failed
	public int load(String accID, IMarketOpe cIMarketOpe, boolean bCreate)
	{
		m_cSync.Lock();
		
		// init m_accountEntity m_innerMarketReplier
		if(null != cIMarketOpe)
		{
			m_innerMarketReplier = new InnerMarketReplier(this);
			if( 0 != cIMarketOpe.registerDealReplier(m_innerMarketReplier))
			{
				CLog.error("ACCOUNT", "AccoutDriver init cIMarketOpe.registerDealReplier failed\n");
			}
			
			m_accountEntity =  new AccountEntity();
			if(0 != m_accountEntity.load(m_dataRoot, accID, cIMarketOpe, bCreate))
			{
				CLog.error("ACCOUNT", "AccoutDriver init m_accountEntity.load failed\n");
			}
		}
		else
		{
			CLog.error("ACCOUNT", "AccoutDriver init IMarketOpe failed\n");
		}
		
		m_cSync.UnLock();
		return 0;
	}
	
	// reset account entity
	public int reset(double fInitMoney)
	{
		int iRet = -1;
		m_cSync.Lock();
		iRet = m_accountEntity.reset(fInitMoney);
		m_cSync.UnLock();
		return iRet;
	}
	
	// market deal callback
	public void onDeal(TRANACT tranact, String id, int amount, double price, double cost) 
	{
		m_cSync.Lock();
		if(null != m_accountEntity)
		{
			m_accountEntity.onDeal(tranact, id, amount, price, cost);
		}
		m_cSync.UnLock();
	}
	
	// get account
	public Account account()
	{
		Account acc = null;
		m_cSync.Lock();
		if(null != m_accountEntity)
		{
			acc = m_accountEntity;
		}
		m_cSync.UnLock();
		return acc;
	}
	
	// set date time
	public int setDateTime(String date, String time)
	{
		int iRet = -1;
		m_cSync.Lock();
		if(null != m_accountEntity) 
		{
			iRet = m_accountEntity.setDateTime(date, time);
		}
		m_cSync.UnLock();
		return iRet;
	}
	
	// get hold stocks
	public int getHoldStockIDList(List<String> ctnHoldList)
	{
		int iRet = -1;
		m_cSync.Lock();
		if(null != m_accountEntity && null != ctnHoldList)
		{
			List<HoldStock> ctnList = new ArrayList<HoldStock>();
			int iRetGet = m_accountEntity.getHoldStockList(ctnList);
			if(0 == iRetGet)
			{
				for(int i=0; i<ctnList.size(); i++)
				{
					HoldStock cHoldStock = ctnList.get(i);
					ctnHoldList.add(cHoldStock.stockID);
				}
				iRet = 0;
			}
		}
		m_cSync.UnLock();
		return iRet;
	}
	
	// flush current price
	public int flushCurrentPrice(String stockID, double price)
	{
		int iRet = -1;
		m_cSync.Lock();
		if(null != m_accountEntity) 
		{
			iRet = m_accountEntity.flushCurrentPrice(stockID, price);
		}
		m_cSync.UnLock();
		return iRet;
	}
	
	// new day begin
	public int newDayBegin()
	{
		int iRet = -1;
		m_cSync.Lock();
		if(null != m_accountEntity) 
		{
			iRet = m_accountEntity.newDayBegin();
		}
		m_cSync.UnLock();
		return iRet;
	}
	
	// new day end
	public int newDayEnd()
	{
		int iRet = -1;
		m_cSync.Lock();
		if(null != m_accountEntity) 
		{
			iRet = m_accountEntity.newDayEnd();
		}
		m_cSync.UnLock();
		return iRet;
	}
	
	private CSyncObj m_cSync; 
	private String m_dataRoot;
	private AccountEntity m_accountEntity;
	private InnerMarketReplier m_innerMarketReplier;
}
