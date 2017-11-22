package pers.di.dataengine.tasks;

import pers.di.common.*;
import pers.di.dataapi.StockDataApi;
import pers.di.dataengine.*;

public class EngineTaskAllDataUpdate extends CDateTimeThruster.ScheduleTask
{
	public EngineTaskAllDataUpdate(String time, SharedSession tss) {
		super("AllDataUpdate", time, 16);
		m_taskSharedSession = tss;
	}
	@Override
	public void doTask(String date, String time) {
		if(!m_taskSharedSession.tranDayChecker.check(date))
		{
			return;
		}
		CLog.output("DataEngine", "(%s %s) AllDataUpdate...", date, time);
		StockDataApi.instance().updateAllLocalStocks(date);
		CLog.output("DataEngine", "(%s %s) AllDataUpdate Success", date, time);
	}
	private SharedSession m_taskSharedSession;
}
