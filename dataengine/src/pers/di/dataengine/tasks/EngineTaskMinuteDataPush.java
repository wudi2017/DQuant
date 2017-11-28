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
		CLog.output("DENGINE", "[%s %s] EngineTaskMinuteDataPush", date, time);
		
		//call listener
		List<ListenerCallback> lcbs = m_taskSharedSession.minuteTimePricesCbs;
		for(int i=0; i<lcbs.size(); i++)
		{
			ListenerCallback lcb = lcbs.get(i);
			
			// create event
			EETimePricesData ev = new EETimePricesData();
			DAContext cDAContext = m_taskSharedSession.listenerDataContext.get(lcb.listener);
			cDAContext.setDateTime(date, time);
			ev.ctx = cDAContext;
						
			try {
				lcb.md.invoke(lcb.obj, ev);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private SharedSession m_taskSharedSession;
}
