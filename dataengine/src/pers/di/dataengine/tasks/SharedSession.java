package pers.di.dataengine.tasks;

import java.util.*;
import pers.di.common.*;
import pers.di.dataengine.*;

public class SharedSession {
	
	public SharedSession()
	{
		tranDayChecker = new TranDayChecker(this);
		initializeCbs = new ArrayList<ListenerCallback>();
		unInitializeCbs = new ArrayList<ListenerCallback>();
		tranDayStartCbs = new ArrayList<ListenerCallback>();
		minuteTimePricesCbs = new ArrayList<ListenerCallback>();
		tranDayFinishCbs = new ArrayList<ListenerCallback>();
		listenerDataContext = new HashMap<EngineListener, DAContext>();
	}
	
	// base parameter
	public boolean bHistoryTest;
	public String beginDate;
	public String endDate;
	public boolean bConfigFailed;
	
	// running variable
	public TranDayChecker tranDayChecker;
	public List<ListenerCallback> initializeCbs;
	public List<ListenerCallback> unInitializeCbs;
	public List<ListenerCallback> tranDayStartCbs;
	public List<ListenerCallback> minuteTimePricesCbs;
	public List<ListenerCallback> tranDayFinishCbs;
	public Map<EngineListener, DAContext> listenerDataContext;
	
}
