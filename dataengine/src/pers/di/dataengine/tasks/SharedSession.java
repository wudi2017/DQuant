package pers.di.dataengine.tasks;

import java.util.*;
import pers.di.common.*;
import pers.di.dataengine.*;

public class SharedSession {
	
	public SharedSession()
	{
		bIsTranDate = false;
		tranDayStartCbs = new ArrayList<ListenerCallback>();
		minuteTimePricesCbs = new ArrayList<ListenerCallback>();
		tranDayFinishCbs = new ArrayList<ListenerCallback>();
		dACtx = new DAContext();
	}
	
	// base parameter
	public boolean bHistoryTest;
	public String beginDate;
	public String endDate;
	public boolean bConfigFailed;
	
	// running variable
	public boolean bIsTranDate;
	public List<ListenerCallback> tranDayStartCbs;
	public List<ListenerCallback> minuteTimePricesCbs;
	public List<ListenerCallback> tranDayFinishCbs;
	public DAContext dACtx;
}
