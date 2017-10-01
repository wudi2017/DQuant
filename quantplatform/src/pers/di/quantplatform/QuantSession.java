package pers.di.quantplatform;
import java.util.*;

import pers.di.accountengine.*;
import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataapi.StockDataApi;
import pers.di.dataapi.common.*;
import pers.di.quantplatform.accountproxy.*;

public class QuantSession {
	
	public QuantSession(String triggerCfgStr, Account accout, QuantStrategy strategy)
	{
		// init triggerCfgStr
		if(triggerCfgStr.contains("HistoryTest"))
		{
			String[] cols = triggerCfgStr.split(" ");
			m_bHistoryTest = true;
			m_beginDate = cols[1];
			m_endDate = cols[2];
		
			// ��ʼ����ʷ�����ձ�
			if(CUtilsDateTime.CheckValidDate(m_beginDate) 
					&&CUtilsDateTime.CheckValidDate(m_endDate))
			{
				m_hisTranDate = new ArrayList<String>();
				CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
				int errKLineListSZZS = StockDataApi.instance().buildDayKLineListObserver(
						"999999", "2008-01-01", "2100-01-01", obsKLineListSZZS);
				int iB = StockUtils.indexDayKAfterDate(obsKLineListSZZS, m_beginDate, true);
				int iE = StockUtils.indexDayKBeforeDate(obsKLineListSZZS, m_endDate, true);
				
				for(int i = iB; i <= iE; i++)  
		        {  
					KLine cStockDayShangZheng = obsKLineListSZZS.get(i);  
					String curDateStr = cStockDayShangZheng.date;
					m_hisTranDate.add(curDateStr);
		        }
			}
			else
			{
				CLog.error("QEngine", "input parameter error!");
			}
		}
		else
		{
			m_bHistoryTest = false;
		}
		
		// init m_accountProxy
		m_accountProxy = new AccountProxy(accout);
		
		// init m_stratety
		m_stratety = strategy;
		
		// init default object
		if(null == m_context)
		{
			m_context = new QuantContext(m_accountProxy);
		}

		m_startFlag = true;
	}
	
	public boolean run()
	{
		if(!m_startFlag) return false;
		
		CLog.output("QuantSession", "The QuantSession is running now...");
		
		
		String dateStr = getStartDate();
		String timestr = "00:00:00";
		m_context.setDateTime(dateStr, timestr);
		if(null != m_stratety)
		{
			m_stratety.onInit(m_context);
		}
		
		// ÿ�����ѭ��
		while(true) 
		{
			// 09:25ȷ���Ƿ��ǽ�����
			boolean bIsTranDate = false;
			timestr = "09:25:00";
			waitForDateTime(dateStr, timestr);
			if(isTranDate(dateStr))
			{
				bIsTranDate = true;
			}
			CLog.output("QuantSession", "[%s %s] check market day = %b ", dateStr, timestr, bIsTranDate);
			
			if(bIsTranDate)
			{
				// 09:27 ����trigger.onDayBegin
				timestr = "09:27:00";
				waitForDateTime(dateStr, timestr);
				CLog.output("QEngine", "[%s %s] triger.onDayBegin ", dateStr, timestr);
				if(null != m_stratety)
				{
					m_context.setCurrentDayInterestMinuteDataIDs(m_stratety.getCurrentDayInterestMinuteDataIDs());
					m_context.setDateTime(dateStr, timestr);
					m_stratety.onDayStart(m_context);
				}

				// 9:30-11:30 1:00-3:00 ���ڼ�����д���trigger.onEveryMinute
				int interval_min = 1;
				String timestr_begin = "09:30:00";
				String timestr_end = "11:30:00";
				timestr = timestr_begin;
				while(true)
				{
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("QEngine", "[%s %s] triger.onEveryMinute ", dateStr, timestr);
						if(null != m_stratety)
						{
							m_context.setDateTime(dateStr, timestr);
							m_stratety.onMinuteData(m_context);
						}
					}
					timestr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(timestr, interval_min*60);
					if(timestr.compareTo(timestr_end) > 0) break;
				}
				
				timestr_begin = "13:00:00";
				timestr_end = "15:00:00";
				timestr = timestr_begin;
				while(true)
				{
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("QEngine", "[%s %s] triger.onEveryMinute ", dateStr, timestr);
						if(null != m_stratety)
						{
							m_context.setDateTime(dateStr, timestr);
							m_stratety.onMinuteData(m_context);
						}
					}
					timestr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(timestr, interval_min*60);
					if(timestr.compareTo(timestr_end) > 0) break;
				}

				// 19:00 ������ʷ����
				timestr = "19:00:00";
				if(waitForDateTime(dateStr, timestr))
				{
					CLog.output("QEngine", "[%s %s] update market data ", dateStr, timestr);
					StockDataApi.instance().updateAllLocalStocks(dateStr);
				}
				
				// 20:00 ����trigger.onDayEnd
				timestr = "21:00:00";
				waitForDateTime(dateStr, timestr);
				CLog.output("QEngine", "[%s %s] triger.onDayEnd ", dateStr, timestr);
				if(null != m_stratety)
				{
					m_context.setDateTime(dateStr, timestr);
					m_stratety.onDayFinish(m_context);
					m_context.clearCurrentDayInterestMinuteDataIDs();
				}
			}
			
