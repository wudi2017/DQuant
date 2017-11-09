package pers.di.quantplatform;

import java.util.List;

import pers.di.account.*;
import pers.di.account.common.CommissionOrder;
import pers.di.account.common.HoldStock;
import pers.di.account.common.TRANACT;
import pers.di.common.*;

public class AccountProxy {
	
	public AccountProxy(AccoutDriver accoutDriver)
	{
		m_cAccount = accoutDriver.account();
	}
	
	public int pushBuyOrder(String id, int amount, double price)
	{
		return m_cAccount.postTradeOrder(TRANACT.BUY, id, amount, price);
	}
	
	public int pushSellOrder(String id, int amount, double price)
	{
		return m_cAccount.postTradeOrder(TRANACT.SELL, id, amount, price);
	}
	
	public int getMoney(CObjectContainer ctnAvailableMoney)
	{
		return m_cAccount.getMoney(ctnAvailableMoney);
	}
	
	public int getHoldStockList(List<HoldStock> ctnList)
	{
		return m_cAccount.getHoldStockList(ctnList);
	}
	
	public int getCommissionOrderList(List<CommissionOrder> ctnList)
	{
		return m_cAccount.getCommissionOrderList(ctnList);
	}
	
	public int getTotalAssets(CObjectContainer<Double> ctnTotalAssets)
	{
		return m_cAccount.getTotalAssets(ctnTotalAssets);
	}
	
	public String dump()
	{
		return m_cAccount.dump();
	}
	
	private Account m_cAccount;
}
