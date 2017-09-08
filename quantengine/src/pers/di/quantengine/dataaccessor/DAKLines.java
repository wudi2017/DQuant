package pers.di.quantengine.dataaccessor;

import pers.di.dataengine.common.*;

/*
 * -----------------------------------------------------------------------
 * Day KLines 
 */
public class DAKLines
{
	public DAKLines(String stockID, String date)
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