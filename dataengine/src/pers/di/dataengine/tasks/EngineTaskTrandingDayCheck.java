package pers.di.dataengine.tasks;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CDateTimeThruster;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.*;
import pers.di.localstock.LocalStock;
import pers.di.localstock.common.KLine;
import pers.di.localstock.common.RealTimeInfoLite;
import pers.di.localstock.common.StockUtils;

public class EngineTaskTrandingDayCheck extends CDateTimeThruster.ScheduleTask 
{

	public EngineTaskTrandingDayCheck(String time, StockDataEngine sde, SharedSession tss) {
		super("TrandingDayCheck", time, 16);
		m_stockDataEngine = sde;
		m_taskSharedSession = tss;
	}

	@Override
	public void doTask(String date, String time) {
		
		CLog.output("DENGINE", "[%s %s] EngineTaskTrandingDayCheck", date, time);
		
		boolean bIsTranDate = m_taskSharedSession.tranDayChecker.check(date, time);
		
		// first call onTradingDayStart if is tran date
		if(bIsTranDate)
		{
			// callback listener onTradingDayStart
			for(int i=0; i<m_taskSharedSession.listeners.size(); i++)
			{
				IEngineListener listener = m_taskSharedSession.listeners.get(i);
				DAContext context = m_taskSharedSession.listenerContext.get(listener);
				context.setDateTime(date, time);
				listener.onTradingDayStart(context);
			}
		}
	}
	
	private StockDataEngine m_stockDataEngine;
	private SharedSession m_taskSharedSession;

}
