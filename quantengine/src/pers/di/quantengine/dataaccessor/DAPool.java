package pers.di.quantengine.dataaccessor;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CLog;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.StockDataEngine.*;
import pers.di.dataengine.common.*;


/*
 * ���ݷ�����
 * ϸ�ڣ��ڲ�ֻ��ʱ�����ݣ�����Ҫ����ʱ���е���
 */
public class DAPool {
	public DAPool(String date, String time)
	{
	}
	
	public int size()
	{
		return 0;
	}
	
	public DAStock get(int i)
	{
		return new DAStock();
	}
	
	public DAStock get(String stockID)
	{
		return new DAStock();
	}
}
