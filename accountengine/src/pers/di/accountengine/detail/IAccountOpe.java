package pers.di.accountengine.detail;

import java.util.*;

import pers.di.accountengine.common.*;
import pers.di.common.*;

abstract public class IAccountOpe {
	public IAccountOpe() { }

	abstract public String ID();
	abstract public String password();
	abstract public ACCOUNTTYPE type();
	
	// ���տ�ʼ�˻���ʼ��
	abstract public int newDayInit(String date, String time);
	// ���տ�ʼ�˻����׽���
	abstract public int newDayTranEnd(String date, String time);
	// ������ί��
	abstract public int pushBuyOrder(String date, String time, String id, int amount, float price); 
	// ��������ί��
	abstract public int pushSellOrder(String date, String time, String id, int amount, float price);
	// ����˻������ʽ�
	abstract public int getAvailableMoney(String date, String time, CObjectContainer<Float> ctnAvailableMoney);
	// ����ʽ�
	abstract public int getMoney(String date, String time, CObjectContainer<Float> ctnMoney);
	// ��óֹ��б�
	abstract public int getHoldStockList(String date, String time, List<HoldStock> out_list);
}