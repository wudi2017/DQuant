package pers.di.dataengine;

import java.util.List;

/*
 * engine data pusher
 */
public class EngineDataPusher {
	
	// Callback����ǩ�� void cb(DataContext ctx)
	public boolean configCallback(Object obj, String methodName)
	{
		return false;
	}
	
	public boolean configPushTime(List<String> timeList)
	{
		return false;
	}
	
	// �������չ�Ʊ��ʱ����
	public boolean enableCurrentDayTimePriceNow(List<String> stockID)
	{
		return false;
	}
}