			// ��ȡ��һ����
			dateStr = getNextDate();
			if(null == dateStr) break;
		}
		
		return true;
	}
	
	private String getStartDate()
	{
		if(m_bHistoryTest)
		{
			m_curDate = m_beginDate;
			return m_curDate;
		}
		else
		{
			String curDateStr = CUtilsDateTime.GetCurDateStr();
			m_curDate = curDateStr;
			return curDateStr;
		}
	}
	private String getNextDate()
	{
		m_curDate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(m_curDate, 1);
		if(m_bHistoryTest)
		{
			if(m_curDate.compareTo(m_endDate) > 0)
			{
				return null;
			}
			else
			{
				return m_curDate;
			}
		}
		else
		{
			return m_curDate;
		}
	}
	
	/*
	 * realtimeģʽ
	 * 	�ȴ�����ʱ��ɹ�������true
	 * 	�ȴ�ʧ�ܣ�����false������ȴ���ʱ���Ѿ�����
	 * historymockģʽ
	 * 	ֱ�ӷ���true
	 */
	private boolean waitForDateTime(String date, String time)
	{
		boolean bWait = false;
		if(m_bHistoryTest)
		{
			bWait = true;
		}
		else
		{
			CLog.output("QEngine", "realtime waitting DateTime (%s %s)... ", date, time);
			CUtilsDateTime.WAITRESULT bWaitResult = CUtilsDateTime.waitFor(date, time);
			if(bWaitResult == CUtilsDateTime.WAITRESULT.TIME_IS_UP)
			{
				bWait = true;
			}
			CLog.output("QEngine", "realtime waitting DateTime (%s %s) complete! result(%b)", date, time, bWait);
		}
		return bWait;
	}
	
	/*
	 * realtimeģʽ
	 * 	һֱ�ȴ���9:25�����Ƿ��ǽ����գ�������ָ֤��ʵʱ�仯ȷ��
	 * historymockģʽ
	 * 	������ָ֤��ֱ��ȷ���Ƿ��ǽ�����
	 */
	private boolean isTranDate(String date)
	{
		if(m_bHistoryTest)
		{
			// ���ݴ����ų�,�������� ���������޷��������ȡ����
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
			// ȷ�Ͻ����Ƿ��ǽ�����
			String yesterdayDate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(m_curDate, -1);
			StockDataApi.instance().updateLocalStocks("999999", yesterdayDate);
			CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
			int errKLineListSZZS = StockDataApi.instance().buildDayKLineListObserver(
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
			
			for(int i = 0; i < 5; i++) // ��ͼ5����ȷ��
			{
				RealTimeInfo ctnRealTimeInfo = new RealTimeInfo();
				int errRealTimeInfo = StockDataApi.instance().loadRealTimeInfo("999999", ctnRealTimeInfo);
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

	private String m_curDate;
	private QuantContext m_context;
	
	//-----------------------------------------------------
	// ��������
	private boolean m_bHistoryTest;
	private String m_beginDate;
	private String m_endDate;
	private List<String> m_hisTranDate;
	
	private AccountProxy m_accountProxy;
	
	private QuantStrategy m_stratety;
	
	private boolean m_startFlag;
}
