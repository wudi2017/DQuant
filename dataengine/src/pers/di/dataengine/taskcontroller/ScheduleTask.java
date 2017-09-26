package pers.di.dataengine.taskcontroller;

public abstract class ScheduleTask {
	
	abstract public void doTask(String date, String time);
	
	
	/**
	 * 
	 * @param name 计划任务名称
	 * @param datetime 计划任务执行时间
	 *        08:20:30
	 * @param priority 计划任务执行优先级  约小约高 同一时间触发时按优先级执行
	 *             
	 */
	public ScheduleTask(String name, String time, int priority)
	{
		m_taskName = name;
		m_time = time;
		m_priority = priority;
	}
	
	public ScheduleTask(String name, String time)
	{
		m_taskName = name;
		m_time = time;
		m_priority = 256;
	}
	
	public String getName()
	{
		return m_taskName;
	}
	public String getTime()
	{
		return m_time;
	}
	public int getPriority()
	{
		return m_priority;
	}
	
	private String m_taskName;
	private String m_time;
	private int m_priority;
}