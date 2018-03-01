package pers.di.quantplatform;

import java.util.*;

import pers.di.dataengine.EngineListener;

public abstract class QuantStrategy {
	
	public abstract void onInit(QuantContext ctx);
	
	public abstract void onUnInit(QuantContext ctx);
	
	public abstract void onDayStart(QuantContext ctx);
	
	public abstract void onMinuteData(QuantContext ctx);
	
	public abstract void onDayFinish(QuantContext ctx);
	
	
	public QuantStrategy()
	{
	}
	
	public void setListener(EngineListener listener)
	{
		m_listener = listener;
	}
	
	public final boolean addCurrentDayInterestMinuteDataID(String ID)
	{
		m_listener.addCurrentDayInterestMinuteDataID(ID);
		return true;
	}
	public final boolean addCurrentDayInterestMinuteDataIDs(List<String> IDs)
	{
		m_listener.addCurrentDayInterestMinuteDataIDs(IDs);
		return true;
	}
	public final boolean removeCurrentDayInterestMinuteDataID(String ID)
	{
		m_listener.removeCurrentDayInterestMinuteDataID(ID);
		return true;
	}
	public final List<String> getCurrentDayInterestMinuteDataIDs()
	{
		return m_listener.getCurrentDayInterestMinuteDataIDs();
	}
	
	private EngineListener m_listener;
}
