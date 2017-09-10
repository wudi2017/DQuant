package pers.di.quantengine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pers.di.common.*;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.StockDataEngine.*;
import pers.di.dataengine.common.*;
import pers.di.quantengine.dataaccessor.*;

public class QuantEngine {
	/*
	 * ������������
	 * 
	 * key: "TrigerMode" ����ģʽ
	 *     value: "HistoryTest XXXX-XX-XX XXXX-XXXX-XX" ��ʷ�ز�
	 *     value: "RealTime" ʵʱ
	 *     
	 * key: "TrigerPoint"
	 *     value: "EveryMinute" ÿ���Ӵ���
	 *     value: "XX:XX" ����ʱ���ֶ�����  ���� 08:20
	 *   
	 */
	public int config(String key, String value)
	{
		if(0 == key.compareTo("TrigerMode"))
		{
			if(value.contains("HistoryTest"))
			{
				String[] cols = value.split(" ");
				m_bHistoryTest = true;
				m_beginDate = cols[1];
				m_endDate = cols[2];
				
				// ��ʼ����ʷ�����ձ�
				if(m_bHistoryTest)
				{
					m_hisTranDate = new ArrayList<String>();
					CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
					int errKLineListSZZS = StockDataEngine.instance().buildDayKLineListObserver(
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
			}
			else
			{
				m_bHistoryTest = false;
				// ʵʱ����
				m_realtimeCache = new RealtimeCache();
			}
		}
		else if(0 == key.compareTo("TrigerPoint"))
		{
			
		}
		
		return 0;
	}
	
	/*
	 * ��������״̬���������ý��д����ص�
	 */
	public int run(QuantTriger triger)
	{
		// ÿ�����ѭ��
		String dateStr = getStartDate();
		while(true) 
		{
			CLog.output("QEngine", "Date [%s] ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n", dateStr);
			
			String timestr = "00:00:00";
			
			// 09:25ȷ���Ƿ��ǽ�����
			boolean bIsTranDate = false;
			timestr = "09:25:00";
			waitForDateTime(dateStr, timestr);
			if(isTranDate(dateStr))
			{
				bIsTranDate = true;
			}
			CLog.output("QEngine", "[%s %s] isTranDate = %b \n", dateStr, timestr, bIsTranDate);
			
			
			if(bIsTranDate)
			{
				// 09:27 �˻��½����ճ�ʼ��
				timestr = "09:27:00";
				waitForDateTime(dateStr, timestr);
				boolean bAccInit = false;
				bAccInit = true;
				CLog.output("QEngine", "[%s %s] account newDayInit = %b \n", dateStr, timestr, bAccInit);
				
				if(bAccInit)
				{
					// 9:30-11:30 1:00-3:00 ���ڼ�����д���trigger
					int interval_min = 1;
					String timestr_begin = "09:30:00";
					String timestr_end = "11:30:00";
					timestr = timestr_begin;
					while(true)
					{
						if(waitForDateTime(dateStr, timestr))
						{
							CLog.output("QEngine", "[%s %s] triger.onHandler \n", dateStr, timestr);
							QuantContext ctx = new QuantContext();
							ctx.date = dateStr;
							ctx.time = timestr;
							ctx.pool = new DAPool(dateStr, timestr, m_realtimeCache);
							triger.onHandler(ctx);
						}
						timestr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(timestr, interval_min);
						if(timestr.compareTo(timestr_end) > 0) break;
					}
					
					timestr_begin = "13:00:00";
					timestr_end = "15:00:00";
					timestr = timestr_begin;
					while(true)
					{
						if(waitForDateTime(dateStr, timestr))
						{
							CLog.output("QEngine", "[%s %s] triger.onHandler \n", dateStr, timestr);
							QuantContext ctx = new QuantContext();
							ctx.date = dateStr;
							ctx.time = timestr;
							ctx.pool = new DAPool(dateStr, timestr, m_realtimeCache);
							triger.onHandler(ctx);
						}
						timestr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(timestr, interval_min);
						if(timestr.compareTo(timestr_end) > 0) break;
					}

					// 19:00 ������ʷ����
					timestr = "19:00:00";
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("QEngine", "[%s %s] updateStockData \n", dateStr, timestr);
					}
					
					// 20:30  ѡ��
					timestr = "20:30:00";
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("QEngine", "[%s %s] StockSelectAnalysis \n", dateStr, timestr);
					}
					
					// 20:35 ���ձ���
					timestr = "20:35:00";
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("QEngine", "[%s %s] daily report collection \n", dateStr, timestr);
					}
					
					// 20:40  �˻����ս��׽���
					timestr = "20:40:00";
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("QEngine", "[%s %s] account newDayTranEnd\n", dateStr, timestr);
					}
				}
				else
				{
					CLog.output("QEngine", "[%s %s] account newDayInit failed, continue! \n", dateStr, timestr);
				}
			}
			else
			{
				CLog.output("QEngine", "[%s %s] Not transaction date, continue! \n", dateStr, timestr);
			}
			
			// ��ȡ��һ����
			dateStr = getNextDate();
			
			// ���촦�����
			if(null !=m_realtimeCache)
				m_realtimeCache.clear(); // ���ʵʱ����
			
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
			String curDateStr = CUtilsDateTime.GetDateStr(new Date());
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
			CLog.output("QEngine", "realtime waitting DateTime (%s %s)... \n", date, time);
			boolean bWait = CUtilsDateTime.waitDateTime(date, time);
			CLog.output("QEngine", "realtime waitting DateTime (%s %s) complete! result(%b)\n", date, time, bWait);
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
			
			for(int i = 0; i < 5; i++) // ��ͼ5����ȷ��
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
				CThread.sleep(1000);
			}
			return false;
		}
	}
	
	//-----------------------------------------------------
	// ������
	// ��ǰ����
	private String m_curDate;
	
	//-----------------------------------------------------
	// ��ʷ������
	private List<String> m_hisTranDate;
	
	//-----------------------------------------------------
	// ʵʱ���ݻ���
	private RealtimeCache m_realtimeCache;
	
	//-----------------------------------------------------
	// ��������
	private boolean m_bHistoryTest;
	private String m_beginDate;
	private String m_endDate;
}
