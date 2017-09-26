package pers.di.dataengine.tasks;

import java.lang.reflect.Method;
import java.util.*;

import org.json.JSONObject;

import pers.di.common.CLog;
import pers.di.dataengine.ENGINEEVENTID;
import pers.di.dataengine.EngineEventContext;
import pers.di.dataengine.EngineEventObject;
import pers.di.dataengine.EngineListener;

public class EngineTaskSharedSession {
	
	public EngineTaskSharedSession()
	{
		m_lcb_tranDayStart = new ArrayList<ListenerCallback>();
		m_lcb_tranDayFinish = new ArrayList<ListenerCallback>();
	}
	
	/*
	 * bIsTranDate
	 */
	public boolean bIsTranDate()
	{
		return bIsTranDate;
	}
	public void setIsTranDate(boolean bFlag){
		bIsTranDate = bFlag;
	}
	private boolean bIsTranDate;
	
	
	/*
	 * bHistoryTest
	 */
	public boolean bHistoryTest()
	{
		return m_bHistoryTest;
	}
	public void setHistoryTest(boolean bFlag){
		m_bHistoryTest = bFlag;
	}
	private boolean m_bHistoryTest;
	
	/*
	 * beginDate
	 */
	public String beginDate()
	{
		return m_beginDate;
	}
	public void setBeginDate(String beginDate){
		m_beginDate = beginDate;
	}
	private String m_beginDate;
	
	/*
	 * endDate
	 */
	public String endDate()
	{
		return m_endDate;
	}
	public void setEndDate(String endDate){
		m_endDate = endDate;
	}
	private String m_endDate;
	
	/*
	 * bConfigFailed
	 */
	public boolean bConfigFailed()
	{
		return m_configFailed;
	}
	public void setConfigFailed(boolean configFailed){
		m_configFailed = configFailed;
	}
	private boolean m_configFailed;
	
	
	/*
	 * subscribe
	 */
	public class ListenerCallback
	{
		public EngineListener listener;
		public Object obj;
		public Method md;
	}
	private List<ListenerCallback> m_lcb_tranDayStart;
	private List<ListenerCallback> m_lcb_tranDayFinish;
	public void subscribe(EngineListener listener, ENGINEEVENTID ID, Object obj, String methodname)
	{
		if(ID == ENGINEEVENTID.TRADINGDAYSTART)
		{
			try {
				if(null != obj && null!= methodname)
				{
					Class<?> clz = Class.forName(obj.getClass().getName());
					Method md = clz.getMethod(methodname, EngineEventContext.class, EngineEventObject.class);
					ListenerCallback lcb = new ListenerCallback();
					lcb.listener = listener;
					lcb.obj = obj;
					lcb.md = md;
					m_lcb_tranDayStart.add(lcb);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else if(ID == ENGINEEVENTID.TRADINGDAYFINISH)
		{
			try {
				if(null != obj && null!= methodname)
				{
					Class<?> clz = Class.forName(obj.getClass().getName());
					Method md = clz.getMethod(methodname, EngineEventContext.class, EngineEventObject.class);
					ListenerCallback lcb = new ListenerCallback();
					lcb.listener = listener;
					lcb.obj = obj;
					lcb.md = md;
					m_lcb_tranDayFinish.add(lcb);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public List<ListenerCallback> getLCBTranDayStart()
	{
		return m_lcb_tranDayStart;
	}
	public List<ListenerCallback> getLCBTranDayFinish()
	{
		return m_lcb_tranDayFinish;
	}
	
	public void setInterestMinuteDataID(EngineListener listener, List<String> stockIDs)
	{
	}
}
