package pers.di.quantplatform;

import pers.di.account.*;
import pers.di.common.*;

public class AccountProxy {
	
	public AccountProxy(AccoutDriver accoutDriver)
	{
	}
	
	public int pushBuyOrder(String id, int amount, float price)
	{
		return 0;
	}
	
	public int pushSellOrder(String id, int amount, float price)
	{
		return 0;
	}
	
	public int getAvailableMoney(CObjectContainer ctnAvailableMoney)
	{
		return 0;
	}
}
