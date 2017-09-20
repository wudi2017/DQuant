package pers.di.dataengine;

import java.util.List;

/*
 * engine data pusher
 */
public class EngineDataPusher {
	
	// Callback·½·¨Ç©Ãû void cb(DataContext ctx)
	public boolean configCallback(Object obj, String methodName)
	{
		return false;
	}
	
	public boolean configPushTime(List<String> timeList)
	{
		return false;
	}
	
	public boolean configPushData(List<String> stockID)
	{
		return false;
	}
}
