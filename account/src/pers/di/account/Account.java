package pers.di.account;

import java.util.List;

import pers.di.account.common.*;
import pers.di.common.*;

public abstract class Account {

	public abstract String ID();
	
	public abstract int getMoney(CObjectContainer<Float> ctnMoney);
	
	public abstract int postTradeOrder(TRANACT tranact, String stockID, int amount, float price);
	
	public abstract int getCommissionOrderList(List<CommissionOrder> ctnList);
	
	public abstract int getHoldStockList(List<HoldStock> ctnList);
}
