package pers.di.accountengine.detail;

import java.util.*;

import pers.di.accountengine.common.*;
import pers.di.common.*;

abstract public class IAccountOpe {
	public IAccountOpe() { }

	abstract public String ID();
	abstract public String password();
	abstract public ACCOUNTTYPE type();
	
	// 隔日开始账户初始化
	abstract public int newDayInit(String date, String time);
	// 隔日开始账户交易结束
	abstract public int newDayTranEnd(String date, String time);
	// 推送买单委托
	abstract public int pushBuyOrder(String date, String time, String id, int amount, float price); 
	// 推送卖单委托
	abstract public int pushSellOrder(String date, String time, String id, int amount, float price);
	// 获得账户可用资金
	abstract public int getAvailableMoney(String date, String time, CObjectContainer<Float> ctnAvailableMoney);
	// 获得资金
	abstract public int getMoney(String date, String time, CObjectContainer<Float> ctnMoney);
	// 获得持股列表
	abstract public int getHoldStockList(String date, String time, List<HoldStock> out_list);
}