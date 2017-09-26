package pers.di.dataengine;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.RealTimeInfo;
import pers.di.dataapi.common.StockUtils;
import pers.di.dataengine.EngineTaskSharedSession.ListenerCallback;
import pers.di.dataapi.StockDataApi;

public class EngineTaskTrandingDayCheck extends ScheduleTask
{

	public EngineTaskTrandingDayCheck(String time, StockDataEngine sde, EngineTaskSharedSession tss) {
		super("TrandingDayCheck", time, 16);
		m_hisTranDate = null;
		m_stockDataEngine = sde;
		m_taskSharedSession = tss;
	}
	public void initializeHistoryTranDate()
	{
		m_hisTranDate = new ArrayList<String>();
		CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
		int errKLineListSZZS = StockDataApi.instance().buildDayKLineListObserver(
				"999999", "2008-01-01", "2100-01-01", obsKLineListSZZS);
		int iB = StockUtils.indexDayKAfterDate(obsKLineListSZZS, m_taskSharedSession.beginDate(), true);
		int iE = StockUtils.indexDayKBeforeDate(obsKLineListSZZS, m_taskSharedSession.endDate(), true);
		
		for(int i = iB; i <= iE; i++)  
        {  
			KLine cStockDayShangZheng = obsKLineListSZZS.get(i);  
			String curDateStr = cStockDayShangZheng.date;
			m_hisTranDate.add(curDateStr);
        }
	}
	@Override
	public void doTask(String date, String time) {
		CLog.output("DataEngine", "(%s %s) TrandingDayCheck...", date, time);
		
		boolean bIsTranDate = false;

		if(m_taskSharedSession.bHistoryTest())
		{
			if(null == m_hisTranDate)
			{
				initializeHistoryTranDate();
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
				bIsTranDate = m_hisTranDate.contains(date);
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
					RealTimeInfo ctnRealTimeInfo = new RealTimeInfo();
					int errRealTimeInfo = StockDataApi.instance().loadRealTimeInfo("999999", ctnRealTimeInfo);
					if(0 == errRealTimeInfo)
					{
						if(ctnRealTimeInfo.date.compareTo(date) == 0)
						{
							bIsTranDate = true;
							break;
						}
					}
					CThread.msleep(1000);
				}
			}
		}
		
		m_taskSharedSession.setIsTranDate(bIsTranDate);
		CLog.output("DataEngine", "(%s %s) EngineTaskTrandingDayCheck bIsTranDate=%b", date, time, bIsTranDate);
		
		if(m_taskSharedSession.bIsTranDate())
		{
			//call listener: TRADINGDAYSTART
			List<ListenerCallback> lcbs = m_taskSharedSession.getLCBTranDayStart();
			for(int i=0; i<lcbs.size(); i++)
			{
				ListenerCallback lcb = lcbs.get(i);
				try {
					lcb.md.invoke(lcb.obj, new EngineEventContext(date, time), new EngineEventObject());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public List<String> m_hisTranDate;
	private StockDataEngine m_stockDataEngine;
	private EngineTaskSharedSession m_taskSharedSession;

}
