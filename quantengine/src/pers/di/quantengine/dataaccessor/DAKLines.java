package pers.di.quantengine.dataaccessor;

import pers.di.dataengine.StockDataEngine.DEKLines;
import pers.di.dataengine.webdata.CommonDef.KLine;

/*
 * -----------------------------------------------------------------------
 * Day KLines 
 */
public class DAKLines
{
	public DAKLines()
	{
	}
	public int size()
	{
		return 0;
	}
	public KLine get(int i)
	{
		return new KLine();
	}
}