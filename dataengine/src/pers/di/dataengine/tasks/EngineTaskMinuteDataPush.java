package pers.di.dataengine.tasks;

import pers.di.common.*;
import pers.di.dataengine.*;

public class EngineTaskMinuteDataPush extends CScheduleTaskController.ScheduleTask 
{
	public EngineTaskMinuteDataPush(String time, EngineTaskSharedSession tss) {
		super("MinuteDataPush", time, 16);
		m_taskSharedSession = tss;
	}
	@Override
	public void doTask(String date, String time) {
		if(!m_taskSharedSession.bIsTranDate)
		{
			return;
		}
		CLog.output("DataEngine", "(%s %s) EngineTaskMinuteDataPush", date, time);
	}
	private EngineTaskSharedSession m_taskSharedSession;
}
