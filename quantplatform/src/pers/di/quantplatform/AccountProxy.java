package pers.di.quantplatform;

import java.util.ArrayList;
import java.util.List;

import pers.di.account.*;
//import pers.di.account.IAccount.ICallback;
import pers.di.account.common.CommissionOrder;
import pers.di.account.common.HoldStock;
import pers.di.account.common.TRANACT;
import pers.di.common.*;

public class AccountProxy {
	
	public AccountProxy(AccountController cTestAccountController)
	{
		m_cAccount = cTestAccountController.account();
	}
	
	public String ID()
	{
		return m_cAccount.ID();
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
	
	public int getTotalStockMarketValue(CObjectContainer<Double> ctnTotalStockMarketValue)
	{
		return m_cAccount.getTotalStockMarketValue(ctnTotalStockMarketValue);
	}
	
//	public void registerCallback(ICallback cb)
//	{
//		m_cAccount.registerCallback(cb);
//	}
	
	public String dump()
	{
		return m_cAccount.dump();
	}
	
	// extend
	
	public List<String> getHoldStockIDList()
	{
		List<String> retList = new ArrayList<String>();
		List<HoldStock> ctnList = new ArrayList<HoldStock>();
		int iRet = this.getHoldStockList(ctnList);
		if(0 == iRet)
		{
			for(int i=0; i<ctnList.size(); i++)
			{
				HoldStock cHoldStock = ctnList.get(i);
				retList.add(cHoldStock.stockID);
			}
		}
		return retList;
	}
	
	private IAccount m_cAccount;
}
