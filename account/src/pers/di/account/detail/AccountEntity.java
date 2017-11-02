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
		
		ctnMoney.set(m_accountStore.storeEntity().money);
		
		return 0;
	}
	
	@Override
	public int postTradeOrder(TRANACT tranact, String stockID, int amount, float price)
	{
		if(!m_initFlag) return -1;
		
		if(tranact == TRANACT.BUY)
		{
			CommissionOrder cCommissionOrder = new CommissionOrder();
			cCommissionOrder.time = m_accountStore.storeEntity().time;
			cCommissionOrder.tranAct = TRANACT.BUY;
			cCommissionOrder.stockID = stockID;
			cCommissionOrder.amount = amount;
			cCommissionOrder.price = price;
			m_accountStore.storeEntity().commissionOrderList.add(cCommissionOrder);
			m_accountStore.sync2File();
			
			int ret = m_cIMarketOpe.postTradeRequest(TRANACT.BUY, stockID, amount, price);
			return ret;
		}
		
		if(tranact == TRANACT.SELL)
		{
			CommissionOrder cCommissionOrder = new CommissionOrder();
			cCommissionOrder.time = m_accountStore.storeEntity().time;
			cCommissionOrder.tranAct = TRANACT.SELL;
			cCommissionOrder.stockID = stockID;
			cCommissionOrder.amount = amount;
			cCommissionOrder.price = price;
			m_accountStore.storeEntity().commissionOrderList.add(cCommissionOrder);
			m_accountStore.sync2File();
			
			int ret = m_cIMarketOpe.postTradeRequest(TRANACT.SELL, stockID, amount, price);
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
		
		ctnList.clear();
		ctnList.addAll(m_accountStore.storeEntity().holdStockList);
		
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
		if(!m_accountStore.sync2Mem())
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
		
		if(!m_accountStore.storeEntity().date.equals(date))
		{
			newDayEnd();
		}
		
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
		
		// 清空委托表
		m_accountStore.storeEntity().commissionOrderList.clear();

		// 股票全部可卖
		for(int i=0; i< m_accountStore.storeEntity().holdStockList.size(); i++)
		{
			m_accountStore.storeEntity().holdStockList.get(i).availableAmount = m_accountStore.storeEntity().holdStockList.get(i).totalAmount;
		}
		
		m_accountStore.sync2File();
		
		return 0; 
	}
	
	public void onDeal(TRANACT tranact, String stockID, int amount, float price, float cost) 
	{
		if(tranact == TRANACT.BUY)
		{
			// 获取持有对象
			HoldStock cHoldStock = null;
			for(int i = 0; i< m_accountStore.storeEntity().holdStockList.size(); i++)
			{
				HoldStock cTmpHoldStock = m_accountStore.storeEntity().holdStockList.get(i);
				if(cTmpHoldStock.stockID.equals(stockID))
				{
					cHoldStock = cTmpHoldStock;
					break;
				}
			}
			if(null == cHoldStock)
			{
				HoldStock cNewHoldStock = new HoldStock();
				cNewHoldStock.createDate = m_accountStore.storeEntity().date;
				cNewHoldStock.stockID = stockID;
				cNewHoldStock.totalAmount = 0;
				cNewHoldStock.availableAmount = 0;
				cNewHoldStock.avePrimeCostPrice = 0;
				cNewHoldStock.curPrice = 0;
				cNewHoldStock.cost = 0;
				cHoldStock = cNewHoldStock;
				m_accountStore.storeEntity().holdStockList.add(cNewHoldStock);
			}
			
			// 重置对象 (交易费用直接体现在参考成本价里)
			int oriTotalAmount = cHoldStock.totalAmount;
			float oriHoldAvePrice = cHoldStock.avePrimeCostPrice;
			cHoldStock.totalAmount = cHoldStock.totalAmount + amount;
			cHoldStock.avePrimeCostPrice = (oriHoldAvePrice*oriTotalAmount + price*amount)/cHoldStock.totalAmount;
			cHoldStock.curPrice = price;
			cHoldStock.cost = cHoldStock.cost + cost;
			
			// 更新 money
			m_accountStore.storeEntity().money = m_accountStore.storeEntity().money - price*amount;
			
		}
		else if(tranact == TRANACT.SELL)
		{
			HoldStock cHoldStock = null;
			for(int i = 0; i< m_accountStore.storeEntity().holdStockList.size(); i++)
			{
				HoldStock cTmpHoldStock = m_accountStore.storeEntity().holdStockList.get(i);
				if(cTmpHoldStock.stockID.equals(stockID))
				{
					cHoldStock = cTmpHoldStock;
					break;
				}
			}
			
			if(null != cHoldStock)
			{
				// 重置对象 (交易费用在卖出价钱中扣除)
				int oriTotalAmount = cHoldStock.totalAmount;
				float oriHoldAvePrice = cHoldStock.avePrimeCostPrice;
				cHoldStock.totalAmount = cHoldStock.totalAmount - amount;
				cHoldStock.availableAmount = cHoldStock.availableAmount - amount;
				cHoldStock.avePrimeCostPrice = (oriHoldAvePrice*oriTotalAmount - price*amount)/cHoldStock.totalAmount;
				cHoldStock.curPrice = price;
				cHoldStock.cost = cHoldStock.cost + cost;
				
				// 清仓计算
				if(cHoldStock.totalAmount != 0)
				{
					// 更新 money
					m_accountStore.storeEntity().money = m_accountStore.storeEntity().money + price*amount;
					
				}
				else
				{
					// 更新 money
					m_accountStore.storeEntity().money = m_accountStore.storeEntity().money + price*amount - cHoldStock.cost;
					m_accountStore.storeEntity().holdStockList.remove(cHoldStock);
				}
			}
			else
			{
				CLog.error("ACCOUNT", "@AccountEntity.onDeal SELL err!\n");
			}
		}
		
		m_accountStore.sync2File();
	}
	
	public int reset(float fInitMoney)
	{
		m_accountStore.storeEntity().reset(fInitMoney);
		m_accountStore.sync2File();
		return 0;
	}
	
	/*
	 * ******************************************************************************************
	 */
	private IMarketOpe m_cIMarketOpe;
	private AccountStore m_accountStore;
	private boolean m_initFlag;
}
