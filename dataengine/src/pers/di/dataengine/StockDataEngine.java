package pers.di.dataengine;

import java.lang.reflect.Method;
import java.util.*;

import org.json.JSONObject;
import pers.di.common.*;
import pers.di.dataengine.tasks.*;
import pers.di.localstock.LocalStock;

public class StockDataEngine {
	private static StockDataEngine s_instance = new StockDataEngine(); 
	private StockDataEngine ()
	{
		m_SharedSession = new SharedSession();
		m_CDateTimeThruster = new CDateTimeThruster();
		CLog.output("DENGINE", "DataRoot: %s", LocalStock.instance().dataRoot());
	}
	public static StockDataEngine instance() {  
		return s_instance;  
	} 
	
	/*
	 * reset dataroot
	 * if not reset the dataroot, the data root is default
	 */
	public boolean resetDataRoot(String dateRoot)
	{
		boolean bRet = LocalStock.instance().resetDataRoot(dateRoot);
		CLog.output("DENGINE", "ResetDataRoot: %s", LocalStock.instance().dataRoot());
		return bRet;
	}
	/*
	 * get dataroot
	 */
	public String getDataRoot()
	{
		return LocalStock.instance().dataRoot();
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
					CLog.warning("DENGINE", "config trigger history test: %s -> %s.", beginDate, endDate);
				}
			}
			else if(value.contains("Realtime"))
			{
				m_SharedSession.bHistoryTest = false;
				m_CDateTimeThruster.config("TriggerMode", "Realtime");
				CLog.warning("DENGINE", "config trigger realtime!");
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

	public int registerListener(IEngineListener listener)
	{
		m_SharedSession.listeners.add(listener);
		DAContext cDAContext = new DAContext();
		m_SharedSession.listenerContext.put(listener, cDAContext);
		return 0;
	}
	
	public int unRegisterListener(IEngineListener listener)
	{
		{
			Iterator<Map.Entry<IEngineListener, DAContext>> iterator = m_SharedSession.listenerContext.entrySet().iterator();
	        while(iterator.hasNext()){
	        	Map.Entry<IEngineListener, DAContext> entry = iterator.next();
	            if (listener == entry.getKey()) {  
	            	iterator.remove();   
	            }  
	        }
		}
		{
			Iterator<IEngineListener> iterator = m_SharedSession.listeners.iterator();  
	        while (iterator.hasNext()) {   
	            if (listener == iterator.next()) {  
	                iterator.remove(); 
	            }  
	        }  
		}
		return 0;
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
		
		
		// callback listener initialize
		for(int i=0; i<m_SharedSession.listeners.size(); i++)
		{
			IEngineListener listener = m_SharedSession.listeners.get(i);
			listener.onInitialize(m_SharedSession.listenerContext.get(listener));
		}
		
		// run CDateTimeThruster
		m_CDateTimeThruster.run();
		
		// callback listener unInitialize
		for(int i=0; i<m_SharedSession.listeners.size(); i++)
		{
			IEngineListener listener = m_SharedSession.listeners.get(i);
			listener.onUnInitialize(m_SharedSession.listenerContext.get(listener));
		}
		
		return 0;
	}
	
	
	
//	public void addCurrentDayInterestMinuteDataID(EngineListener listener, String dataID)
//	{
//		DAContext cDAContext = m_SharedSession.listenerDataContext.get(listener);
//		if(null != cDAContext)
//		{
//			cDAContext.addCurrentDayInterestMinuteDataID(dataID);
//		}
//	}
//	
//	public void removeCurrentDayInterestMinuteDataID(EngineListener listener, String dataID)
//	{
//		DAContext cDAContext = m_SharedSession.listenerDataContext.get(listener);
//		if(null != cDAContext)
//		{
//			cDAContext.removeCurrentDayInterestMinuteDataID(dataID);
//		}
//	}
//	
//	public List<String> getCurrentDayInterestMinuteDataIDs(EngineListener listener)
//	{
//		DAContext cDAContext = m_SharedSession.listenerDataContext.get(listener);
//		if(null != cDAContext)
//		{
//			return cDAContext.getCurrentDayInterestMinuteDataIDs();
//		}
//		else
//		{
//			return null;
//		}
//	}
//	
//	public void clearListener(EngineListener listener)
//	{
//		{
//			Iterator<ListenerCallback> it = m_SharedSession.initializeCbs.iterator();
//			while(it.hasNext()){
//				ListenerCallback lcb = it.next();
//			    if(lcb.listener.equals(listener)){
//			        it.remove();
//			    }
//			}
//		}
//		{
//			Iterator<ListenerCallback> it = m_SharedSession.tranDayStartCbs.iterator();
//			while(it.hasNext()){
//				ListenerCallback lcb = it.next();
//			    if(lcb.listener.equals(listener)){
//			        it.remove();
//			    }
//			}
//		}
//		{
//			Iterator<ListenerCallback> it = m_SharedSession.minuteTimePricesCbs.iterator();
//			while(it.hasNext()){
//				ListenerCallback lcb = it.next();
//			    if(lcb.listener.equals(listener)){
//			        it.remove();
//			    }
//			}
//		}
//		{
//			Iterator<ListenerCallback> it = m_SharedSession.tranDayFinishCbs.iterator();
//			while(it.hasNext()){
//				ListenerCallback lcb = it.next();
//			    if(lcb.listener.equals(listener)){
//			        it.remove();
//			    }
//			}
//		}
//	}
	
	private SharedSession m_SharedSession;
	private CDateTimeThruster m_CDateTimeThruster;
}
