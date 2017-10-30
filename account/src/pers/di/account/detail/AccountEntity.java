package pers.di.account.detail;

import java.util.*;

import pers.di.account.Account;
import pers.di.account.IMarketOpe;
import pers.di.account.common.*;
import pers.di.account.detail.AccountStore.StoreEntity;
import pers.di.common.CLog;
import pers.di.common.CObjectContainer;
import pers.di.common.CUtilsDateTime;

public class AccountEntity extends Account {
	
	@Override
	public String ID() {
		
		if(!m_initFlag) return null;
		
		return "local";
	}

	@Override
	public int getMoney(CObjectContainer<Float> ctnMoney) {
		
		if(!m_initFlag) return -1;
		
		return 0;
	}
	
	@Override
	public int postTradeOrder(TRANACT tranact, String stockID, int amount, float price)
	{
		if(!m_initFlag) return -1;
		
		if(tranact == TRANACT.BUY)
		{
			int ret = m_cIMarketOpe.postTradeRequest(TRANACT.BUY, stockID, amount, price);
			if(0 == ret)
			{
				CommissionOrder cCommissionOrder = new CommissionOrder();
				cCommissionOrder.time = m_accountStore.storeEntity().time;
				cCommissionOrder.tranAct = TRANACT.BUY;
				cCommissionOrder.stockID = stockID;
				cCommissionOrder.amount = amount;
				cCommissionOrder.price = price;
				m_accountStore.storeEntity().commissionOrderList.add(cCommissionOrder);
			}
			return ret;
		}
		
		if(tranact == TRANACT.SELL)
		{
			int ret = m_cIMarketOpe.postTradeRequest(TRANACT.SELL, stockID, amount, price);
			if(0 == ret)
			{
				CommissionOrder cCommissionOrder = new CommissionOrder();
				cCommissionOrder.time = m_accountStore.storeEntity().time;
				cCommissionOrder.tranAct = TRANACT.SELL;
				cCommissionOrder.stockID = stockID;
				cCommissionOrder.amount = amount;
				cCommissionOrder.price = price;
				m_accountStore.storeEntity().commissionOrderList.add(cCommissionOrder);
			}
			return ret;
		}
		
		return -1;
	}

	@Override
	public int getCommissionOrderList(List<CommissionOrder> ctnList) {
		
		if(!m_initFlag) return -1;
		
		ctnList.clear();
		ctnList.addAll(m_accountStore.storeEntity().commissionOrderList);
		return 0;
	}

	@Override
	public int getHoldStockList(List<HoldStock> ctnList) {
		
		if(!m_initFlag) return -1;
		
		return 0;
	}

	/*
	 * ******************************************************************************************
	 */
	
	public AccountEntity()
	{
		m_cIMarketOpe = null;
		m_accountStore = null;
		m_initFlag = false;
	}
	
	public int load(String accID, IMarketOpe cIMarketOpe, boolean bCreate)
	{
		
		m_cIMarketOpe = cIMarketOpe;
		
		m_accountStore = new AccountStore(accID);
		if(!m_accountStore.sync2File())
		{
			if(bCreate)
			{
				boolean iStoreInit = m_accountStore.storeInit();
				if(iStoreInit)
				{
					return 0;
				}
				else
				{
					CLog.output("ACCOUNT", "@AccountEntity initialize AccountID:%s err!\n", accID);
					return -1;
				}
			}
			else
			{
				CLog.output("ACCOUNT", "@AccountEntity initialize AccountID:%s err!\n", accID);
				return -1;
			}
		}
		
		m_initFlag = true;
		CLog.output("ACCOUNT", "@AccountEntity initialize AccountID:%s OK~\n", accID);
		return 0;
	}
	
	public int setDateTime(String date, String time)
	{
		if(!m_initFlag) return -1;
		
		m_accountStore.storeEntity().date = date;
		m_accountStore.storeEntity().time = time;
		
		return 0;
	}
	
	public int flushCurrentPrice(String stockID, float price)
	{
		if(!m_initFlag) return -1;
		
		return 0;
	}
	
	public int newDayBegin() {
		
		return 0;
	}

	public int newDayEnd() {
		
		if(!m_initFlag) return -1;
		
		int iNewDayTranEnd = 0;
		if(0 == iNewDayTranEnd)
		{
			// 更新调查天数map
			Map<String, Integer> newholdStockInvestigationDaysMap = new HashMap<String, Integer>();
			
			List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
			int iGetHoldStockList = getHoldStockList(cHoldStockList);
			if(0 == iGetHoldStockList)
			{
				for(int i=0; i<cHoldStockList.size();i++)
				{
					HoldStock cHoldStock = cHoldStockList.get(i);
					newholdStockInvestigationDaysMap.put(cHoldStock.stockID, 0);
				}
				for(Map.Entry<String, Integer> entry:newholdStockInvestigationDaysMap.entrySet()){   
					String key = entry.getKey();
					int iInvestigationDays = 0;
					if(m_accountStore.storeEntity().holdStockInvestigationDaysMap.containsKey(key))
					{
						iInvestigationDays = m_accountStore.storeEntity().holdStockInvestigationDaysMap.get(key);
					}
					entry.setValue(iInvestigationDays);
				} 
				for(Map.Entry<String, Integer> entry:newholdStockInvestigationDaysMap.entrySet()){   
					int iInvestigationDays = entry.getValue();
					entry.setValue(iInvestigationDays+1);
				} 
				m_accountStore.storeEntity().holdStockInvestigationDaysMap.clear();
				m_accountStore.storeEntity().holdStockInvestigationDaysMap.putAll(newholdStockInvestigationDaysMap);
				
				m_accountStore.sync2File();
			}
			else
			{
				iNewDayTranEnd = -201;
			}
			
			// 清空委托表
			m_accountStore.storeEntity().commissionOrderList.clear();
			m_accountStore.sync2File();
		}
		
		return iNewDayTranEnd; 
	}
	
	public void onDeal(TRANACT tranact, String id, int amount, float price, float cost) 
	{
		
	}
	
	public int reset(float fInitMoney)
	{
		return 0;
	}
	
	/*
	 * ******************************************************************************************
	 */
	private IMarketOpe m_cIMarketOpe;
	private AccountStore m_accountStore;
	private boolean m_initFlag;
}
