package pers.di.dataengine;

import java.util.List;

public class EngineListener {
	
	public EngineListener(StockDataEngine sde)
	{
		m_stockDataEngine = sde;
	}
	
	// callback 
	// void callback(EngineEventContext ctx, EngineEvent ev)
	public void subscribe(ENGINEEVENTID ID, Object obj, String methodname)
	{
		if(null != m_stockDataEngine)
		{
			m_stockDataEngine.subscribe(this, ID, obj, methodname);
		}
	}
	
	// 隔天后自动失效
	public void setInterestMinuteDataID(List<String> stockIDs)
	{
		if(null != m_stockDataEngine)
		{
			m_stockDataEngine.setInterestMinuteDataID(this, stockIDs);
		}
	}
	
	private StockDataEngine m_stockDataEngine;
}
