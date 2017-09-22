package pers.di.dataengine;

import java.util.*;

import pers.di.common.CListObserver;
import pers.di.common.CLog;
import pers.di.common.CThread;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.baseapi.StockDataApi;
import pers.di.dataengine.common.KLine;
import pers.di.dataengine.common.RealTimeInfo;
import pers.di.dataengine.common.StockUtils;

public class StockDataEngine {
	private static StockDataEngine s_instance = new StockDataEngine(); 
	private StockDataEngine ()
	{
		m_bHistoryTest = false;
		m_beginDate = "0000-00-00";
		m_endDate = "0000-00-00";
		m_configFailed = false;
		
		m_ScheduleTaskController = new ScheduleTaskController();
	}
	public static StockDataEngine instance() {  
		return s_instance;  
	} 

	/*
	 * 配置量化引擎
	 * 
	 * key: "TriggerMode" 触发模式
	 *     value: "HistoryTest XXXX-XX-XX XXXX-XXXX-XX" 历史回测
	 *     value: "Realtime" 实时
	 */
	public int config(String key, String value)
	{
		if(0 == key.compareTo("TriggerMode"))
		{
			if(value.contains("HistoryTest"))
			{
				String[] cols = value.split(" ");
				m_bHistoryTest = true;
				m_beginDate = cols[1];
				m_endDate = cols[2];
			
				// 初始化历史交易日表
				if( !CUtilsDateTime.CheckValidDate(m_beginDate) 
						|| !CUtilsDateTime.CheckValidDate(m_endDate))
				{
					CLog.error("DataEngine", "input parameter error!");
					m_configFailed = true;
				}
				else
				{
					m_ScheduleTaskController.config("TriggerMode", value);
				}
			}
			else if(value.contains("Realtime"))
			{
				m_bHistoryTest = false;
				m_ScheduleTaskController.config("TriggerMode", "Realtime");
			}
			else
			{
				CLog.error("DataEngine", "input parameter error!");
				m_configFailed = true;
			}
		}
		else
		{
			CLog.error("DataEngine", "input parameter error!");
			m_configFailed = true;
		}
		return 0;
	}
	
	public EngineTimeListener createTimeListener()
	{
		EngineTimeListener cTimeListener = new EngineTimeListener(ScheduleTaskController stc);
		m_timeListenerList.add(cTimeListener);
		return cTimeListener;
	}
	
	public EngineDataPusher createDataPusher()
	{
		return new EngineDataPusher();
	}
	
	public int run()
	{
		if(m_configFailed) return -1;
		
		// init
		
		// run ScheduleTaskController
		m_ScheduleTaskController.run();
		
		return 0;
	}
	
	private List<EngineTimeListener> m_timeListenerList;
	
	/////////////////////////////////////////////////////////////////////////////
	
	private boolean m_bHistoryTest;
	private String m_beginDate;
	private String m_endDate;
	private boolean m_configFailed;
	
	private ScheduleTaskController m_ScheduleTaskController;
	private StockDataApi m_stockDataApi;
}
