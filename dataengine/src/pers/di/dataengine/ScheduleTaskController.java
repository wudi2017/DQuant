package pers.di.dataengine;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.*;

/*
 * 计划任务控制器
 */
public class ScheduleTaskController {
	
	public static abstract class DateTimeProvider
	{
		public abstract String date();
		public abstract String time();
	}
	
	public static class DefaultDateTimeProvider extends DateTimeProvider
	{
		@Override
		public String date() {
			return CUtilsDateTime.GetCurDateStr();
		}

		@Override
		public String time() {
			return CUtilsDateTime.GetCurTimeStr();
		}
	}

	private static class TimeTasksPair
	{
		public TimeTasksPair()
		{
			time = "00:00:00";
			tasks = new ArrayList<ScheduleTask>();
		}
		public String time;
		public List<ScheduleTask> tasks;
	}
	
	public ScheduleTaskController()
	{
		m_BeginDate = "0000-00-00";
		m_BeginEnd = "0000-00-00";
		m_DateTimeProvider = new DefaultDateTimeProvider();
		m_mainTimeTaskList = new ArrayList<TimeTasksPair>();
	}
	
	public boolean schedule(ScheduleTask cScheduleTask)
	{
		// find TimeTasksPair
		TimeTasksPair cCurTimeTasksPair = null;
		for(int i=0; i<m_mainTimeTaskList.size(); i++)
		{
			TimeTasksPair cTmpTimeTasksPair = m_mainTimeTaskList.get(i);
			if(cScheduleTask.getTime().compareTo(cTmpTimeTasksPair.time) == 0)
			{
				cCurTimeTasksPair = cTmpTimeTasksPair;
				break;
			}
			if(cScheduleTask.getTime().compareTo(cTmpTimeTasksPair.time) > 0)
			{
				TimeTasksPair cNewTimeTasksPair = new TimeTasksPair();
				m_mainTimeTaskList.add(i+1, cNewTimeTasksPair);
				cCurTimeTasksPair = cNewTimeTasksPair;
				break;
			}
		}
		if(null == cCurTimeTasksPair)
		{
			TimeTasksPair cNewTimeTasksPair = new TimeTasksPair();
			cNewTimeTasksPair.time = cScheduleTask.getTime();
			m_mainTimeTaskList.add(0, cNewTimeTasksPair);
			cCurTimeTasksPair = cNewTimeTasksPair;
		}
		
		// insert task to list
		List<ScheduleTask> tasks = cCurTimeTasksPair.tasks;
		if(tasks.size() > 0)
		{
			for(int i=0; i<tasks.size(); i++)
			{
				ScheduleTask task = cCurTimeTasksPair.tasks.get(i);
				if(cScheduleTask.getPriority() <= task.getPriority())
				{
					continue;
				}
				else
				{
					tasks.add(cScheduleTask);
					break;
				}
			}
		}
		else
		{
			tasks.add(cScheduleTask);
		}

		return true;
	}
	
	
	public boolean config_RunDatePeriod(String beginDate, String endDate)
	{
		m_BeginDate = beginDate;
		m_BeginEnd = endDate;
		return true;
	}
	
	public boolean config_DatetimeProvider(DateTimeProvider cDateTimeProvider)
	{
		m_DateTimeProvider = cDateTimeProvider;
		return true;
	}
	
	public boolean run()
	{
		String dateStr = m_BeginDate;
		while(true) 
		{
			CLog.output("DataEngine", "date %s", dateStr);
			
			
			dateStr = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(dateStr, 1);
			if(dateStr.compareTo(m_BeginEnd) > 0)
			{
				break;
			}
		}
		return true;
	}
	
	/*
	 * ********************************************************************************
	 */
	private String m_BeginDate;
	private String m_BeginEnd;
	private DateTimeProvider m_DateTimeProvider;
	
	private List<TimeTasksPair> m_mainTimeTaskList;
	private CWaitObject m__mainTimeTaskListWaitObj;
	
}
