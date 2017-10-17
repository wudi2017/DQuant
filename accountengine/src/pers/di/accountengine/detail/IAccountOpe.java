package pers.di.accountengine.detail;

import java.util.*;

import pers.di.accountengine.common.*;
import pers.di.common.*;

abstract public class IAccountOpe {
	public IAccountOpe() { }

	abstract public ACCOUNTTYPE type();
	abstract public String ID();
	abstract public String password();
	
	// ���տ�ʼ�˻���ʼ��
	abstract public int newDayInit();
	// ���տ�ʼ�˻����׽���
	abstract public int newDayTranEnd();
	// ������ί��
	abstract public int pushBuyOrder(String id, int amount, float price); 
	// ��������ί��
	abstract public int pushSellOrder(String id, int amount, float price);
	// ����˻������ʽ�
	abstract public int getAvailableMoney(CObjectContainer<Float> ctnAvailableMoney);
	// ����ʽ�
	abstract public int getMoney(CObjectContainer<Float> ctnMoney);
	// ��óֹ��б�
	abstract public int getHoldStockList(List<HoldStock> out_list);
}