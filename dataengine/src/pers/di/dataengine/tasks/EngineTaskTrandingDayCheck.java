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
		
		CLog.output("DENGINE", "[%s %s] EngineTaskTrandingDayCheck", date, time);
		
		m_taskSharedSession.tranDayChecker.check(date, time);
	}
	
	private StockDataEngine m_stockDataEngine;
	private SharedSession m_taskSharedSession;

}
