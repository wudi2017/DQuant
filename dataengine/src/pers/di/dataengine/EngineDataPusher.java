package pers.di.dataengine;

import java.util.List;

/*
 * engine data pusher
 */
public class EngineDataPusher {
	
	// Callback方法签名 void cb(DataContext ctx)
	public boolean configCallback(Object obj, String methodName)
	{
		return false;
	}
	
	public boolean configPushTime(List<String> timeList)
	{
		return false;
	}
	
	// 开启当日股票分时数据
	public boolean enableCurrentDayTimePriceNow(List<String> stockID)
	{
		return false;
	}
}
