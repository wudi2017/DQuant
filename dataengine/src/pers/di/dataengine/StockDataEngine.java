package pers.di.dataengine;

import java.util.*;

import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.baseapi.StockDataApi;
import pers.di.dataengine.common.KLine;
import pers.di.dataengine.common.RealTimeInfo;
import pers.di.dataengine.common.StockUtils;

public class StockDataEngine {
	
	public static String ENGINEEVENTID_NEWDAYSTART = "NewDayStart";
	public static String ENGINEEVENTID_NEWDAYFINISH = "NewDayFinish";
	public static String ENGINEEVENTID_MINUTEDATAPUSH = "MinuteDataPush";
	public static String ENGINEEVENTID_DAYDATAPUSH = "DayDataPush";
	
	public static class EngineTask_TrandingDayCheck extends ScheduleTask
	{
		public EngineTask_TrandingDayCheck(String time, StockDataEngine sde) {
			super("TrandingDayCheck", time, 16);
			m_dataEngine = sde;
		}
		@Override
		public void doTask(String date, String time) {
			CLog.output("DataEngine", "(%s %s) TrandingDayCheck", date, time);
			if(m_dataEngine.m_bHistoryTest)
			{
				// 数据错误排除,经过测试 次日期内无法从网络获取数据
				if(
					date.equals("2013-03-08")
					|| date.equals("2015-06-09")
					|| date.equals("2016-10-17")
					|| date.equals("2016-11-25")
					)
				{
					return false;
				}
				return m_hisTranDate.contains(date);
			}
			else
			{
				// 确认今天是否是交易日
				String yesterdayDate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(m_curDate, -1);
				StockDataEngine.instance().updateLocalStocks("999999", yesterdayDate);
				CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
				int errKLineListSZZS = StockDataEngine.instance().buildDayKLineListObserver(
						"999999", "2000-01-01", "2100-01-01", obsKLineListSZZS);
				for(int i = 0; i < obsKLineListSZZS.size(); i++)  
		        {  
					KLine cStockDayShangZheng = obsKLineListSZZS.get(i);  
					String checkDateStr = cStockDayShangZheng.date;
					if(checkDateStr.equals(date))
					{
						return true;
					}
		        }
				
				for(int i = 0; i < 5; i++) // 试图5次来确认
				{
					RealTimeInfo ctnRealTimeInfo = new RealTimeInfo();
					int errRealTimeInfo = StockDataEngine.instance().loadRealTimeInfo("999999", ctnRealTimeInfo);
					if(0 == errRealTimeInfo)
					{
						if(ctnRealTimeInfo.date.compareTo(date) == 0)
						{
							return true;
						}
					}
					CThread.msleep(1000);
				}
				return false;
			}
		}
		
		private StockDataEngine m_dataEngine;
	}
	
	public static class EngineTask_MinuteDataPush extends ScheduleTask
	{
		public EngineTask_MinuteDataPush(String time) {
			super("MinuteDataPush", time, 16);
		}
		@Override
		public void doTask(String date, String time) {
			CLog.output("DataEngine", "MinuteDataPush");
		}
	}
	
	public static class EngineTask_AllDataUpdate extends ScheduleTask
	{
		public EngineTask_AllDataUpdate(String time) {
			super("AllDataUpdate", time, 16);
		}
		@Override
		public void doTask(String date, String time) {
			CLog.output("DataEngine", "AllDataUpdate");
		}
	}
	
	public static class EngineTask_DayFinish extends ScheduleTask
	{
		public EngineTask_DayFinish(String time) {
			super("DayFinish", time, 16);
		}
		@Override
		public void doTask(String date, String time) {
			CLog.output("DataEngine", "DayFinish");
		}
	}
	
	
	/*
	 * **********************************************************************************************
	 */
	
	private static StockDataEngine s_instance = new StockDataEngine(); 
	private StockDataEngine ()
	{
		m_bHistoryTest = false;
		m_beginDate = "0000-00-00";
		m_endDate = "0000-00-00";
		m_configFailed = false;
		
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
				m_bHistoryTest = true;
				m_beginDate = cols[1];
				m_endDate = cols[2];
			
				// 初始化历史交易日表
				if( !CUtilsDateTime.CheckValidDate(m_beginDate) 
						|| !CUtilsDateTime.CheckValidDate(m_endDate))
				{
					CLog.error("DataEngine", "input parameter error!");
					m_configFailed = true;
				}
				else
				{
					m_hisTranDate = new ArrayList<String>();
					CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
					int errKLineListSZZS = StockDataApi.instance().buildDayKLineListObserver(
							"999999", "2008-01-01", "2100-01-01", obsKLineListSZZS);
					if(0 != errKLineListSZZS)
					{
						m_configFailed = true;
					}
					int iB = StockUtils.indexDayKAfterDate(obsKLineListSZZS, m_beginDate, true);
					int iE = StockUtils.indexDayKBeforeDate(obsKLineListSZZS, m_endDate, true);
					for(int i = iB; i <= iE; i++)  
			        {  
						KLine cStockDayShangZheng = obsKLineListSZZS.get(i);  
						String curDateStr = cStockDayShangZheng.date;
						m_hisTranDate.add(curDateStr);
			        }
					
					m_ScheduleTaskController.config("TriggerMode", value);
				}
			}
			else if(value.contains("Realtime"))
			{
				m_bHistoryTest = false;
				m_ScheduleTaskController.config("TriggerMode", "Realtime");
			}
			else
			{
				CLog.error("DataEngine", "input parameter error!");
				m_configFailed = true;
			}
		}
		else
		{
			CLog.error("DataEngine", "input parameter error!");
			m_configFailed = true;
		}
		return 0;
	}
	
	public EngineListener createListener()
	{
		return new EngineListener();
	}
	
	public int run()
	{
		if(m_configFailed) return -1;
		
		// init all task
		m_ScheduleTaskController.schedule(new EngineTask_TrandingDayCheck("09:27:00"));
		for(String time="09:30:00"; time.compareTo("11:30:00")<=0; 
				time=CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(time, 60))
		{
			m_ScheduleTaskController.schedule(new EngineTask_MinuteDataPush(time));
		}
		for(String time="13:00:00"; time.compareTo("15:00:00")<=0; 
				time=CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(time, 60))
		{
			m_ScheduleTaskController.schedule(new EngineTask_MinuteDataPush(time));
		}
		m_ScheduleTaskController.schedule(new EngineTask_AllDataUpdate("19:00:00"));
		m_ScheduleTaskController.schedule(new EngineTask_DayFinish("21:00:00"));
		
		// run ScheduleTaskController
		m_ScheduleTaskController.run();
		
		return 0;
	}
	
	/////////////////////////////////////////////////////////////////////////////
	
	private List<String> m_hisTranDate;
	
	private boolean m_bHistoryTest;
	private String m_beginDate;
	private String m_endDate;
	private boolean m_configFailed;
	
	private ScheduleTaskController m_ScheduleTaskController;
}
