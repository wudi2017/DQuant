package pers.di.dataengine;

import java.util.List;

public class EngineListener {
	
	public EngineListener() 
	{
		
	}
	
	// ���ý���ʱ������б�
	public boolean configTimeMonitorList(List<String> timeList)
	{
		return false;
	}
	
	// ����ʱ���������
	// ����ǩ�� void function(String data, String time)
	public boolean setTimeMonitorMethod(Object obj, String methodName)
	{
		return false;
	}
	
	// ��������ʱ�䴥���б�
	public boolean configDataTrigerTimeList(List<String> timeList)
	{
		return false;
	}
	
	// �������ݶ���
	public boolean configDataSubscribe(List<String> stockID)
	{
		return false;
	}
	
	// �������ݽ��շ���
	// ����ǩ�� void function(DataContext ctx)
	public boolean setDataReceiveMethod(Object obj, String methodName)
	{
		return false;
	}
}
