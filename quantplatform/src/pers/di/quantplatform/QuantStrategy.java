package pers.di.quantplatform;

import java.util.*;

public abstract class QuantStrategy {
	
	public abstract void onInit(QuantContext ctx);
	
	public abstract void onUnInit(QuantContext ctx);
	
	public abstract void onDayStart(QuantContext ctx);
	
	public abstract void onMinuteData(QuantContext ctx);
	
	public abstract void onDayFinish(QuantContext ctx);
	
	
	public QuantStrategy()
	{
		m_currentDayInterestMinuteDataIDs = new ArrayList<String>();
	}
	
	public final boolean addCurrentDayInterestMinuteDataID(String ID)
	{
		m_currentDayInterestMinuteDataIDs.add(ID);
		return true;
	}
	public final boolean addCurrentDayInterestMinuteDataIDs(List<String> IDs)
	{
		m_currentDayInterestMinuteDataIDs.addAll(IDs);
		return true;
	}
	public final boolean clearCurrentDayInterestMinuteDataIDs()
	{
		m_currentDayInterestMinuteDataIDs.clear();
		return true;
	}
	public final List<String> getCurrentDayInterestMinuteDataIDs()
	{
		return m_currentDayInterestMinuteDataIDs;
	}
	private List<String> m_currentDayInterestMinuteDataIDs;
}
