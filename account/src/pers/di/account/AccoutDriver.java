package pers.di.account;

import java.util.*;

import pers.di.account.common.*;
import pers.di.account.detail.*;
import pers.di.common.CLog;

public class AccoutDriver {
	
	public static class InnerMarketReplier extends IMarketDealReplier
	{
		public InnerMarketReplier(AccoutDriver cAccoutDriver)
		{
			m_cAccoutDriver = cAccoutDriver;
		}
		@Override
		public void onDeal(TRANACT tranact, String id, int amount, float price, float cost) {
			m_cAccoutDriver.onDeal(tranact, id, amount, price, cost);
		}	
		private AccoutDriver m_cAccoutDriver;
	}
	
	public AccoutDriver()
	{
		m_accountEntity = null;
		m_innerMarketReplier = null;
	}
	
	// load account entity, 
	// if bCreate is true, will try create new account when open account failed
	public int load(String accID, IMarketOpe cIMarketOpe, boolean bCreate)
	{
		// init m_accountEntity m_innerMarketReplier
		if(null != cIMarketOpe)
		{
			m_innerMarketReplier = new InnerMarketReplier(this);
			if( 0 != cIMarketOpe.registerDealReplier(m_innerMarketReplier))
			{
				CLog.error("ACCOUNT", "AccoutDriver init cIMarketOpe.registerDealReplier failed\n");
			}
			
			m_accountEntity =  new AccountEntity();
			if(0 != m_accountEntity.load(accID, cIMarketOpe, bCreate))
			{
				CLog.error("ACCOUNT", "AccoutDriver init m_accountEntity.load failed\n");
			}
		}
		else
		{
			CLog.error("ACCOUNT", "AccoutDriver init IMarketOpe failed\n");
		}
		return 0;
	}
	
	// reset account entity
	public int reset(float fInitMoney)
	{
		return m_accountEntity.reset(fInitMoney);
	}
	
	// market deal callback
	public void onDeal(TRANACT tranact, String id, int amount, float price, float cost) 
	{
		if(null != m_accountEntity)
		{
			m_accountEntity.onDeal(tranact, id, amount, price, cost);
		}
	}
	
	// get account
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
	
	// get hold stocks
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
	
	// flush current price
	public int flushCurrentPrice(String stockID, float price)
	{
		if(null == m_accountEntity) return -1;
		return m_accountEntity.flushCurrentPrice(stockID, price);
	}
	
	// new day begin
	public int newDayBegin()
	{
		if(null == m_accountEntity) return -1;
		return m_accountEntity.newDayBegin();
	}
	
	// new day end
	public int newDayEnd()
	{
		if(null == m_accountEntity) return -1;
		return m_accountEntity.newDayEnd();
	}
	
	private AccountEntity m_accountEntity;
	private InnerMarketReplier m_innerMarketReplier;
}
