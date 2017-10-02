package pers.di.dataengine;

import java.util.List;

public class EngineListener {
	
	public EngineListener(StockDataEngine sde)
	{
		m_stockDataEngine = sde;
	}
	
	// callback 
	// void callback(EEObject ev)
	public void subscribe(EEID eID, Object obj, String methodname)
	{
		if(null != m_stockDataEngine)
		{
			m_stockDataEngine.subscribe(this, eID, obj, methodname);
		}
	}
	
	// ������Զ�ʧЧ
	public void addCurrentDayInterestMinuteDataID(String dataID)
	{
		m_stockDataEngine.addCurrentDayInterestMinuteDataID(this, dataID);
	}
	
	private StockDataEngine m_stockDataEngine;
}
