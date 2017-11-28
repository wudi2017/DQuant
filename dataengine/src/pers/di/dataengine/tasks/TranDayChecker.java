package pers.di.dataengine.tasks;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataapi.StockDataApi;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.RealTimeInfo;
import pers.di.dataapi.common.StockUtils;
import pers.di.dataengine.DAContext;
import pers.di.dataengine.EETradingDayStart;

public class TranDayChecker {
	
	public TranDayChecker(SharedSession taskSharedSession)
	{
		m_taskSharedSession =  taskSharedSession;
		m_hisTranDate = null;
		
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
			if(null == m_hisTranDate)
			{
				initializeHistoryTranDate();
			}
			
			// ���ݴ����ų�,�������� ���������޷��������ȡ����
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
			// ȷ�Ͻ����Ƿ��ǽ�����
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
				for(int i = 0; i < 5; i++) // ��ͼ5����ȷ��
				{
					List<RealTimeInfo> ctnRealTimeInfos = new ArrayList<RealTimeInfo>();
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
		
		// first call onStart
		CLog.output("DENGINE", "[%s %s] TranDayChecker.check = %b", date, time, bIsTranDate);
		if(m_bIsTranDate)
		{
			//call listener: TRADINGDAYSTART
			List<ListenerCallback> lcbs = m_taskSharedSession.tranDayStartCbs;
			for(int i=0; i<lcbs.size(); i++)
			{
				ListenerCallback lcb = lcbs.get(i);
				
				// create event
				EETradingDayStart ev = new EETradingDayStart();
				DAContext cDAContext = m_taskSharedSession.listenerDataContext.get(lcb.listener);
				cDAContext.setDateTime(date, time);
				ev.ctx = cDAContext;
				
				try {
					lcb.md.invoke(lcb.obj, ev);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return bIsTranDate;
	}
	
	private void initializeHistoryTranDate()
	{
		m_hisTranDate = new ArrayList<String>();
		CListObserver<KLine> obsKLineListSZZS = new CListObserver<KLine>();
		int errKLineListSZZS = StockDataApi.instance().buildDayKLineListObserver(
				"999999", "2008-01-01", "2100-01-01", obsKLineListSZZS);
		if(0 != errKLineListSZZS)
		{
			StockDataApi.instance().updateAllLocalStocks(CUtilsDateTime.GetCurDateStr());
		}
		
		int iB = StockUtils.indexDayKAfterDate(obsKLineListSZZS, m_taskSharedSession.beginDate, true);
		int iE = StockUtils.indexDayKBeforeDate(obsKLineListSZZS, m_taskSharedSession.endDate, true);
		
		for(int i = iB; i <= iE; i++)  
        {  
			KLine cStockDayShangZheng = obsKLineListSZZS.get(i);  
			String curDateStr = cStockDayShangZheng.date;
			m_hisTranDate.add(curDateStr);
        }
	}
	
	private SharedSession m_taskSharedSession;
	private List<String> m_hisTranDate;
	
	private boolean m_bIsTranDate;
	private String m_lastValidCheckDate;
	
}