package pers.di.marketaccount.mock;

import java.util.*;

import pers.di.account.IMarketOpe;
import pers.di.account.common.HoldStock;
import pers.di.account.common.TRANACT;
import pers.di.account.detail.*;
import pers.di.common.*;

public class MockAccountOpe extends IMarketOpe {
	
	public int postTradeRequest(TRANACT tranact, String id, int amount, double price)
	{
		super.dealReply(tranact, id, amount, price, 0.0f);
		
		return 0;
	}
	
//	public MockAccountOpe()
//	{
//		super();
//		
//		m_transactionCostsRatio = 0.0016f;
//		m_accountID = null;
//		m_password = null;
//		
//		m_mockAccountOpeStore = null;
//		m_initFlag = false;
//	}
//	
//	@Override
//	public int pushBuyOrder(String stockID, int amount, double price) {
//		
//		if(!m_initFlag) return -1;
//		
//		// 买入量标准化
//		int maxBuyAmount = (int)(m_mockAccountOpeStore.storeEntity().money/price);
//		int realBuyAmount = Math.min(maxBuyAmount, amount);
//		realBuyAmount = realBuyAmount/100*100; 
//		if(realBuyAmount <= 0) 
//		{
//			return 0;
//		}
//		
//		// 获取持有对象
//		HoldStock cHoldStock = null;
//		for(int i = 0; i< m_mockAccountOpeStore.storeEntity().holdStockList.size(); i++)
//		{
//			HoldStock cTmpHoldStock = m_mockAccountOpeStore.storeEntity().holdStockList.get(i);
//			if(cTmpHoldStock.stockID == stockID)
//			{
//				cHoldStock = cTmpHoldStock;
//				break;
//			}
//		}
//		if(null == cHoldStock)
//		{
//			HoldStock cNewHoldStock = new HoldStock();
//			cNewHoldStock.stockID = stockID;
//			cNewHoldStock.curPrice = price;
//			m_mockAccountOpeStore.storeEntity().holdStockList.add(cNewHoldStock);
//			cHoldStock = cNewHoldStock;
//		}
//		
//		// 重置对象 (交易费用直接体现在参考成本价里)
//		double transactionCosts = m_transactionCostsRatio*price*realBuyAmount;
//		int oriTotalAmount = cHoldStock.totalAmount;
//		double oriHoldAvePrice = cHoldStock.refPrimeCostPrice;
//		cHoldStock.totalAmount = cHoldStock.totalAmount + realBuyAmount;
//		cHoldStock.refPrimeCostPrice = (oriHoldAvePrice*oriTotalAmount + price*realBuyAmount + transactionCosts)/cHoldStock.totalAmount;
//		cHoldStock.curPrice = price;
//		
//		m_mockAccountOpeStore.storeEntity().money = m_mockAccountOpeStore.storeEntity().money - realBuyAmount*price;
//		
//		CLog.output("ACCOUNT", " @MockAccountOpe pushBuyOrder [%s %d %.3f %.3f(%.3f) %.3f] \n",
//				stockID, realBuyAmount, price, realBuyAmount*price, transactionCosts, m_mockAccountOpeStore.storeEntity().money);
//		
//		return 0;
//	}
//
//	@Override
//	public int pushSellOrder(String stockID, int amount, double price) {
//		
//		if(!m_initFlag) return -1;
//		
//		// 获取持有对象
//		HoldStock cHoldStock = null;
//		for(int i = 0; i< m_mockAccountOpeStore.storeEntity().holdStockList.size(); i++)
//		{
//			HoldStock cTmpHoldStock = m_mockAccountOpeStore.storeEntity().holdStockList.get(i);
//			if(cTmpHoldStock.stockID.equals(stockID))
//			{
//				cHoldStock = cTmpHoldStock;
//				break;
//			}
//		}
//		
//		if(null != cHoldStock)
//		{
//			// 卖出量标准化
//			int realSellAmount = Math.min(cHoldStock.totalAmount, amount);
//			realSellAmount = realSellAmount/100*100;
//			
//			// 重置对象 (交易费用在卖出价钱中扣除)
//			double transactionCosts = m_transactionCostsRatio*price*realSellAmount;
//			int oriTotalAmount = cHoldStock.totalAmount;
//			double oriHoldAvePrice = cHoldStock.refPrimeCostPrice;
//			cHoldStock.totalAmount = cHoldStock.totalAmount - realSellAmount;
//			cHoldStock.curPrice = price;
//			m_mockAccountOpeStore.storeEntity().money = m_mockAccountOpeStore.storeEntity().money + price*realSellAmount - transactionCosts;
//			if(cHoldStock.totalAmount == 0) // 卖光则不计算买入价格 清零
//			{
//				cHoldStock.refPrimeCostPrice = 0.0f;
//			}
//			else
//			{
//				cHoldStock.refPrimeCostPrice = (oriHoldAvePrice*oriTotalAmount - price*realSellAmount - transactionCosts)/cHoldStock.totalAmount;
//			}
//			
//			// 清仓计算
//			if(cHoldStock.totalAmount == 0)
//			{
//				m_mockAccountOpeStore.storeEntity().holdStockList.remove(cHoldStock);
//			}
//			
//			CLog.output("ACCOUNT", " @MockAccountOpe pushSellOrder [%s %d %.3f %.3f(%.3f) %.3f] \n", 
//					stockID, realSellAmount, price, realSellAmount*price, transactionCosts, m_mockAccountOpeStore.storeEntity().money);
//			
//			return 0;
//		}
//	
//		return 0;
//	}
//
//	@Override
//	public int getAvailableMoney(CObjectContainer<double> ctnAvailableMoney) {
//		
//		if(!m_initFlag) return -1;
//		
//		ctnAvailableMoney.set(m_mockAccountOpeStore.storeEntity().money);
//		
//		return 0;
//	}
//	
//	@Override
//	public int getHoldStockList(List<HoldStock> out_list) {
//		
//		if(!m_initFlag) return -1;
//		
//		out_list.addAll(m_mockAccountOpeStore.storeEntity().holdStockList);
//		
//		return 0;
//	}
//	
//	public int initialize(String accountID, String password)
//	{
//		m_transactionCostsRatio = 0.0016f;
//		
//		m_accountID = accountID;
//		m_password = password;
//
//		m_mockAccountOpeStore = new MockAccountOpeStore(m_accountID, m_password);
//		m_initFlag = m_mockAccountOpeStore.load();
//		if(m_initFlag)
//		{
//			CLog.output("ACCOUNT", " @MockAccountOpe initialize AccountID:%s Password:%s money:%.2f transactionCostsRatio:%.4f\n", 
//					m_accountID, password, m_mockAccountOpeStore.storeEntity().money, m_transactionCostsRatio);
//			return 0;
//		}
//		else
//		{
//			CLog.output("ACCOUNT", " @MockAccountOpe initialize AccountID:%s Password:%s failed\n", 
//					m_accountID, password);
//			return -1;
//		}
//	}
//
//	/**
//	 * 成员-----------------------------------------------------------------------
//	 */
//
//	private double m_transactionCostsRatio;
//	private String m_accountID;
//	private String m_password;
//
//	private MockAccountOpeStore m_mockAccountOpeStore;
//	private boolean m_initFlag;

}