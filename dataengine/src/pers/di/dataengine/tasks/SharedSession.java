package pers.di.dataengine.tasks;

import java.util.*;
import pers.di.common.*;
import pers.di.dataengine.*;

public class SharedSession {
	
	public SharedSession()
	{
		tranDayChecker = new TranDayChecker(this);
		listenerDataContext = new HashMap<EngineListener, DAContext>();
		
		listeners = new ArrayList<IEngineListener>();
		listenerContext = new HashMap<IEngineListener, DAContext>();
	}
	
	// base parameter
	public boolean bHistoryTest;
	public String beginDate;
	public String endDate;
	public boolean bConfigFailed;
	
	// running variable
	public TranDayChecker tranDayChecker;
	public Map<EngineListener, DAContext> listenerDataContext;
	
	public List<IEngineListener> listeners;
	public Map<IEngineListener, DAContext> listenerContext;
}
