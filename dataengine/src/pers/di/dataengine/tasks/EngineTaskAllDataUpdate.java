package pers.di.dataengine.tasks;

import pers.di.common.*;
import pers.di.dataapi.StockDataApi;
import pers.di.dataengine.*;

public class EngineTaskAllDataUpdate extends CScheduleTaskController.ScheduleTask
{
	public EngineTaskAllDataUpdate(String time, EngineTaskSharedSession tss) {
		super("AllDataUpdate", time, 16);
		m_taskSharedSession = tss;
	}
	@Override
	public void doTask(String date, String time) {
		if(!m_taskSharedSession.bIsTranDate)
		{
			return;
		}
		CLog.output("DataEngine", "(%s %s) AllDataUpdate...", date, time);
		StockDataApi.instance().updateAllLocalStocks(date);
		CLog.output("DataEngine", "(%s %s) AllDataUpdate Success", date, time);
	}
	private EngineTaskSharedSession m_taskSharedSession;
}
