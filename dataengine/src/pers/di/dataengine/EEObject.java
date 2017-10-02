package pers.di.dataengine;

public class EEObject {
	
	public EEObject(EEID eID)
	{
		m_eID = eID;
	}
	
	public EEID eID()
	{
		return m_eID;
	}
	
	private EEID m_eID;
}
