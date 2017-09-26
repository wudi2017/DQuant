package pers.di.dataengine.taskcontroller;

public abstract class ScheduleTask {
	
	abstract public void doTask(String date, String time);
	
	
	/**
	 * 
	 * @param name �ƻ���������
	 * @param datetime �ƻ�����ִ��ʱ��
	 *        08:20:30
	 * @param priority �ƻ�����ִ�����ȼ�  ԼСԼ�� ͬһʱ�䴥��ʱ�����ȼ�ִ��
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