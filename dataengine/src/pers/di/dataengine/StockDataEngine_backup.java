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

public class StockDataEngine_backup {
	private static StockDataEngine_backup s_instance = new StockDataEngine_backup(); 
	private StockDataEngine_backup ()
	{
		m_bHistoryTest = false;
		m_beginDate = "0000-00-00";
		m_endDate = "0000-00-00";
		m_configFailed = false;
		
		m_ScheduleTaskController = new ScheduleTaskController();
	}
	public static StockDataEngine_backup instance() {  
		return s_instance;  
	} 
	

//	
//	public static class EngineBuildinTask_CheckTranDay extends EngineTask
//	{
//
//		public EngineBuildinTask_CheckTranDay(StockDataEngine engine) {
//			super("EngineBuildinTask_CheckTranDay");
//			m_engine = engine;
//		}
//
//		@Override
//		public void run(String date, String time) {
//			// TODO Auto-generated method stub
//			m_engine.checkTranDate(date);
//		}
//		
//		private StockDataEngine m_engine;
//	}
	
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
	
	public int run()
	{
		if(m_configFailed) return -1;
		
		// init
		
		// run ScheduleTaskController
		m_ScheduleTaskController.run();
		
		return 0;
	}
	
//	public int run()
//	{
//		if(m_configFailed)
//		{
//			return 0;
//		}
//		
//		EngineBuildinTask_CheckTranDay cEngineBuildinTask_CheckTranDay = new EngineBuildinTask_CheckTranDay(this);
//		scheduleEngineTask("09:30:00", cEngineBuildinTask_CheckTranDay);
//		
//		CLog.output("DataEngine", "The DataEngine is running now...");
//		
//		String dateStr = getStartDate();
//		while(null != dateStr) 
//		{
//			CLog.output("DataEngine", "date %s", dateStr);
//			
//			if(!m_curDateIgnore)
//			{
//				// do one day all time tasks
//				TimeTasks cTimeTasks = getMainTimeTaskMapFirstTimeTasks();
//				while(null != cTimeTasks)
//				{
//					String timeStr = cTimeTasks.time;
//					
//					waitForDateTime(dateStr, timeStr);
//					
//					CLog.output("DataEngine", "[%s %s]", dateStr, timeStr);
//					doAllTask(cTimeTasks);
//					
//					if(m_curDateIgnore)
//					{
//						break;
//					}
//					
//					cTimeTasks = getMainTimeTaskMapNextTimeTasks();
//				}
//			}
//			
//			dateStr = getNextDate();
//		}
//		
////		// 每天进行循环
////		String dateStr = getStartDate();
////		while(true) 
////		{
////			String timestr = "00:00:00";
////			
////			// 09:25确定是否是交易日
////			boolean bIsTranDate = false;
////			timestr = "09:25:00";
////			waitForDateTime(dateStr, timestr);
////			if(isTranDate(dateStr))
////			{
////				bIsTranDate = true;
////			}
////			CLog.output("DataEngine", "[%s %s] check market day = %b ", dateStr, timestr, bIsTranDate);
////			
////			if(bIsTranDate)
////			{
////				// 09:27 触发onDayBegin
////				timestr = "09:27:00";
////				waitForDateTime(dateStr, timestr);
////				CLog.output("DataEngine", "[%s %s] listener.onDayBegin ", dateStr, timestr);
////				m_context.setDateTime(dateStr, timestr);
////				
////				// 9:30-11:30 1:00-3:00 定期间隔进行触发trigger.onEveryMinute
////				int interval_min = 1;
////				String timestr_begin = "09:30:00";
////				String timestr_end = "11:30:00";
////				timestr = timestr_begin;
////				while(true)
////				{
////					if(waitForDateTime(dateStr, timestr))
////					{
////						CLog.output("DataEngine", "[%s %s] listener.onTransactionEveryMinute ", dateStr, timestr);
////						m_context.setDateTime(dateStr, timestr);
////					}
////					timestr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(timestr, interval_min*60);
////					if(timestr.compareTo(timestr_end) > 0) break;
////				}
////				
////				timestr_begin = "13:00:00";
////				timestr_end = "15:00:00";
////				timestr = timestr_begin;
////				while(true)
////				{
////					if(waitForDateTime(dateStr, timestr))
////					{
////						CLog.output("DataEngine", "[%s %s] listener.onTransactionEveryMinute ", dateStr, timestr);
////						m_context.setDateTime(dateStr, timestr);
////					}
////					timestr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(timestr, interval_min*60);
////					if(timestr.compareTo(timestr_end) > 0) break;
////				}
////
////				// 19:00 更新历史数据
////				timestr = "19:00:00";
////				if(waitForDateTime(dateStr, timestr))
////				{
////					CLog.output("DataEngine", "[%s %s] update market data ", dateStr, timestr);
////					m_stockDataApi.updateAllLocalStocks(dateStr);
////				}
////				
////				// 20:00 触发trigger.onDayEnd
////				timestr = "21:00:00";
////				waitForDateTime(dateStr, timestr);
////				CLog.output("DataEngine", "[%s %s] listener.onDayEnd ", dateStr, timestr);
////				m_context.setDateTime(dateStr, timestr);
////			}
////			
////			// 获取下一日期
////			dateStr = getNextDate();
////			if(null == dateStr) break;
////		}
////				
//		return 0;
//	}

