package pers.di.quantengine.dataaccessor;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CLog;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.StockDataEngine.*;
import pers.di.dataengine.webdata.CommonDef.KLine;
import pers.di.dataengine.webdata.CommonDef.StockBaseInfo;
import pers.di.dataengine.webdata.CommonDef.TimePrice;


/*
 * 数据访问器
 * 细节：内部只有时间数据，当需要访问时进行调用
 */
public class DASPool {
	public DASPool()
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
