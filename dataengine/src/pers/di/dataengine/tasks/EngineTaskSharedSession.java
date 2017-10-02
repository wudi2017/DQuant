package pers.di.dataengine.tasks;

import java.lang.reflect.Method;
import java.util.*;

import org.json.JSONObject;

import pers.di.common.CLog;
import pers.di.dataengine.ENGINEEVENTID;
import pers.di.dataengine.EngineEventContext;
import pers.di.dataengine.EngineEventObject;
import pers.di.dataengine.EngineListener;

public class EngineTaskSharedSession {
	
	public EngineTaskSharedSession()
	{
		bIsTranDate = false;
		tranDayStartCbs = new ArrayList<ListenerCallback>();
		tranDayFinishCbs = new ArrayList<ListenerCallback>();
	}
	
	public boolean bHistoryTest;
	public String beginDate;
	public String endDate;
	public boolean bIsTranDate;
	public boolean bConfigFailed;
	public List<ListenerCallback> tranDayStartCbs;
	public List<ListenerCallback> tranDayFinishCbs;
}
