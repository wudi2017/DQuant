package pers.di.dataengine.tasks;

import java.util.List;

import pers.di.common.CLog;
import pers.di.dataengine.taskcontroller.ScheduleTask;
import pers.di.dataengine.tasks.EngineTaskSharedSession.ListenerCallback;
import pers.di.dataengine.*;

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
		CLog.output("DataEngine", "(%s %s) EngineTaskDayFinish", date, time);
		//call listener
		List<ListenerCallback> lcbs = m_taskSharedSession.getLCBTranDayFinish();
		for(int i=0; i<lcbs.size(); i++)
		{
			ListenerCallback lcb = lcbs.get(i);
			try {
				lcb.md.invoke(lcb.obj, new EngineEventContext(date, time), new EngineEventObject());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private EngineTaskSharedSession m_taskSharedSession;
}