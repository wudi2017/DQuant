package pers.di.dataengine.tasks;

import java.util.List;

import pers.di.common.*;
import pers.di.dataengine.*;

public class EngineTaskDayFinish extends CScheduleTaskController.ScheduleTask
{
	public EngineTaskDayFinish(String time, SharedSession tss) {
		super("DayFinish", time, 16);
		m_taskSharedSession = tss;
	}
	@Override
	public void doTask(String date, String time) {
		if(!m_taskSharedSession.bIsTranDate)
		{
			return;
		}
		CLog.output("DataEngine", "(%s %s) EngineTaskDayFinish", date, time);
		
		// create event
		EE_TradingDayFinish ev = new EE_TradingDayFinish();
		ev.setDate(date);
		ev.setTime(time);
		m_taskSharedSession.dACtx.setDateTime(date, time);
		ev.ctx = m_taskSharedSession.dACtx;
		
		//call listener
		List<ListenerCallback> lcbs = m_taskSharedSession.tranDayFinishCbs;
		for(int i=0; i<lcbs.size(); i++)
		{
			ListenerCallback lcb = lcbs.get(i);
			try {
				lcb.md.invoke(lcb.obj, ev);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private SharedSession m_taskSharedSession;
}