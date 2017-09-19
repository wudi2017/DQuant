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
	private static StockDataEngine s_instance = new StockDataEngine(); 
	private StockDataEngine ()
	{
		m_stockDataApi = StockDataApi.instance();
		m_configFailed = false;
		m_dataListener = null;

		m_curDate = "0000-00-00";
		m_context = new DataContext();
		
		m_bHistoryTest = false;
		m_beginDate = "0000-00-00";
		m_endDate = "0000-00-00";
		m_hisTranDate = new ArrayList<String>();
		
	}
	public static StockDataEngine instance() {  
		return s_instance;  
	} 
	
	/*
	 * ������������
	 * 
	 * key: "ListenMode" ����ģʽ
	 *     value: "HistoryTest XXXX-XX-XX XXXX-XXXX-XX" ��ʷ�ز�
	 *     value: "Realtime" ʵʱ
	 */
	public int config(String key, String value)
	{
		// init history or realtime
		if(0 == key.compareTo("ListenMode"))
		{
			if(value.contains("HistoryTest"))
			{
				String[] cols = value.split(" ");
				m_bHistoryTest = true;
				m_beginDate = cols[1];
				m_endDate = cols[2];
			
				// ��ʼ����ʷ�����ձ�
				if(CUtilsDateTime.CheckValidDate(m_beginDate) 
						&&CUtilsDateTime.CheckValidDate(m_endDate))
				{
					m_hisTranDate = new ArrayList<String>();
					CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
					int errKLineListSZZS = m_stockDataApi.buildDayKLineListObserver(
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
					CLog.error("DataEngine", "input parameter error!");
					m_configFailed = true;
				}
			}
			else if(value.contains("Realtime"))
			{
				m_bHistoryTest = false;
			}
			else
			{
				CLog.error("DataEngine", "input parameter error!");
				m_configFailed = true;
			}
		}
		return 0;
	}
	
	public int registerDataListener(DataListener dataListener)
	{
		m_dataListener = dataListener;
		return 0;
	}
	
	public EngineListener createListener()
	{
		return new EngineListener();
	}
	
	public int run()
	{
		if(m_configFailed)
		{
			return 0;
		}
		CLog.output("DataEngine", "The DataEngine is running now...");
		
		// ÿ�����ѭ��
		String dateStr = getStartDate();
		while(true) 
		{
			String timestr = "00:00:00";
			
			// 09:25ȷ���Ƿ��ǽ�����
			boolean bIsTranDate = false;
			timestr = "09:25:00";
			waitForDateTime(dateStr, timestr);
			if(isTranDate(dateStr))
			{
				bIsTranDate = true;
			}
			CLog.output("QEngine", "[%s %s] check market day = %b ", dateStr, timestr, bIsTranDate);
			
			if(bIsTranDate)
			{
				// 09:27 ����onDayBegin
				timestr = "09:27:00";
				waitForDateTime(dateStr, timestr);
				CLog.output("QEngine", "[%s %s] listener.onDayBegin ", dateStr, timestr);
				m_context.setDateTime(dateStr, timestr);
				m_dataListener.onDayBegin(m_context);
				
				// 9:30-11:30 1:00-3:00 ���ڼ�����д���trigger.onEveryMinute
				int interval_min = 1;
				String timestr_begin = "09:30:00";
				String timestr_end = "11:30:00";
				timestr = timestr_begin;
				while(true)
				{
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("QEngine", "[%s %s] listener.onTransactionEveryMinute ", dateStr, timestr);
						m_context.setDateTime(dateStr, timestr);
						m_dataListener.onTransactionEveryMinute(m_context);
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
						CLog.output("QEngine", "[%s %s] listener.onTransactionEveryMinute ", dateStr, timestr);
						m_context.setDateTime(dateStr, timestr);
						m_dataListener.onTransactionEveryMinute(m_context);
					}
					timestr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(timestr, interval_min*60);
					if(timestr.compareTo(timestr_end) > 0) break;
				}

				// 19:00 ������ʷ����
				timestr = "19:00:00";
				if(waitForDateTime(dateStr, timestr))
				{
					CLog.output("QEngine", "[%s %s] update market data ", dateStr, timestr);
					m_stockDataApi.updateAllLocalStocks(dateStr);
				}
				
				// 20:00 ����trigger.onDayEnd
				timestr = "21:00:00";
				waitForDateTime(dateStr, timestr);
				CLog.output("QEngine", "[%s %s] listener.onDayEnd ", dateStr, timestr);
				m_context.setDateTime(dateStr, timestr);
				m_dataListener.onDayEnd(m_context);
			}
			
			// ��ȡ��һ����
			dateStr = getNextDate();
			if(null == dateStr) break;
		}
				
		return 0;
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
		if(m_bHistoryTest)
		{
			return true;
		}
		else
		{
			CLog.output("DataEngine", "realtime waitting DateTime (%s %s)... ", date, time);
			boolean bWait = CUtilsDateTime.waitDateTime(date, time);
			CLog.output("DataEngine", "realtime waitting DateTime (%s %s) complete! result(%b)", date, time, bWait);
			return bWait;
		}
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
			m_stockDataApi.updateLocalStocks("999999", yesterdayDate);
			CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
			int errKLineListSZZS = m_stockDataApi.buildDayKLineListObserver(
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
				int errRealTimeInfo = m_stockDataApi.loadRealTimeInfo("999999", ctnRealTimeInfo);
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
	
	
	private StockDataApi m_stockDataApi;
	private boolean m_configFailed;
	private DataListener m_dataListener;
	
	private String m_curDate;
	private DataContext m_context;
	private boolean m_bHistoryTest;
	private String m_beginDate;
	private String m_endDate;
	private List<String> m_hisTranDate;
}
