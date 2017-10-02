package pers.di.dataengine.tasks;

import java.util.*;
import pers.di.common.*;
import pers.di.dataengine.*;

public class SharedSession {
	
	public SharedSession()
	{
		bIsTranDate = false;
		initializeCbs = new ArrayList<ListenerCallback>();
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
	public boolean bIsTranDate;
	public List<ListenerCallback> initializeCbs;
	public List<ListenerCallback> tranDayStartCbs;
	public List<ListenerCallback> minuteTimePricesCbs;
	public List<ListenerCallback> tranDayFinishCbs;
	public Map<EngineListener, DAContext> listenerDataContext;
}
