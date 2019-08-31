package pers.di.dataengine.tasks;

import java.util.List;

import pers.di.common.*;
import pers.di.dataengine.*;

public class EngineTaskDayFinish extends CDateTimeThruster.ScheduleTask
{
	public EngineTaskDayFinish(String time, SharedSession tss) {
		super("DayFinish", time, 16);
		m_taskSharedSession = tss;
	}
	@Override
	public void doTask(String date, String time) {
		if(!m_taskSharedSession.tranDayChecker.check(date, time))
		{
			return;
		}
		CLog.output("DENGINE", "[%s %s] EngineTaskDayFinish", date, time);
		
		// callback listener onMinuteTimePrices
		for(int i=0; i<m_taskSharedSession.listeners.size(); i++)
		{
			IEngineListener listener = m_taskSharedSession.listeners.get(i);
			DAContext context = m_taskSharedSession.listenerContext.get(listener);
			context.setDateTime(date, time);
			listener.onTradingDayFinish(context);
			
			// clearCurrentDayInterestMinuteDataCache
			context.clearCurrentDayInterestMinuteDataCache();
		}
	}
	private SharedSession m_taskSharedSession;
}