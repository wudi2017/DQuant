package pers.di.dataengine;

import java.util.*;

import pers.di.common.*;
import pers.di.dataengine.common.KLine;
import pers.di.dataengine.common.StockUtils;

/*
 * 计划任务控制器
 */
public class ScheduleTaskController {
	
	private static class TimeTasksPair
	{
		public TimeTasksPair()
		{
			time = "00:00:00";
			tasks = new LinkedList<ScheduleTask>();
		}
		public String time;
		public List<ScheduleTask> tasks;
	}
	
	public ScheduleTaskController()
	{
		m_bHistoryTest = false;
		m_beginDate = "0000-00-00";
		m_endDate = "0000-00-00";
		
		m_curDate = "0000-00-00";
		m_curTime = "00:00:00";
		
		m_mainTimeTaskList = new LinkedList<TimeTasksPair>();
		m_mainTimeTaskListWaitObj = new CWaitObject();
	}
	
	public boolean schedule(ScheduleTask cScheduleTask)
	{
		// find TimeTasksPair
		TimeTasksPair cCurTimeTasksPair = null;
		for(int i=0; i<m_mainTimeTaskList.size(); i++)
		{
			TimeTasksPair cTmpTimeTasksPair = m_mainTimeTaskList.get(i);
			if(cScheduleTask.getTime().compareTo(cTmpTimeTasksPair.time) > 0)
			{
				continue;
			}
			else if(cScheduleTask.getTime().compareTo(cTmpTimeTasksPair.time) == 0)
			{
				cCurTimeTasksPair = cTmpTimeTasksPair;
				break;
			}
			else
			{
				TimeTasksPair cNewTimeTasksPair = new TimeTasksPair();
				cNewTimeTasksPair.time = cScheduleTask.getTime();
				m_mainTimeTaskList.add(i, cNewTimeTasksPair);
				cCurTimeTasksPair = cNewTimeTasksPair;
				break;
			}
		}
		if(null == cCurTimeTasksPair)
		{
			TimeTasksPair cNewTimeTasksPair = new TimeTasksPair();
			cNewTimeTasksPair.time = cScheduleTask.getTime();
			m_mainTimeTaskList.add(cNewTimeTasksPair);
			cCurTimeTasksPair = cNewTimeTasksPair;
		}
		
		// insert task to list
		List<ScheduleTask> tasks = cCurTimeTasksPair.tasks;
		int iInsert = -1;
		for(int i=0; i<tasks.size(); i++)
		{
			ScheduleTask task = cCurTimeTasksPair.tasks.get(i);
			if(cScheduleTask.getPriority() >= task.getPriority())
			{
				continue;
			}
			else
			{
				iInsert = i;
				tasks.add(iInsert, cScheduleTask);
				break;
			}
		}
		if(-1 == iInsert)
		{
			tasks.add(cScheduleTask);
		}
		
		return true;
	}
	
	
	/*
	 * 配置时间轴模式
	 * 
	 * key: "TriggerMode" 触发模式
	 *     value: "HistoryTest XXXX-XX-XX XXXX-XXXX-XX" 历史回测
	 *     value: "Realtime" 实时
	 */
	public int config(String key, String value)
	{
		// init history or realtime
		if(0 == key.compareTo("TriggerMode"))
		{
			if(value.contains("HistoryTest"))
			{
				String[] cols = value.split(" ");
				m_bHistoryTest = true;
				m_beginDate = cols[1];
				m_endDate = cols[2];
			}
			else if(value.contains("Realtime"))
			{
				m_bHistoryTest = false;
			}
			else
			{
				CLog.error("DataEngine", "input parameter error!");
			}
		}
		return 0;
	}
	
	public boolean run()
	{
		m_curDate = getBeginDate();
		while(null != m_curDate) 
		{
			m_curTime = "00:00:00";
			CLog.output("DataEngine", "(%s %s) new day begin", m_curDate, m_curTime);
			
			TimeTasksPair timeTasksPair = getFirstTimeTasksPair();
			while(null != timeTasksPair)
			{
				CUtilsDateTime.WAITRESULT wr = waitForDateTime(m_curDate, timeTasksPair.time, m_mainTimeTaskListWaitObj);
					
				if(CUtilsDateTime.WAITRESULT.TIME_IS_UP == wr)
				{
					m_curTime = timeTasksPair.time;
					CLog.output("DataEngine", "(%s %s) doAllTask", m_curDate, m_curTime);
					doAllTask(timeTasksPair);
				}
				else
				{
					continue;
				}

				timeTasksPair = getNearestTimeTasksPair();
			}

			m_curDate = getNextDate();
		}
		return true;
	}
	
	private void doAllTask(TimeTasksPair cTimeTasksPair)
	{
		if(null != cTimeTasksPair && null != cTimeTasksPair.tasks)
		{
			for(int i=0; i<cTimeTasksPair.tasks.size(); i++)
			{
				ScheduleTask task = cTimeTasksPair.tasks.get(i);
				if(null != task)
				{
					task.doTask(m_curDate, m_curTime);
				}
			}
		}
	}
	
	private CUtilsDateTime.WAITRESULT waitForDateTime(String date, String time, CWaitObject watiObj)
	{
		if(m_bHistoryTest)
		{
			return CUtilsDateTime.WAITRESULT.TIME_IS_UP;
		}
		else
		{
			CLog.output("DataEngine", "realtime waitting DateTime (%s %s)... ", date, time);
			CUtilsDateTime.WAITRESULT eWait = CUtilsDateTime.waitFor(date, time, watiObj);
			CLog.output("DataEngine", "realtime waitting DateTime (%s %s) complete! result(%d)", date, time, eWait);
			return eWait;
		}
	}
	
	private TimeTasksPair getFirstTimeTasksPair()
	{
		TimeTasksPair cTimeTasksPair = null;
		if(m_mainTimeTaskList.size() > 0)
		{
			cTimeTasksPair = m_mainTimeTaskList.get(0);
		}
		return cTimeTasksPair;
	}
	
	private TimeTasksPair getNearestTimeTasksPair()
	{
		TimeTasksPair cTimeTasksPair = null;
		for(int i=0; i<m_mainTimeTaskList.size(); i++)
		{
			TimeTasksPair cTmpTimeTasksPair = m_mainTimeTaskList.get(i);
			if(cTmpTimeTasksPair.time.compareTo(m_curTime) > 0)
			{
				cTimeTasksPair = cTmpTimeTasksPair;
				break;
			}
		}
		return cTimeTasksPair;
	}
	
	private String getBeginDate()
	{
		if(m_bHistoryTest)
		{
			return m_beginDate;
		}
		else
		{
			return CUtilsDateTime.GetCurDateStr();
		}
	}
	private String getNextDate()
	{
		String nextDate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(m_curDate, 1);
		if(m_bHistoryTest)
		{
			if(nextDate.compareTo(m_endDate) > 0)
			{
				return null;
			}
			else
			{
				return nextDate;
			}
		}
		else
		{
			return nextDate;
		}
	}
	
	/*
	 * ********************************************************************************
	 */
	private boolean m_bHistoryTest;
	private String m_beginDate;
	private String m_endDate;
	
	private String m_curDate;
	private String m_curTime;
	
	private List<TimeTasksPair> m_mainTimeTaskList;
	private CWaitObject m_mainTimeTaskListWaitObj;
	
}
