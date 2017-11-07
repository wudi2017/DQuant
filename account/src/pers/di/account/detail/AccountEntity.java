package pers.di.account.detail;

import java.util.*;

import pers.di.account.Account;
import pers.di.account.IMarketOpe;
import pers.di.account.common.*;
import pers.di.account.detail.AccountStore.StoreEntity;
import pers.di.common.CLog;
import pers.di.common.CObjectContainer;
import pers.di.common.CUtilsDateTime;
import pers.di.common.CUtilsMath;

public class AccountEntity extends Account {
	
	@Override
	public String ID() {
		
		if(!m_initFlag) return null;
		
		return m_accountStore.accountID();
	}
	
	@Override
	public String date()
	{
		if(!m_initFlag) return null;
		
		return m_accountStore.storeEntity().date;
	}
	
	@Override
	public String time()
	{
		if(!m_initFlag) return null;
		
		return m_accountStore.storeEntity().time;
	}

	@Override
	public int getMoney(CObjectContainer<Double> ctnMoney) {
		
		if(!m_initFlag) return -1;
		
		ctnMoney.set(m_accountStore.storeEntity().money);
		
		return 0;
	}
	
	@Override
	public int postTradeOrder(TRANACT tranact, String stockID, int amount, float price)
	{
		// reset price 4-5ignore
		price = CUtilsMath.saveNDecimal(price, 3);
		
		if(!m_initFlag) return -1;

		if(tranact == TRANACT.BUY)
		{
			CommissionOrder cCommissionOrder = new CommissionOrder();
			cCommissionOrder.date = m_accountStore.storeEntity().date;
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
			cCommissionOrder.date = m_accountStore.storeEntity().date;
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
	public int getDealOrderList(List<DealOrder> ctnList)
	{
		if(!m_initFlag) return -1;
		
		ctnList.clear();
		ctnList.addAll(m_accountStore.storeEntity().dealOrderList);
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
	
	public int load(String dataRoot, String accID, IMarketOpe cIMarketOpe, boolean bCreate)
	{
		
		m_cIMarketOpe = cIMarketOpe;
		
		m_accountStore = new AccountStore(dataRoot, accID);
		if(!m_accountStore.sync2Mem())
		{
			if(bCreate)
			{
				boolean iStoreInit = m_accountStore.storeInit();
				if(iStoreInit)
				{
					m_initFlag = true;
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
		// reset price 4-5ignore
		price = CUtilsMath.saveNDecimal(price, 3);
				
		if(!m_initFlag) return -1;
		
		for(int i=0; i< m_accountStore.storeEntity().holdStockList.size(); i++)
		{
			HoldStock cHoldStock = m_accountStore.storeEntity().holdStockList.get(i);
			if(stockID.equals(cHoldStock.stockID))
			{
				//cHoldStock.curPrice = CUtilsMath.saveNDecimal(price, 3);
				cHoldStock.curPrice = price;
				
			}
		}
		
		return 0;
	}
	
	public int newDayBegin() {
		
		return 0;
	}

	public int newDayEnd() {
		
		if(!m_initFlag) return -1;
		
		// 清空委托表
		m_accountStore.storeEntity().commissionOrderList.clear();
		
		// 清空成交表
		m_accountStore.storeEntity().dealOrderList.clear();

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
		// reset price 4-5ignore
		price = CUtilsMath.saveNDecimal(price, 3);
		cost = CUtilsMath.saveNDecimal(cost, 3);
				
		float dealCost = 0.0f; // 此费用是成交单费用，买入直接=buycost，卖出=sellcost+buycost
		
		// hold stock
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
				cNewHoldStock.totalBuyCost = 0;
				cNewHoldStock.curPrice = 0;
				cNewHoldStock.refPrimeCostPrice = 0;
				cHoldStock = cNewHoldStock;
				m_accountStore.storeEntity().holdStockList.add(cNewHoldStock);
			}
			
			// 重置对象 (交易费用直接体现在参考成本价里)
			int oriTotalAmount = cHoldStock.totalAmount;
			cHoldStock.totalAmount = cHoldStock.totalAmount + amount;
			cHoldStock.availableAmount = cHoldStock.availableAmount;
			cHoldStock.totalBuyCost = cHoldStock.totalBuyCost + cost;
			cHoldStock.curPrice = price;
			cHoldStock.refPrimeCostPrice = 
					(cHoldStock.refPrimeCostPrice*oriTotalAmount + price*amount + cost)/cHoldStock.totalAmount;
			cHoldStock.refPrimeCostPrice = CUtilsMath.saveNDecimal(cHoldStock.refPrimeCostPrice, 3);
			dealCost = cost;
			
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
				float oriAveBuyCost = CUtilsMath.saveNDecimal(cHoldStock.totalBuyCost/cHoldStock.totalAmount, 3);
				float balanceBuyCost = oriAveBuyCost*amount; // 计算买入费用
				float balanceSellCost = cost; //结算卖出费用
				float sellRawProfit = (price - cHoldStock.refPrimeCostPrice)*amount; // 卖出裸利润
				
				// 重置对象 (交易费用在卖出价钱中扣除)
				cHoldStock.totalAmount = cHoldStock.totalAmount - amount;
				cHoldStock.availableAmount = cHoldStock.availableAmount - amount;
				cHoldStock.totalBuyCost = cHoldStock.totalBuyCost - balanceBuyCost;
				cHoldStock.curPrice = price;
				
				cHoldStock.refPrimeCostPrice = 
						(cHoldStock.refPrimeCostPrice*cHoldStock.totalAmount + cost - sellRawProfit)/cHoldStock.totalAmount;
				cHoldStock.refPrimeCostPrice = CUtilsMath.saveNDecimal(cHoldStock.refPrimeCostPrice, 3);
				
				m_accountStore.storeEntity().money = 
						m_accountStore.storeEntity().money + price*amount - balanceBuyCost - balanceSellCost;
				
				dealCost = balanceBuyCost + balanceSellCost;
				
				// 清仓
				if(cHoldStock.totalAmount == 0)
				{
					m_accountStore.storeEntity().holdStockList.remove(cHoldStock);
				}
			}
			else
			{
				CLog.error("ACCOUNT", "@AccountEntity.onDeal SELL err!\n");
			}
		}
		
		// deal order
		DealOrder dealOrder = new DealOrder();
		dealOrder.date = m_accountStore.storeEntity().date;
		dealOrder.time = m_accountStore.storeEntity().time;
		dealOrder.tranAct = tranact;
		dealOrder.stockID = stockID;
		dealOrder.amount = amount;
		dealOrder.price = price;
		dealOrder.cost = dealCost;
		m_accountStore.storeEntity().dealOrderList.add(dealOrder);
				
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