	/*
	 * realtime模式
	 * 	一直等待到9:25返回是否是交易日，根据上证指数实时变化确定
	 * historymock模式
	 * 	根据上证指数直接确定是否是交易日
	 */
//	private static enum TRANCHECKRESULT
//	{
//		UNKNOWN,
//		TRANDATE_OK,
//		TRANDATE_NG,
//	}
//	private boolean checkTranDate(String date)
//	{
//		if(m_bHistoryTest)
//		{
//			// 数据错误排除,经过测试 次日期内无法从网络获取数据
//			if(
//				date.equals("2013-03-08")
//				|| date.equals("2015-06-09")
//				|| date.equals("2016-10-17")
//				|| date.equals("2016-11-25")
//				)
//			{
//				return false;
//			}
//			return m_hisTranDate.contains(date);
//		}
//		else
//		{
//			// 确认今天是否是交易日
//			String yesterdayDate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(m_curDate, -1);
//			m_stockDataApi.updateLocalStocks("999999", yesterdayDate);
//			CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
//			int errKLineListSZZS = m_stockDataApi.buildDayKLineListObserver(
//					"999999", "2000-01-01", "2100-01-01", obsKLineListSZZS);
//			for(int i = 0; i < obsKLineListSZZS.size(); i++)  
//	        {  
//				KLine cStockDayShangZheng = obsKLineListSZZS.get(i);  
//				String checkDateStr = cStockDayShangZheng.date;
//				if(checkDateStr.equals(date))
//				{
//					m_curDateIsTranDay = true;
//					return;
//				}
//	        }
//			
//			for(int i = 0; i < 5; i++) // 试图5次来确认
//			{
//				RealTimeInfo ctnRealTimeInfo = new RealTimeInfo();
//				int errRealTimeInfo = m_stockDataApi.loadRealTimeInfo("999999", ctnRealTimeInfo);
//				if(0 == errRealTimeInfo)
//				{
//					if(ctnRealTimeInfo.date.compareTo(date) == 0)
//					{
//						m_curDateIsTranDay = true;
//						return;
//					}
//				}
//				CThread.msleep(1000);
//			}
//			m_curDateIsTranDay = false;
//			return;
//		}
//	}
	
	
	
	
	
	private boolean m_curDateIgnore;

	private DataContext m_context;

	private List<String> m_hisTranDate;
	
	
	/////////////////////////////////////////////////////////////////////////////
	
	private boolean m_bHistoryTest;
	private String m_beginDate;
	private String m_endDate;
	private boolean m_configFailed;
	
	private ScheduleTaskController m_ScheduleTaskController;
	private StockDataApi m_stockDataApi;
}
