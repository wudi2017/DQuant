package pers.di.account;

import java.util.*;

import pers.di.account.common.*;
import pers.di.common.*;

abstract public class IMarketAccountOpe {
	// ������ί��
	abstract public int pushBuyOrder(String id, int amount, float price); 
	// ��������ί��
	abstract public int pushSellOrder(String id, int amount, float price);
	// ����˻������ʽ�
	abstract public int getAvailableMoney(CObjectContainer<Float> ctnAvailableMoney);
	// ��óֹ��б�
	abstract public int getHoldStockList(List<HoldStock> out_list);
}