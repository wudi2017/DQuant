package pers.di.account;

import java.util.*;

import pers.di.account.common.*;
import pers.di.common.*;

abstract public class IMarketAccountOpe {
	// 推送买单委托
	abstract public int pushBuyOrder(String id, int amount, float price); 
	// 推送卖单委托
	abstract public int pushSellOrder(String id, int amount, float price);
	// 获得账户可用资金
	abstract public int getAvailableMoney(CObjectContainer<Float> ctnAvailableMoney);
	// 获得持股列表
	abstract public int getHoldStockList(List<HoldStock> out_list);
}