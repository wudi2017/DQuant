package pers.di.dataengine;

/***
 * data pusher
 * @author wudi
 *
 */
public class DataPusher {
	
	public DataPusher()
	{
		
	}
	
	/*
	 * property: "PushTimePoint" 交易日推送时间点
	 *     args1: List<String> 
	 *            e.g "08:00:00"...
	 * property: "PushContent" 推动股票ID
	 *     args1: List<String> 
	 *            e.g "600000" ...
	 * property: "setCallback" 
	 *     args1: object
	 *     args1: methodName
	 */
	public boolean config(String property, Object... args)
	{
		return false;
	}
}
