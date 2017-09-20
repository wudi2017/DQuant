package pers.di.dataengine;

import java.util.List;

/*
 * engine time listener
 */
public class EngineTimeListener {
	
	// Callback·½·¨Ç©Ãû void cb(String date, String time)
	public boolean configCallback(Object obj, String methodName)
	{
		return false;
	}
	
	public boolean configListenTime(List<String> timeList)
	{
		return false;
	}
}
