package pers.di.quantengine.dataaccessor;

import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.StockDataEngine.DEKLines;

public class DAStock {
	
	public String ID()
	{
		return null;
	}
	
	public String name()
	{
		return null;
	}
	
	public float PE()
	{
		return 0;
	}
	
	/*
	 * ��ȡ����K��
	 */
	public DAKLines dayKLines()
	{
		return new DAKLines();
	}
	
	/*
	 * ��ȡĳ�շ�ʱ��
	 */
	public DATimePrices timePrices(String date)
	{
		return null;
	}
}
