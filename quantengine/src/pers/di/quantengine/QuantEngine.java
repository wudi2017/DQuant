package pers.di.quantengine;

import java.util.Date;
import java.util.List;

import pers.di.common.*;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.StockDataEngine.*;
import pers.di.dataengine.webdata.CommonDef.*;

public class QuantEngine {
	/*
	 * 配置量化引擎
	 * 1可以历史回测与实时运行
	 * 2可以配置触发时间
	 * 
	 * key: "TrigerMode" 触发模式
	 *     value: "HistoryTest" 历史回测
	 *     value: "RealTime" 实时
	 * key: "HistoryTimeSpan"
	 *     value: "XXXX-XX-XX XXXX-XXXX-XX" 历史回测时间段
	 *   
	 */
	public int config(String key, String value)
	{
		return 0;
	}
	
	/*
	 * 进入运行状态，根据配置进行触发回调
	 */
	public int run(QuantTriger triger)
	{
		// 每天进行循环
		String dateStr = getStartDate();
		while(true) 
		{
			CLog.output("QEngine", "Date [%s] ##########################\n", dateStr);
			
			String timestr = "00:00:00";
			
			// 09:25确定是否是交易日
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
				// 09:27 账户新交易日初始化
				timestr = "09:27:00";
				waitForDateTime(dateStr, timestr);
				boolean bAccInit = false;
				CLog.output("CTRL", "[%s %s] account newDayInit = %b \n", dateStr, timestr, bAccInit);
				
				if(bAccInit)
				{
					// 9:30-11:30 1:00-3:00 定期间隔进行触发trigger
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

					// 19:00 更新历史数据
					timestr = "19:00:00";
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("CTRL", "[%s %s] updateStockData \n", dateStr, timestr);
					}
					
					// 20:30  选股
					timestr = "20:30:00";
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("CTRL", "[%s %s] StockSelectAnalysis \n", dateStr, timestr);
					}
					
					// 20:35 当日报告
					timestr = "20:35:00";
					if(waitForDateTime(dateStr, timestr))
					{
						CLog.output("CTRL", "[%s %s] daily report collection \n", dateStr, timestr);
					}
					
					// 20:40  账户当日交易结束
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
			
			// 获取下一日期
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
	 * realtime模式
	 * 	等待日期时间成功，返回true
	 * 	等待失败，返回false，比如等待的时间已经过期
	 * historymock模式
	 * 	直接返回true
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
	 * realtime模式
	 * 	一直等待到9:25返回是否是交易日，根据上证指数实时变化确定
	 * historymock模式
	 * 	根据上证指数直接确定是否是交易日
	 */
	private boolean isTranDate(String date)
	{
		if(m_bHistoryTest)
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
			
			for(int i = 0; i < 5; i++) // 试图5次来确认
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
	
	// 历史交易日
	private List<String> m_hisTranDate;
	// 当前日期
	private String m_curDate;
	
	// 基本参数
	private boolean m_bHistoryTest;
	private String m_beginDate;
	private String m_endDate;
}
