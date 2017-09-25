package pers.di.dataengine;

import pers.di.common.CLog;

public class EngineTaskDayFinish extends ScheduleTask 
{
	public EngineTaskDayFinish(String time, EngineTaskSharedSession tss) {
		super("DayFinish", time, 16);
		m_taskSharedSession = tss;
	}
	@Override
	public void doTask(String date, String time) {
		if(!m_taskSharedSession.bIsTranDate())
		{
			return;
		}
		CLog.output("DataEngine", "DayFinish");
	}
	private EngineTaskSharedSession m_taskSharedSession;
}