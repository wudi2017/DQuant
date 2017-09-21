package pers.di.dataengine;

public abstract class ScheduleTask {
	
	abstract public void doTask(String date, String time);
	
	
	/**
	 * 
	 * @param name �ƻ���������
	 * @param datetime �ƻ�����ִ��ʱ��
	 *        08:20:30
	 * @param priority �ƻ�����ִ�����ȼ� -128~127 ԼСԼ�� ͬһʱ�䴥��ʱ�����ȼ�ִ��
	 *             
	 */
	public ScheduleTask(String name, String time, byte priority)
	{
		m_taskName = name;
		m_time = time;
		m_priority = priority;
	}
	
	public ScheduleTask(String name, String time)
	{
		m_taskName = name;
		m_time = time;
		m_priority = 127;
	}
	
	public String getName()
	{
		return m_taskName;
	}
	public String getTime()
	{
		return m_time;
	}
	public byte getPriority()
	{
		return m_priority;
	}
	
	private String m_taskName;
	private String m_time;
	private byte m_priority;
}