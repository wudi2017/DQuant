package pers.di.dataengine.tasks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataapi.StockDataApi;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.RealTimeInfoLite;
import pers.di.dataapi.common.StockUtils;
import pers.di.dataengine.DAContext;
import pers.di.dataengine.EETradingDayStart;

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
			if(null == m_hisTranDates)
			{
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
					int errRealTimeInfo = StockDataApi.instance().loadRealTimeInfo(stockIDs, ctnRealTimeInfos);
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
		CLog.output("DENGINE", "[%s %s] TranDayChecker.check = %b", date, time, bIsTranDate);
		
		return bIsTranDate;
	}
	
	private void initializeHistoryTranDate(String date)
	{
		m_hisTranDates = new HashSet<String>();
		CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
		int errKLineListSZZS = StockDataApi.instance().buildDayKLineListObserver(
				"999999", "2008-01-01", "2100-01-01", obsKLineListSZZS);
		if(0 != errKLineListSZZS)
		{
			StockDataApi.instance().updateAllLocalStocks(CUtilsDateTime.GetCurDateStr());
		}
		else
		{
			if(obsKLineListSZZS.get(obsKLineListSZZS.size()-1).date.compareTo(date) < 0
					&& obsKLineListSZZS.get(obsKLineListSZZS.size()-1).date.compareTo(CUtilsDateTime.GetCurDateStr()) < 0)
			{
				StockDataApi.instance().updateAllLocalStocks(CUtilsDateTime.GetCurDateStr());
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
	}
	
	private SharedSession m_taskSharedSession;
	private Set<String> m_hisTranDates;
	
	private boolean m_bIsTranDate;
	private String m_lastValidCheckDate;
	
}
