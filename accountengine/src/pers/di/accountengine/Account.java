package pers.di.accountengine;

import java.util.List;

import pers.di.accountengine.common.*;
import pers.di.common.*;

public abstract class Account {

	public abstract ACCOUNTTYPE type();
	public abstract String ID();
	public abstract String password();
	
	public abstract int getTotalAssets(CObjectContainer<Float> ctnTotalAssets);
	public abstract int getMoney(CObjectContainer<Float> ctnMoney);
	public abstract int getAvailableMoney(CObjectContainer<Float> ctnAvailableMoney);
	
	public abstract int pushBuyOrder(String stockID, int amount, float price);
	public abstract int pushSellOrder(String stockID, int amount, float price);
	
	public abstract int getCommissionOrderList(List<CommissionOrder> ctnList);
	public abstract int getBuyCommissionOrderList(List<CommissionOrder> ctnList);
	public abstract int getSellCommissionOrderList(List<CommissionOrder> ctnList);
	
	public abstract int getHoldStockList(List<HoldStock> ctnList);
	public abstract int getHoldStock(String stockID, CObjectContainer<HoldStock> ctnHoldStock);
}
