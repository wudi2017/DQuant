package pers.di.account.detail;

import java.util.*;

import pers.di.account.Account;
import pers.di.account.IMarketAccountOpe;
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
	public int getTotalAssets(CObjectContainer<Float> ctnTotalAssets) {
		
		if(!m_initFlag) return -1;
		
		float all_marketval = 0.0f;
		
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		int iRetHoldStock = getHoldStockList(cHoldStockList);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			all_marketval = all_marketval + cHoldStock.curPrice*cHoldStock.totalAmount;
		}
		
		CObjectContainer<Float> money = new  CObjectContainer<Float>();
		int iRetMoney = m_cIMarketAccountOpe.getAvailableMoney(money);
		
		ctnTotalAssets.set(all_marketval + money.get());
		if(0 == iRetHoldStock && 0 == iRetMoney)
		{
			return 0;
		}
		
		return -99;
	}

	@Override
	public int getAvailableMoney(CObjectContainer<Float> ctnAvailableMoney) {
		
		if(!m_initFlag) return -1;
		
		return m_cIMarketAccountOpe.getAvailableMoney(ctnAvailableMoney);
	}

	@Override
	public int pushBuyOrder(String stockID, int amount, float price) {
		
		if(!m_initFlag) return -1;
		
		int ret = m_cIMarketAccountOpe.pushBuyOrder(stockID, amount, price);
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

	@Override
	public int pushSellOrder(String stockID, int amount, float price) {
		
		if(!m_initFlag) return -1;
		
		int ret = m_cIMarketAccountOpe.pushSellOrder(stockID, amount, price);
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

	@Override
	public int getCommissionOrderList(List<CommissionOrder> ctnList) {
		
		if(!m_initFlag) return -1;
		
		ctnList.clear();
		ctnList.addAll(m_accountStore.storeEntity().commissionOrderList);
		return 0;
	}

	@Override
	public int getBuyCommissionOrderList(List<CommissionOrder> ctnList) {
		
		if(!m_initFlag) return -1;
		
		ctnList.clear();
		List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
		int iRet = this.getCommissionOrderList(cCommissionOrderList);
		for(int i= 0;i<cCommissionOrderList.size();i++)
		{
			CommissionOrder cCommissionOrder = cCommissionOrderList.get(i);
			if(cCommissionOrder.tranAct == TRANACT.BUY)
			{
				CommissionOrder cNewCommissionOrder = new CommissionOrder();
				cNewCommissionOrder.CopyFrom(cCommissionOrder);
				ctnList.add(cNewCommissionOrder);
			}
		}
		return iRet;
	}

	@Override
	public int getSellCommissionOrderList(List<CommissionOrder> ctnList) {
		
		if(!m_initFlag) return -1;
		
		ctnList.clear();
		List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
		int iRet = this.getCommissionOrderList(cCommissionOrderList);
		for(int i= 0;i<cCommissionOrderList.size();i++)
		{
			CommissionOrder cCommissionOrder = cCommissionOrderList.get(i);
			if(cCommissionOrder.tranAct == TRANACT.SELL)
			{
				CommissionOrder cNewCommissionOrder = new CommissionOrder();
				cNewCommissionOrder.CopyFrom(cCommissionOrder);
				ctnList.add(cNewCommissionOrder);
			}
		}
		return iRet;
	}

	@Override
	public int getHoldStockList(List<HoldStock> ctnList) {
		
		if(!m_initFlag) return -1;
		
		int iGetHoldStockList = m_cIMarketAccountOpe.getHoldStockList(ctnList);
		if(0 == iGetHoldStockList)
		{
			for(int i=0;i<ctnList.size();i++)
	        {
	        	HoldStock cHoldStock = ctnList.get(i);
	        	if(m_accountStore.storeEntity().holdStockInvestigationDaysMap.containsKey(cHoldStock.stockID))
	        	{
	        		cHoldStock.investigationDays = m_accountStore.storeEntity().holdStockInvestigationDaysMap.get(cHoldStock.stockID);
	        	}
	        	else
	        	{
	        		cHoldStock.investigationDays = 0;
	        	}
	        }
		}
		return iGetHoldStockList;
	}

	@Override
	public int getHoldStock(String stockID, CObjectContainer<HoldStock> ctnHoldStock) {
		
		if(!m_initFlag) return -1;
		
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		int iRet = getHoldStockList(cHoldStockList);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			if(cHoldStockList.get(i).stockID.equals(stockID))
			{
				HoldStock cNewHoldStock = cHoldStockList.get(i);
				ctnHoldStock.set(cNewHoldStock);
				break;
			}
		}
		return iRet;
	}
	
	/*
	 * ******************************************************************************************
	 */
	
	public AccountEntity()
	{
		m_cIMarketAccountOpe = null;
		m_accountStore = null;
		m_initFlag = false;
	}
	
	public int initialize(IMarketAccountOpe cIMarketAccountOpe)
	{
		return 0;
//		m_cIMarketAccountOpe = cIMarketAccountOpe;
//		if(null != m_cIMarketAccountOpe.ID())
//		{
//			m_accountStore = new AccountStore(m_cIMarketAccountOpe.ID(), m_cIMarketAccountOpe.password());
//			boolean bLoad = m_accountStore.load();
//			if(bLoad)
//			{
//				CLog.output("ACCOUNT", " @AccountEntity initialize AccountID:%s Password:%s OK~\n", 
//						m_cIMarketAccountOpe.ID(), m_cIMarketAccountOpe.password());
//				m_initFlag = true;
//				return 0;
//			}
//			else
//			{
//				CLog.output("ACCOUNT", " @AccountEntity initialize failed, m_accountStore.load err!\n");
//				return -1;
//			}
//		}
//		else
//		{
//			CLog.output("ACCOUNT", " @AccountEntity initialize failed, m_cIMarketAccountOpe err!\n");
//			return -1;
//		}
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
				
				m_accountStore.flush();
			}
			else
			{
				iNewDayTranEnd = -201;
			}
			
			// 清空委托表
			m_accountStore.storeEntity().commissionOrderList.clear();
			m_accountStore.flush();
		}
		
		return iNewDayTranEnd; 
	}
	
	/*
	 * ******************************************************************************************
	 */
	private IMarketAccountOpe m_cIMarketAccountOpe;
	private AccountStore m_accountStore;
	private boolean m_initFlag;
}
