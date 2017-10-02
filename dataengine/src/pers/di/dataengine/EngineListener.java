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
	
	// 隔天后自动失效
	public void addCurrentDayInterestMinuteDataID(String dataID)
	{
		m_stockDataEngine.addCurrentDayInterestMinuteDataID(this, dataID);
	}
	public void addCurrentDayInterestMinuteDataIDs(List<String> dataIDs)
	{
		for(int i=0; i<dataIDs.size();i++)
		{
			m_stockDataEngine.addCurrentDayInterestMinuteDataID(this, dataIDs.get(i));
		}
	}
	
	private StockDataEngine m_stockDataEngine;
}
