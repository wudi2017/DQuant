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
	 * 获取次日K线
	 */
	public DAKLines dayKLines()
	{
		return new DAKLines();
	}
	
	/*
	 * 获取某日分时线
	 */
	public DATimePrices timePrices(String date)
	{
		return null;
	}
}
