package pers.di.dataengine;

import pers.di.common.CLog;
import pers.di.dataengine.baseapi.StockDataApi;

public class EngineTaskAllDataUpdate extends ScheduleTask
{
	public EngineTaskAllDataUpdate(String time, EngineTaskSharedSession tss) {
		super("AllDataUpdate", time, 16);
		m_taskSharedSession = tss;
	}
	@Override
	public void doTask(String date, String time) {
		if(!m_taskSharedSession.bIsTranDate())
		{
			return;
		}
		CLog.output("DataEngine", "(%s %s) AllDataUpdate...", date, time);
		StockDataApi.instance().updateAllLocalStocks(date);
		CLog.output("DataEngine", "(%s %s) AllDataUpdate Success", date, time);
	}
	private EngineTaskSharedSession m_taskSharedSession;
}
