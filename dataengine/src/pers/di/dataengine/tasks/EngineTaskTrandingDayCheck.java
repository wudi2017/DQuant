package pers.di.dataengine.tasks;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CDateTimeThruster;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.RealTimeInfo;
import pers.di.dataapi.common.StockUtils;
import pers.di.dataapi.StockDataApi;
import pers.di.dataengine.*;

public class EngineTaskTrandingDayCheck extends CDateTimeThruster.ScheduleTask 
{

	public EngineTaskTrandingDayCheck(String time, StockDataEngine sde, SharedSession tss) {
		super("TrandingDayCheck", time, 16);
		m_stockDataEngine = sde;
		m_taskSharedSession = tss;
	}

	@Override
	public void doTask(String date, String time) {
		
		boolean bIsTranDate = false;
		bIsTranDate = m_taskSharedSession.tranDayChecker.check(date);
		
		CLog.output("DATAENGINE", "(%s %s) EngineTaskTrandingDayCheck bIsTranDate=%b", date, time, bIsTranDate);
		
		if(bIsTranDate)
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
	}
	
	private StockDataEngine m_stockDataEngine;
	private SharedSession m_taskSharedSession;

}
