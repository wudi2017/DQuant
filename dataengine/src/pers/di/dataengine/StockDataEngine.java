package pers.di.dataengine;

import java.util.*;

import org.json.JSONObject;

import pers.di.common.*;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.RealTimeInfo;
import pers.di.dataapi.common.StockUtils;
import pers.di.dataengine.tasks.*;
import pers.di.dataapi.StockDataApi;

public class StockDataEngine {
	private static StockDataEngine s_instance = new StockDataEngine(); 
	private StockDataEngine ()
	{
		m_EngineTaskSharedSession = new EngineTaskSharedSession();
		m_EngineTaskSharedSession.setHistoryTest(false);
		m_EngineTaskSharedSession.setBeginDate("0000-00-00");
		m_EngineTaskSharedSession.setEndDate("0000-00-00");
		m_EngineTaskSharedSession.setConfigFailed(false);
		
		m_CScheduleTaskController = new CScheduleTaskController();
	}
	public static StockDataEngine instance() {  
		return s_instance;  
	} 

	/*
	 * 配置量化引擎
	 * 
	 * key: "TriggerMode" 触发模式
	 *     value: "HistoryTest XXXX-XX-XX XXXX-XXXX-XX" 历史回测
	 *     value: "Realtime" 实时
	 */
	public int config(String key, String value)
	{
		if(0 == key.compareTo("TriggerMode"))
		{
			if(value.contains("HistoryTest"))
			{	
				String[] cols = value.split(" ");
				String beginDate = cols[1];
				String endDate = cols[2];
				
				// 初始化历史交易日表
				if( !CUtilsDateTime.CheckValidDate(beginDate) 
						|| !CUtilsDateTime.CheckValidDate(endDate))
				{
					CLog.error("DataEngine", "input parameter error!");
					m_EngineTaskSharedSession.setConfigFailed(true);
				}
				else
				{
					m_CScheduleTaskController.config("TriggerMode", value);
					m_EngineTaskSharedSession.setHistoryTest(true);
					m_EngineTaskSharedSession.setBeginDate(beginDate);
					m_EngineTaskSharedSession.setEndDate(endDate);
				}
			}
			else if(value.contains("Realtime"))
			{
				m_EngineTaskSharedSession.setHistoryTest(false);
				m_CScheduleTaskController.config("TriggerMode", "Realtime");
			}
			else
			{
				CLog.error("DataEngine", "input parameter error!");
				m_EngineTaskSharedSession.setConfigFailed(true);
			}
		}
		else
		{
			CLog.error("DataEngine", "input parameter error!");
			m_EngineTaskSharedSession.setConfigFailed(true);
		}
		return 0;
	}
	
	public EngineListener createListener()
	{
		return new EngineListener(this);
	}
	
	public int run()
	{
		if(m_EngineTaskSharedSession.bConfigFailed()) return -1;
		
		// init all task
		m_CScheduleTaskController.schedule(new EngineTaskTrandingDayCheck("09:27:00", this, m_EngineTaskSharedSession));
		for(String time="09:30:00"; time.compareTo("11:30:00")<=0; 
				time=CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(time, 60))
		{
			m_CScheduleTaskController.schedule(new EngineTaskMinuteDataPush(time, m_EngineTaskSharedSession));
		}
		for(String time="13:00:00"; time.compareTo("15:00:00")<=0; 
				time=CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(time, 60))
		{
			m_CScheduleTaskController.schedule(new EngineTaskMinuteDataPush(time, m_EngineTaskSharedSession));
		}
		m_CScheduleTaskController.schedule(new EngineTaskAllDataUpdate("19:00:00", m_EngineTaskSharedSession));
		m_CScheduleTaskController.schedule(new EngineTaskDayFinish("21:00:00", m_EngineTaskSharedSession));
		
		// run CScheduleTaskController
		m_CScheduleTaskController.run();
		
		return 0;
	}
	
	public void subscribe(EngineListener listener, ENGINEEVENTID ID, Object obj, String methodname)
	{
		m_EngineTaskSharedSession.subscribe(listener, ID, obj, methodname);
	}
	
	public void setInterestMinuteDataID(EngineListener listener, List<String> stockIDs)
	{
		m_EngineTaskSharedSession.setInterestMinuteDataID(listener, stockIDs);
	}
	
	private EngineTaskSharedSession m_EngineTaskSharedSession;
	private CScheduleTaskController m_CScheduleTaskController;
}
