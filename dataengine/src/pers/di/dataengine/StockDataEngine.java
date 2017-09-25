package pers.di.dataengine;

import java.util.*;

import org.json.JSONObject;

import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.baseapi.StockDataApi;
import pers.di.dataengine.common.KLine;
import pers.di.dataengine.common.RealTimeInfo;
import pers.di.dataengine.common.StockUtils;

public class StockDataEngine {
	private static StockDataEngine s_instance = new StockDataEngine(); 
	private StockDataEngine ()
	{
		m_EngineTaskSharedSession = new EngineTaskSharedSession();
		m_EngineTaskSharedSession.setHistoryTest(false);
		m_EngineTaskSharedSession.setBeginDate("0000-00-00");
		m_EngineTaskSharedSession.setEndDate("0000-00-00");
		m_EngineTaskSharedSession.setConfigFailed(false);
		
		m_ScheduleTaskController = new ScheduleTaskController();
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
					m_ScheduleTaskController.config("TriggerMode", value);
					m_EngineTaskSharedSession.setHistoryTest(true);
					m_EngineTaskSharedSession.setBeginDate(beginDate);
					m_EngineTaskSharedSession.setEndDate(endDate);
				}
			}
			else if(value.contains("Realtime"))
			{
				m_EngineTaskSharedSession.setHistoryTest(false);
				m_ScheduleTaskController.config("TriggerMode", "Realtime");
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
		m_ScheduleTaskController.schedule(new EngineTaskTrandingDayCheck("09:27:00", this, m_EngineTaskSharedSession));
		for(String time="09:30:00"; time.compareTo("11:30:00")<=0; 
				time=CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(time, 60))
		{
			m_ScheduleTaskController.schedule(new EngineTaskMinuteDataPush(time, m_EngineTaskSharedSession));
		}
		for(String time="13:00:00"; time.compareTo("15:00:00")<=0; 
				time=CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(time, 60))
		{
			m_ScheduleTaskController.schedule(new EngineTaskMinuteDataPush(time, m_EngineTaskSharedSession));
		}
		m_ScheduleTaskController.schedule(new EngineTaskAllDataUpdate("19:00:00", m_EngineTaskSharedSession));
		m_ScheduleTaskController.schedule(new EngineTaskDayFinish("21:00:00", m_EngineTaskSharedSession));
		
		// run ScheduleTaskController
		m_ScheduleTaskController.run();
		
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
	private ScheduleTaskController m_ScheduleTaskController;
}
