package pers.di.quantengine;

import java.util.Date;
import java.util.List;

import pers.di.common.*;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.StockDataEngine.*;
import pers.di.dataengine.webdata.CommonDef.*;

public class QuantEngine {
	/*
	 * ������������
	 * 1������ʷ�ز���ʵʱ����
	 * 2�������ô���ʱ��
	 * 
	 * key: "TrigerMode" ����ģʽ
	 *     value: "HistoryTest" ��ʷ�ز�
	 *     value: "RealTime" ʵʱ
	 * key: "HistoryTimeSpan"
	 *     value: "XXXX-XX-XX XXXX-XXXX-XX" ��ʷ�ز�ʱ���
	 *   
	 */
	public int config(String key, String value)
	{
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
			CLog.output("QEngine", "Date [%s] ##########################\n", dateStr);
			
			String timestr = "00:00:00";
			
			// 09:25ȷ���Ƿ��ǽ�����
			boolean bIsTranDate = false;
			timestr = "09:25:00";
			waitForDateTime(dateStr, timestr);
			if(isTranDate(dateStr))
			{
				bIsTranDate = true;
			}
			CLog.output("CTRL", "[%s %s] isTranDate = %b \n", dateStr, timestr, bIsTranDate);
			
			
			if(bIsTranDate)
			{
				// 09:27 �˻��½����ճ�ʼ��
				timestr = "09:27:00";
				waitForDateTime(dateStr, timestr);
				boolean bAccInit = false;
				CLog.output("CTRL", "[%s %s] account newDayInit = %b \n", dateStr, timestr, bAccInit);
				
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
							CLog.output("CTRL", "[%s %s] stockClearAnalysis & stockCreateAnalysis \n", dateStr, timestr);
							QuantContext ctx = new QuantContext();
							ctx.date = dateStr;
							ctx.time = timestr;
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
							CLog.output("CTRL", "[%s %s] stockClearAnalysis & stockCreateAnalysis \n", dateStr, timestr);
							QuantContext ctx = new QuantContext();
							ctx.date = dateStr;
							ctx.time = timestr;
							triger.onHandler(ctx);
						}
						timestr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(timestr, interval_min);
						if(timestr.compareTo(timestr_end) > 0) break;
					}

					// 19:00 ������ʷ����
					timestr = "19:00:00";
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("CTRL", "[%s %s] updateStockData \n", dateStr, timestr);
					}
					
					// 20:30  ѡ��
					timestr = "20:30:00";
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("CTRL", "[%s %s] StockSelectAnalysis \n", dateStr, timestr);
					}
					
					// 20:35 ���ձ���
					timestr = "20:35:00";
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("CTRL", "[%s %s] daily report collection \n", dateStr, timestr);
					}
					
					// 20:40  �˻����ս��׽���
					timestr = "20:40:00";
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("CTRL", "[%s %s] account newDayTranEnd\n", dateStr, timestr);
					}
				}
				else
				{
					CLog.output("CTRL", "[%s %s] account newDayInit failed, continue! \n", dateStr, timestr);
				}
			}
			else
			{
				CLog.output("CTRL", "[%s %s] Not transaction date, continue! \n", dateStr, timestr);
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
			CLog.output("CTRL", "realtime waitting DateTime (%s %s)... \n", date, time);
			boolean bWait = CUtilsDateTime.waitDateTime(date, time);
			CLog.output("CTRL", "realtime waitting DateTime (%s %s) complete! result(%b)\n", date, time, bWait);
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
			DEKLines cDEKLines = StockDataEngine.instance().getDayKLines("999999", "2000-01-01", "2100-01-01");
			for(int i = 0; i < cDEKLines.size(); i++)  
	        {  
				KLine cStockDayShangZheng = cDEKLines.get(i);  
				String checkDateStr = cStockDayShangZheng.date;
				if(checkDateStr.equals(date))
				{
					return true;
				}
	        }
			
			for(int i = 0; i < 5; i++) // ��ͼ5����ȷ��
			{
				ResultStockTime cResultStockTime = stockDataIF.getStockTime("999999", date, CUtilsDateTime.GetCurTimeStr());
				if(0 == cResultStockTime.error)
				{
					if(cResultStockTime.date.compareTo(date) == 0)
					{
						return true;
					}
				}
				BThread.sleep(3000);
			}
			return false;
		}
	}
	
	// ��ʷ������
	private List<String> m_hisTranDate;
	// ��ǰ����
	private String m_curDate;
	
	// ��������
	private boolean m_bHistoryTest;
	private String m_beginDate;
	private String m_endDate;
}
