package pers.di.dataengine.taskcontroller;

import java.util.*;

import pers.di.common.*;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.StockUtils;

/*
 * �ƻ����������
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
		
		CLog.output("DataEngine", "ScheduleTaskController.schedule Task(%s %s %d)"
				, cScheduleTask.getName(), cScheduleTask.getTime(), cScheduleTask.getPriority());
		
		// notify
		m_mainTimeTaskListWaitObj.Notify();
		
		return true;
	}
	
	
	/*
	 * ����ʱ����ģʽ
	 * 
	 * key: "TriggerMode" ����ģʽ
	 *     value: "HistoryTest XXXX-XX-XX XXXX-XXXX-XX" ��ʷ�ز�
	 *     value: "Realtime" ʵʱ
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
			CLog.output("DataEngine", "(%s %s) <<<<<< ------ new day begin ------ >>>>>>", m_curDate, m_curTime);
			
			TimeTasksPair timeTasksPair = getFirstTimeTasksPair();
			String waitToTime = ((null!=timeTasksPair)?timeTasksPair.time:"23:59:50");
			while(waitToTime.compareTo("23:59:00") <= 0)
			{
				CUtilsDateTime.WAITRESULT wr = waitForDateTime(m_curDate, waitToTime, m_mainTimeTaskListWaitObj);
				
				// ��������ж�
				if(waitToTime.compareTo("23:59:00") >= 0
						&& (wr == CUtilsDateTime.WAITRESULT.TIME_IS_UP || wr == CUtilsDateTime.WAITRESULT.TIME_HAS_GONE))
				{
					break; // ����������Ƚ���
				}
				
				// ����ʱ
				if(CUtilsDateTime.WAITRESULT.TIME_IS_UP == wr)
				{
					m_curTime = waitToTime;
					//CLog.output("DataEngine", "(%s %s) doAllTask", m_curDate, m_curTime);
					
					// do all task begin
					if(null != timeTasksPair && null != timeTasksPair.tasks)
					{
						for(int i=0; i<timeTasksPair.tasks.size(); i++)
						{
							ScheduleTask task = timeTasksPair.tasks.get(i);
							if(null != task)
							{
								task.doTask(m_curDate, m_curTime);
							}
						}
					}
					// do all task end
				}
				else if(CUtilsDateTime.WAITRESULT.TIME_HAS_GONE == wr)
				{
					m_curTime = waitToTime;
				}

				timeTasksPair = getNearestTimeTasksPair(m_curTime);
				waitToTime = ((null!=timeTasksPair)?timeTasksPair.time:"23:59:00");
			}

			m_curDate = getNextDate();
		}
		return true;
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
			CLog.output("DataEngine", "realtime waitting DateTime (%s %s) complete! result(%s)", date, time, eWait.toString());
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
	
	private TimeTasksPair getNearestTimeTasksPair(String curTime)
	{
		TimeTasksPair cTimeTasksPair = null;
		for(int i=0; i<m_mainTimeTaskList.size(); i++)
		{
			TimeTasksPair cTmpTimeTasksPair = m_mainTimeTaskList.get(i);
			if(cTmpTimeTasksPair.time.compareTo(curTime) > 0)
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
