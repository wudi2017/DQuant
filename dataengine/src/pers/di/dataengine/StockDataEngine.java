package pers.di.dataengine;

import java.lang.reflect.Method;
import java.util.*;

import org.json.JSONObject;
import pers.di.common.*;
import pers.di.dataengine.tasks.*;
import pers.di.dataapi.StockDataApi;

public class StockDataEngine {
	private static StockDataEngine s_instance = new StockDataEngine(); 
	private StockDataEngine ()
	{
		m_SharedSession = new SharedSession();
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
					m_SharedSession.bConfigFailed = true;
				}
				else
				{
					m_CScheduleTaskController.config("TriggerMode", value);
					m_SharedSession.bHistoryTest = true;
					m_SharedSession.beginDate = beginDate;
					m_SharedSession.endDate = endDate;
				}
			}
			else if(value.contains("Realtime"))
			{
				m_SharedSession.bHistoryTest = false;
				m_CScheduleTaskController.config("TriggerMode", "Realtime");
			}
			else
			{
				CLog.error("DataEngine", "input parameter error!");
				m_SharedSession.bConfigFailed = true;
			}
		}
		else
		{
			CLog.error("DataEngine", "input parameter error!");
			m_SharedSession.bConfigFailed = true;
		}
		return 0;
	}
	
	public EngineListener createListener()
	{
		return new EngineListener(this);
	}
	
	public int run()
	{
		if(m_SharedSession.bConfigFailed) return -1;
		
		// init all task
		m_CScheduleTaskController.schedule(new EngineTaskTrandingDayCheck("09:27:00", this, m_SharedSession));
		for(String time="09:30:00"; time.compareTo("11:30:00")<=0; 
				time=CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(time, 60))
		{
			m_CScheduleTaskController.schedule(new EngineTaskMinuteDataPush(time, m_SharedSession));
		}
		for(String time="13:00:00"; time.compareTo("15:00:00")<=0; 
				time=CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(time, 60))
		{
			m_CScheduleTaskController.schedule(new EngineTaskMinuteDataPush(time, m_SharedSession));
		}
		m_CScheduleTaskController.schedule(new EngineTaskAllDataUpdate("19:00:00", m_SharedSession));
		m_CScheduleTaskController.schedule(new EngineTaskDayFinish("21:00:00", m_SharedSession));
		
		// run CScheduleTaskController
		m_CScheduleTaskController.run();
		
		return 0;
	}
	
	public void subscribe(EngineListener listener, EE_ID eID, Object obj, String methodname)
	{
		if(eID == EE_ID.TRADINGDAYSTART)
		{
			try {
				if(null != obj && null!= methodname)
				{
					Class<?> clz = Class.forName(obj.getClass().getName());
					Method md = clz.getMethod(methodname, EE_Object.class);
					ListenerCallback lcb = new ListenerCallback();
					lcb.listener = listener;
					lcb.obj = obj;
					lcb.md = md;
					m_SharedSession.tranDayStartCbs.add(lcb);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(eID == EE_ID.MINUTETIMEPRICES)
		{
			try {
				if(null != obj && null!= methodname)
				{
					Class<?> clz = Class.forName(obj.getClass().getName());
					Method md = clz.getMethod(methodname, EE_Object.class);
					ListenerCallback lcb = new ListenerCallback();
					lcb.listener = listener;
					lcb.obj = obj;
					lcb.md = md;
					m_SharedSession.minuteTimePricesCbs.add(lcb);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(eID == EE_ID.TRADINGDAYFINISH)
		{
			try {
				if(null != obj && null!= methodname)
				{
					Class<?> clz = Class.forName(obj.getClass().getName());
					Method md = clz.getMethod(methodname, EE_Object.class);
					ListenerCallback lcb = new ListenerCallback();
					lcb.listener = listener;
					lcb.obj = obj;
					lcb.md = md;
					m_SharedSession.tranDayFinishCbs.add(lcb);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void addCurrentDayInterestMinuteDataID(EngineListener listener, String dataID)
	{
		m_SharedSession.dACtx.addCurrentDayInterestMinuteDataID(dataID);
	}
	
	private SharedSession m_SharedSession;
	private CScheduleTaskController m_CScheduleTaskController;
}
