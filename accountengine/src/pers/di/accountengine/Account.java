package pers.di.accountengine;

import java.util.List;

import pers.di.accountengine.common.*;
import pers.di.common.*;

public abstract class Account {

	public abstract ACCOUNTTYPE type();
	public abstract String ID();
	public abstract String password();
	
	public abstract int newDayBegin(String date, String time);
	public abstract int newDayEnd(String date, String time);
	
	public abstract int getTotalAssets(String date, String time, CObjectContainer<Float> ctnTotalAssets);
	public abstract int getMoney(String date, String time, CObjectContainer<Float> ctnMoney);
	public abstract int getAvailableMoney(String date, String time, CObjectContainer<Float> ctnAvailableMoney);
	
	public abstract int pushBuyOrder(String date, String time, String stockID, int amount, float price);
	public abstract int pushSellOrder(String date, String time, String stockID, int amount, float price);
	
	public abstract int getCommissionOrderList(List<CommissionOrder> ctnList);
	public abstract int getBuyCommissionOrderList(List<CommissionOrder> ctnList);
	public abstract int getSellCommissionOrderList(List<CommissionOrder> ctnList);
	
	public abstract int getHoldStockList(String date, String time, List<HoldStock> ctnList);
	public abstract int getHoldStock(String date, String time, String stockID, CObjectContainer<HoldStock> ctnHoldStock);
}
