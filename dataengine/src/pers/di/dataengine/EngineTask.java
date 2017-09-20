package pers.di.dataengine;

public abstract class EngineTask {
	
	public EngineTask(String name)
	{
		m_taskName = name;
	}
	
	abstract public void run();
	
	public String getName()
	{
		return m_taskName;
	}
	
	private String m_taskName;
}
