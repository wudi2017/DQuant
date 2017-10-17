package pers.di.accountengine.detail;

import java.util.*;

import pers.di.accountengine.common.*;
import pers.di.common.*;

abstract public class IAccountOpe {
	public IAccountOpe() { }

	abstract public ACCOUNTTYPE type();
	abstract public String ID();
	abstract public String password();
	
	// 隔日开始账户初始化
	abstract public int newDayInit();
	// 隔日开始账户交易结束
	abstract public int newDayTranEnd();
	// 推送买单委托
	abstract public int pushBuyOrder(String id, int amount, float price); 
	// 推送卖单委托
	abstract public int pushSellOrder(String id, int amount, float price);
	// 获得账户可用资金
	abstract public int getAvailableMoney(CObjectContainer<Float> ctnAvailableMoney);
	// 获得资金
	abstract public int getMoney(CObjectContainer<Float> ctnMoney);
	// 获得持股列表
	abstract public int getHoldStockList(List<HoldStock> out_list);
}