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
		m_CDateTimeThruster = new CDateTimeThruster();
	}
	public static StockDataEngine instance() {  
		return s_instance;  
	} 
	
	/*
	 * 重置数据路径
	 * 如果不调用，就是默认路径
	 */
	public boolean resetDataRoot(String dateRoot)
	{
		return StockDataApi.instance().resetDataRoot(dateRoot);
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
					CLog.error("DENGINE", "input parameter error!");
					m_SharedSession.bConfigFailed = true;
				}
				else
				{
					m_CDateTimeThruster.config("TriggerMode", value);
					m_SharedSession.bHistoryTest = true;
					m_SharedSession.beginDate = beginDate;
					m_SharedSession.endDate = endDate;
				}
			}
			else if(value.contains("Realtime"))
			{
				m_SharedSession.bHistoryTest = false;
				m_CDateTimeThruster.config("TriggerMode", "Realtime");
			}
			else
			{
				CLog.error("DENGINE", "input parameter error!");
				m_SharedSession.bConfigFailed = true;
			}
		}
		else
		{
			CLog.error("DENGINE", "input parameter error!");
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
		m_CDateTimeThruster.schedule(new EngineTaskTrandingDayCheck("09:27:00", this, m_SharedSession));
		for(String time="09:30:00"; time.compareTo("11:30:00")<=0; 
				time=CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(time, 60))
		{
			m_CDateTimeThruster.schedule(new EngineTaskMinuteDataPush(time, m_SharedSession));
		}
		for(String time="13:00:00"; time.compareTo("15:00:00")<=0; 
				time=CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(time, 60))
		{
			m_CDateTimeThruster.schedule(new EngineTaskMinuteDataPush(time, m_SharedSession));
		}
		m_CDateTimeThruster.schedule(new EngineTaskAllDataUpdate("19:00:00", m_SharedSession));
		m_CDateTimeThruster.schedule(new EngineTaskDayFinish("21:00:00", m_SharedSession));
		
		
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
		
		// run CDateTimeThruster
		m_CDateTimeThruster.run();
		
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
	
	public void clearListener(EngineListener listener)
	{
		{
			Iterator<ListenerCallback> it = m_SharedSession.initializeCbs.iterator();
			while(it.hasNext()){
				ListenerCallback lcb = it.next();
			    if(lcb.listener.equals(listener)){
			        it.remove();
			    }
			}
		}
		{
			Iterator<ListenerCallback> it = m_SharedSession.tranDayStartCbs.iterator();
			while(it.hasNext()){
				ListenerCallback lcb = it.next();
			    if(lcb.listener.equals(listener)){
			        it.remove();
			    }
			}
		}
		{
			Iterator<ListenerCallback> it = m_SharedSession.minuteTimePricesCbs.iterator();
			while(it.hasNext()){
				ListenerCallback lcb = it.next();
			    if(lcb.listener.equals(listener)){
			        it.remove();
			    }
			}
		}
		{
			Iterator<ListenerCallback> it = m_SharedSession.tranDayFinishCbs.iterator();
			while(it.hasNext()){
				ListenerCallback lcb = it.next();
			    if(lcb.listener.equals(listener)){
			        it.remove();
			    }
			}
		}
	}
	
	private SharedSession m_SharedSession;
	private CDateTimeThruster m_CDateTimeThruster;
}
