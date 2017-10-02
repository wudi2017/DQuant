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
	 * ������������
	 * 
	 * key: "TriggerMode" ����ģʽ
	 *     value: "HistoryTest XXXX-XX-XX XXXX-XXXX-XX" ��ʷ�ز�
	 *     value: "Realtime" ʵʱ
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
				
				// ��ʼ����ʷ�����ձ�
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
		EngineListener cEngineListener = new EngineListener(this);
		DAContext cDAContext = new DAContext();
		m_SharedSession.listenerDataContext.put(cEngineListener, cDAContext);
		return cEngineListener ;
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
		
		
		// call initialize
		List<ListenerCallback> lcbs = m_SharedSession.initializeCbs;
		for(int i=0; i<lcbs.size(); i++)
		{
			ListenerCallback lcb = lcbs.get(i);
			EEInitialize ev = new EEInitialize();
			try {
				lcb.md.invoke(lcb.obj, ev);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		// run CScheduleTaskController
		m_CScheduleTaskController.run();
		
		return 0;
	}
	
	public void subscribe(EngineListener listener, EEID eID, Object obj, String methodname)
	{
		if(eID == EEID.INITIALIZE)
		{
			try {
				if(null != obj && null!= methodname)
				{
					Class<?> clz = Class.forName(obj.getClass().getName());
					Method md = clz.getMethod(methodname, EEObject.class);
					ListenerCallback lcb = new ListenerCallback();
					lcb.listener = listener;
					lcb.obj = obj;
					lcb.md = md;
					m_SharedSession.initializeCbs.add(lcb);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(eID == EEID.TRADINGDAYSTART)
		{
			try {
				if(null != obj && null!= methodname)
				{
					Class<?> clz = Class.forName(obj.getClass().getName());
					Method md = clz.getMethod(methodname, EEObject.class);
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
		else if(eID == EEID.MINUTETIMEPRICES)
		{
			try {
				if(null != obj && null!= methodname)
				{
					Class<?> clz = Class.forName(obj.getClass().getName());
					Method md = clz.getMethod(methodname, EEObject.class);
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
		else if(eID == EEID.TRADINGDAYFINISH)
		{
			try {
				if(null != obj && null!= methodname)
				{
					Class<?> clz = Class.forName(obj.getClass().getName());
					Method md = clz.getMethod(methodname, EEObject.class);
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
		DAContext cDAContext = m_SharedSession.listenerDataContext.get(listener);
		if(null != cDAContext)
		{
			cDAContext.addCurrentDayInterestMinuteDataID(dataID);
		}
	}
	
	private SharedSession m_SharedSession;
	private CScheduleTaskController m_CScheduleTaskController;
}
