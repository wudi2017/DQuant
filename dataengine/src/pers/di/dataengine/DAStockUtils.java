package pers.di.dataengine;

import java.util.List;

import pers.di.dataapi.common.KLine;

public class DAStockUtils {
	// 计算i到j日的最高价格的索引
	static public int indexHigh(DAKLines kLines, int i, int j)
	{
		int index = i;
		double high = -100000.0f;
		for(int k = i; k<=j; k++ )
		{
			KLine cDayKDataTmp = kLines.get(k);
			if(cDayKDataTmp.high > high) 
			{
				high = cDayKDataTmp.high;
				index = k;
			}
		}
		return index;
	}
	
	// 计算i到j日的最低价格的索引
	static public int indexLow(DAKLines kLines, int i, int j)
	{
		int index = i;
		double low = 100000.0f;
		for(int k = i; k<=j; k++ )
		{
			KLine cDayKDataTmp = kLines.get(k);
			if(cDayKDataTmp.low < low) 
			{
				low = cDayKDataTmp.low;
				index = k;
			}
		}
		return index;
	}
}
