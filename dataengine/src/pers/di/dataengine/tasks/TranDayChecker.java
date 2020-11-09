package pers.di.dataengine.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.DAContext;
import pers.di.localstock.LocalStock;
import pers.di.localstock.common.KLine;
import pers.di.localstock.common.RealTimeInfoLite;
import pers.di.localstock.common.StockUtils;

public class TranDayChecker {
	
	public TranDayChecker(SharedSession taskSharedSession)
	{
		m_taskSharedSession =  taskSharedSession;
		m_hisTranDates = null;
		
		m_bIsTranDate = false;
		m_lastValidCheckDate = "0000-00-00";
	}

	public boolean check(String date, String time)
	{
		if(date.equals(m_lastValidCheckDate))
		{
			return m_bIsTranDate;
		}
		
		boolean bIsTranDate = false;
		
		if(m_taskSharedSession.bHistoryTest)
		{
			/*
			 * 如果没有交易日期表 或者 当前需要检查的日期超过了检查表中的最大日期 都需要重新初始化交易日期表.
			 * NOTE:
			 * 本地有上证数据K线，但History测试区间大于了上证数据K线最后一天情况：
			 * 第一次执行时候历史交易日期表初始化为和本地上证数据K线一致，随着测试进行，当超过上证指数最后一天时，
			 * 重新调用初始化方法，由于参数日期已经大于当前历史交易日期表最后一天，所以在初始化中进行本地数据强制更新。
			 * 
			 */
			if(null == m_hisTranDates || date.compareTo(m_hisTranMaxDate) > 0 )
			{
				CLog.info("DENGINE", "[%s %s] TranDayChecker.check initializeHistoryTranDate (m_hisTranDates=NULL or checkDate > hisTranMaxDate)", date, time);
				initializeHistoryTranDate(date);
			}
			
			// 数据错误排除,经过测试 次日期内无法从网络获取数据
			if(
				date.equals("2013-03-08")
				|| date.equals("2015-06-09")
				|| date.equals("2016-10-17")
				|| date.equals("2016-11-25")
				)
			{
				bIsTranDate = false;
			}
			else
			{
				bIsTranDate = m_hisTranDates.contains(date);
			}
		}
		else
		{
			// 确认今天是否是交易日
			String yesterdayDate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(date, -1);
			LocalStock.instance().updateLocalStocks("999999", yesterdayDate);
			CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
			int errKLineListSZZS = LocalStock.instance().buildDayKLineListObserver(
					"999999", "2000-01-01", "2100-01-01", obsKLineListSZZS);
			for(int i = 0; i < obsKLineListSZZS.size(); i++)  
	        {  
				KLine cStockDayShangZheng = obsKLineListSZZS.get(i);  
				String checkDateStr = cStockDayShangZheng.date;
				if(checkDateStr.equals(date))
				{
					bIsTranDate = true;
					break;
				}
	        }
			
			if(false == bIsTranDate)
			{
				for(int i = 0; i < 5; i++) // 试图5次来确认
				{
					List<RealTimeInfoLite> ctnRealTimeInfos = new ArrayList<RealTimeInfoLite>();
					List<String> stockIDs = new ArrayList<String>();
					stockIDs.add("999999");
					int errRealTimeInfo = LocalStock.instance().loadRealTimeInfo(stockIDs, ctnRealTimeInfos);
					if(0 == errRealTimeInfo)
					{
						if(ctnRealTimeInfos.get(0).date.compareTo(date) == 0)
						{
							bIsTranDate = true;
							break;
						}
					}
					CThread.msleep(1000);
				}
			}
		}
		
		m_bIsTranDate = bIsTranDate;
		m_lastValidCheckDate = date;
		CLog.debug("DENGINE", "[%s %s] TranDayChecker.check = %b", date, time, bIsTranDate);
		
		return bIsTranDate;
	}
	
	private void initializeHistoryTranDate(String date)
	{
		m_hisTranDates = new HashSet<String>();
		CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
		int errKLineListSZZS = LocalStock.instance().buildDayKLineListObserver(
				"999999", "1990-01-01", "2100-01-01", obsKLineListSZZS);
		
		if(0 != errKLineListSZZS)
		{
			LocalStock.instance().updateAllLocalStocks(CUtilsDateTime.GetCurDateStr());
		}
		else
		{
			if(obsKLineListSZZS.get(obsKLineListSZZS.size()-1).date.compareTo(date) < 0
					&& obsKLineListSZZS.get(obsKLineListSZZS.size()-1).date.compareTo(CUtilsDateTime.GetCurDateStr()) < 0)
			{
				CLog.info("DENGINE", "TranDayChecker  updateAllLocalStocks:%s", CUtilsDateTime.GetCurDateStr());
				LocalStock.instance().updateAllLocalStocks(CUtilsDateTime.GetCurDateStr());
			}
		}
		
//		int iB = StockUtils.indexDayKAfterDate(obsKLineListSZZS, m_taskSharedSession.beginDate, true);
//		int iE = StockUtils.indexDayKBeforeDate(obsKLineListSZZS, m_taskSharedSession.endDate, true);
		
		for(int i = 0; i < obsKLineListSZZS.size(); i++)  
        {  
			KLine cStockDayShangZheng = obsKLineListSZZS.get(i);  
			String curDateStr = cStockDayShangZheng.date;
			m_hisTranDates.add(curDateStr);
        }
		if (obsKLineListSZZS.size() > 0) {
			m_hisTranMaxDate = obsKLineListSZZS.get(obsKLineListSZZS.size()-1).date;
		}
		
	}
	
	private SharedSession m_taskSharedSession;
	private Set<String> m_hisTranDates;
	private String m_hisTranMaxDate;
	
	private boolean m_bIsTranDate;
	private String m_lastValidCheckDate;
	
}
