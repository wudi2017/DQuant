package pers.di.dataengine.tasks;

import java.util.List;

import pers.di.common.*;
import pers.di.dataengine.*;

public class EngineTaskMinuteDataPush extends CDateTimeThruster.ScheduleTask 
{
	public EngineTaskMinuteDataPush(String time, SharedSession tss) {
		super("MinuteDataPush", time, 16);
		m_taskSharedSession = tss;
	}
	@Override
	public void doTask(String date, String time) {
		if(!m_taskSharedSession.tranDayChecker.check(date, time))
		{
			return;
		}
		CLog.debug("DENGINE", "[%s %s] EngineTaskMinuteDataPush", date, time);
		
		// callback listener onMinuteTimePrices
		for(int i=0; i<m_taskSharedSession.listeners.size(); i++)
		{
			IEngineListener listener = m_taskSharedSession.listeners.get(i);
			DAContext context = m_taskSharedSession.listenerContext.get(listener);
			context.setDateTime(date, time);
			listener.onMinuteTimePrices(context);
		}
	}
	private SharedSession m_taskSharedSession;
}
