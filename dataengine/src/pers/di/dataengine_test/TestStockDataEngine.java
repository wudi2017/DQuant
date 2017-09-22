package pers.di.dataengine_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CFile;
import pers.di.common.CLog;
import pers.di.common.CFileSystem;
import pers.di.common.CSystem;
import pers.di.common.CTest;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.*;
import pers.di.dataengine.baseapi.*;
import pers.di.dataengine.common.KLine;
import pers.di.dataengine.common.TimePrice;

public class TestStockDataEngine {
	
	@CTest.setup
	public static void setup()
	{
		TestCommonHelper.InitLocalData();
	}
	

	public static class EngineTester
	{
		public EngineTester()
		{
			m_timeListener = StockDataEngine.instance().createTimeListener();
			m_timeListener.configCallback(this, "onTimeListener");
			List<String> listenTimeList = new ArrayList<String>();
			listenTimeList.add("08:00:00");
			m_timeListener.configListenTime(listenTimeList);
			
			m_dataPusher = StockDataEngine.instance().createDataPusher();
			m_dataPusher.configCallback(this, "onDataPush");
			List<String> pushDataTimeList = new ArrayList<String>();
			pushDataTimeList.add("09:00:00");
			pushDataTimeList.add("09:30:00");
			pushDataTimeList.add("09:40:00");
			m_dataPusher.configPushTime(pushDataTimeList);
		}
		
		public void onTimeListen(String date, String time)
		{
			CLog.output("TEST", "onTimeListen %s %s", date, time);
			if(time.equals("08:00:00"))
			{
				List<String> dataList = new ArrayList<String>();
				dataList.add("600000");
				m_dataPusher.enableCurrentDayTimePriceNow(dataList);
			}
		}
		
		public void onDataPush(DataContext ctx)
		{
			CLog.output("TEST", "onDataPush %s %s", ctx.date(), ctx.time());
		}
		
		private EngineTimeListener m_timeListener;
		private EngineDataPusher m_dataPusher;
	}

	@CTest.test
	public void test_StockDataEngine()
	{
		EngineTester cEngineTester = new EngineTester();
		StockDataEngine.instance().config("TriggerMode", "HistoryTest 2017-01-01 2017-01-03");
		StockDataEngine.instance().run();
	}
	
	public static void main(String[] args) {
		CSystem.start();
		CLog.config_setTag("DataEngine", true);
		CTest.ADD_TEST(TestStockDataEngine.class);
		CTest.RUN_ALL_TESTS("");
		CSystem.stop();
	}
}
