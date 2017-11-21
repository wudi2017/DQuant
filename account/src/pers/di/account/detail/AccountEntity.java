package pers.di.account.detail;

import java.math.BigDecimal;
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
		
		double availableMoney = 0.0;
		availableMoney = m_accountStore.storeEntity().money;
		for(int i=0; i<m_accountStore.storeEntity().commissionOrderList.size(); i++)
		{
			CommissionOrder cCommissionOrder = m_accountStore.storeEntity().commissionOrderList.get(i);
			if(TRANACT.BUY == cCommissionOrder.tranAct)
			{
				double lockedMoney = cCommissionOrder.price * (cCommissionOrder.amount-cCommissionOrder.dealAmount);
				availableMoney = availableMoney-lockedMoney;
			}
		}
		ctnMoney.set(availableMoney);
		
		return 0;
	}
	
	@Override
	public int postTradeOrder(TRANACT tranact, String stockID, int amount, double price)
	{
		// reset price 4-5ignore
		price = (double)CUtilsMath.saveNDecimal(price, 3);
		
		if(!m_initFlag) return -1;
		
		CLog.output("ACCOUNT", "@AccountEntity CommissionOrder [%s %s] [%s %s %d %.3f]",
				m_accountStore.storeEntity().date, m_accountStore.storeEntity().time,
				tranact.toString(), stockID, amount, price);

		if(tranact == TRANACT.BUY)
		{
			// check input param
			if(0 != amount%100)
			{
				CLog.error("ACCOUNT", "@AccountEntity CommissionOrder amount error!");
				return -1;
			}
			CObjectContainer<Double> money = new CObjectContainer<Double>();
			this.getMoney(money);
			if(Double.compare(money.get(), amount*price) < 0)
			{
				CLog.error("ACCOUNT", "@AccountEntity CommissionOrder money error!");
				return -1;
			}
			
			// create commission order
			CommissionOrder cCommissionOrder = new CommissionOrder();
			cCommissionOrder.date = m_accountStore.storeEntity().date;
			cCommissionOrder.time = m_accountStore.storeEntity().time;
			cCommissionOrder.tranAct = TRANACT.BUY;
			cCommissionOrder.stockID = stockID;
			cCommissionOrder.amount = amount;
			cCommissionOrder.price = price;
			m_accountStore.storeEntity().commissionOrderList.add(cCommissionOrder);
			m_accountStore.sync2File();
			
			m_cIMarketOpe.postTradeRequest(TRANACT.BUY, stockID, amount, price);
			return 0;
		}
		
		if(tranact == TRANACT.SELL)
		{
			// check input param
			boolean bCheck = false;
			for(int i=0; i<m_accountStore.storeEntity().holdStockList.size(); i++)
			{
				HoldStock cHoldStock = m_accountStore.storeEntity().holdStockList.get(i);
				if(cHoldStock.stockID.equals(stockID) && cHoldStock.availableAmount>=amount)
				{
					bCheck = true;
					break;
				}
			}
			if(!bCheck)
			{
				CLog.error("ACCOUNT", "@AccountEntity CommissionOrder availableAmount error!");
				return -1;
			}
						
			// create commission order
			CommissionOrder cCommissionOrder = new CommissionOrder();
			cCommissionOrder.date = m_accountStore.storeEntity().date;
			cCommissionOrder.time = m_accountStore.storeEntity().time;
			cCommissionOrder.tranAct = TRANACT.SELL;
			cCommissionOrder.stockID = stockID;
			cCommissionOrder.amount = amount;
			cCommissionOrder.price = price;
			m_accountStore.storeEntity().commissionOrderList.add(cCommissionOrder);
			m_accountStore.sync2File();
			
			m_cIMarketOpe.postTradeRequest(TRANACT.SELL, stockID, amount, price);
			return 0;
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
					CLog.output("ACCOUNT", "@AccountEntity initialize AccountID:%s err!", accID);
					return -1;
				}
			}
			else
			{
				CLog.output("ACCOUNT", "@AccountEntity initialize AccountID:%s err!", accID);
				return -1;
			}
		}
		
		m_initFlag = true;
		CLog.output("ACCOUNT", "@AccountEntity initialize AccountID:%s OK", accID);
		return 0;
	}
	
	public int setDateTime(String date, String time)
	{
		if(!m_initFlag) return -1;

		m_accountStore.storeEntity().date = date;
		m_accountStore.storeEntity().time = time;
		
		return 0;
	}
	
	public int flushCurrentPrice(String stockID, double price)
	{
		// reset price 4-5ignore
		price = (double)CUtilsMath.saveNDecimal(price, 3);
				
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
	
	public void onDeal(TRANACT tranact, String stockID, int amount, double price, double cost) 
	{
		// reset price 4-5ignore
		price = (double)CUtilsMath.saveNDecimal(price, 3);
		cost = (double)CUtilsMath.saveNDecimal(cost, 3);
				
		double dealCost = 0.0; // 此费用是成交单费用，买入直接=buycost，卖出=sellcost+buycost
		
		// commission order update
		Collections.sort(m_accountStore.storeEntity().commissionOrderList);
		int leftDistribution = amount;
		for(int i=0; i<m_accountStore.storeEntity().commissionOrderList.size(); i++)
		{
			CommissionOrder cCommissionOrder = m_accountStore.storeEntity().commissionOrderList.get(i);
			if(cCommissionOrder.tranAct==tranact && cCommissionOrder.stockID.equals(stockID))
			{
				int currentNeedDistribution = cCommissionOrder.amount-cCommissionOrder.dealAmount;
				if(currentNeedDistribution > 0)
				{
					if(leftDistribution <= currentNeedDistribution)
					{
						cCommissionOrder.dealAmount = cCommissionOrder.dealAmount+leftDistribution;
						leftDistribution = 0;
						break;
					}
					else
					{
						cCommissionOrder.dealAmount = cCommissionOrder.amount;
						leftDistribution = leftDistribution-currentNeedDistribution;
					}
				}
			}
		}
		
		// hold stock update
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
					(cHoldStock.refPrimeCostPrice*oriTotalAmount + price*amount + cost) / cHoldStock.totalAmount;
			cHoldStock.refPrimeCostPrice = CUtilsMath.saveNDecimal(cHoldStock.refPrimeCostPrice, 3);
			dealCost = cost;
			
			// 更新 money
			m_accountStore.storeEntity().money = m_accountStore.storeEntity().money - price*amount;
			m_accountStore.storeEntity().money = CUtilsMath.saveNDecimal(m_accountStore.storeEntity().money, 3);
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
				double oriAveBuyCost = cHoldStock.totalBuyCost/cHoldStock.totalAmount;
				double balanceBuyCost = oriAveBuyCost*amount; // 计算买入费用
				double balanceSellCost = cost; //结算卖出费用
				double sellRawProfit = (price - cHoldStock.refPrimeCostPrice)*amount; // 卖出裸利润
				
				// 重置对象 (交易费用在卖出价钱中扣除)
				cHoldStock.totalAmount = cHoldStock.totalAmount - amount;
				cHoldStock.availableAmount = cHoldStock.availableAmount - amount;
				cHoldStock.totalBuyCost = cHoldStock.totalBuyCost - balanceBuyCost;
				cHoldStock.curPrice = price;
				
				cHoldStock.refPrimeCostPrice = 
						(cHoldStock.refPrimeCostPrice*cHoldStock.totalAmount + cost - sellRawProfit) / cHoldStock.totalAmount;
				cHoldStock.refPrimeCostPrice = CUtilsMath.saveNDecimal(cHoldStock.refPrimeCostPrice, 3);
				
				m_accountStore.storeEntity().money = 
						m_accountStore.storeEntity().money + price*amount - balanceBuyCost - balanceSellCost;
				m_accountStore.storeEntity().money = CUtilsMath.saveNDecimal(m_accountStore.storeEntity().money, 3);
				
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
		dealOrder.cost = (double)dealCost;
		m_accountStore.storeEntity().dealOrderList.add(dealOrder);
				
		CLog.output("ACCOUNT", "@AccountEntity DealOrder [%s %s] [%s %s %d %.3f %.3f(%.3f) %.3f]",
				m_accountStore.storeEntity().date, m_accountStore.storeEntity().time,
				tranact.toString(), stockID, amount, price,
				amount*price, dealCost, m_accountStore.storeEntity().money);
		
		m_accountStore.sync2File();
	}
	
	public int reset(double fInitMoney)
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
