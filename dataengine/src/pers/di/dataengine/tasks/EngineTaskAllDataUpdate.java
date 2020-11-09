package pers.di.dataengine.tasks;

import pers.di.common.*;
import pers.di.dataengine.*;
import pers.di.localstock.LocalStock;

public class EngineTaskAllDataUpdate extends CDateTimeThruster.ScheduleTask
{
	public EngineTaskAllDataUpdate(String time, SharedSession tss) {
		super("AllDataUpdate", time, 16);
		m_taskSharedSession = tss;
	}
	@Override
	public void doTask(String date, String time) {
		if(!m_taskSharedSession.tranDayChecker.check(date, time))
		{
			return;
		}
		CLog.info("DENGINE", "[%s %s] updateAllLocalStocks...", date, time);
		LocalStock.instance().updateAllLocalStocks(date);
	}
	private SharedSession m_taskSharedSession;
}
