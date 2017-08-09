package pers.di.common;

abstract public class CModuleBase {
	
	abstract public void initialize();
	abstract public void start();
	abstract public void stop();
	abstract public void unInitialize();
	
	public CModuleBase(String moduleName)
	{
		m_moduleName = moduleName;
	}
	public String moduleName()
	{
		return m_moduleName;
	}
	private String m_moduleName;
}
