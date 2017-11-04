package pers.di.quantplatform;

import pers.di.account.*;
import pers.di.account.common.TRANACT;
import pers.di.common.*;

public class AccountProxy {
	
	public AccountProxy(AccoutDriver accoutDriver)
	{
		m_cAccount = accoutDriver.account();
	}
	
	public int pushBuyOrder(String id, int amount, float price)
	{
		return m_cAccount.postTradeOrder(TRANACT.BUY, id, amount, price);
	}
	
	public int pushSellOrder(String id, int amount, float price)
	{
		return m_cAccount.postTradeOrder(TRANACT.SELL, id, amount, price);
	}
	
	public int getMoney(CObjectContainer ctnAvailableMoney)
	{
		return m_cAccount.getMoney(ctnAvailableMoney);
	}
	
	private Account m_cAccount;
}
