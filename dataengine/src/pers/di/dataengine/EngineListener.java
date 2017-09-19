package pers.di.dataengine;

import java.util.List;

public class EngineListener {
	
	public EngineListener() 
	{
		
	}
	
	// 配置交易时间监听列表
	public boolean configTimeMonitorList(List<String> timeList)
	{
		return false;
	}
	
	// 设置时间监听方法
	// 方法签名 void function(String data, String time)
	public boolean setTimeMonitorMethod(Object obj, String methodName)
	{
		return false;
	}
	
	// 配置数据时间触发列表
	public boolean configDataTrigerTimeList(List<String> timeList)
	{
		return false;
	}
	
	// 配置数据订阅
	public boolean configDataSubscribe(List<String> stockID)
	{
		return false;
	}
	
	// 设置数据接收方法
	// 方法签名 void function(DataContext ctx)
	public boolean setDataReceiveMethod(Object obj, String methodName)
	{
		return false;
	}
}
