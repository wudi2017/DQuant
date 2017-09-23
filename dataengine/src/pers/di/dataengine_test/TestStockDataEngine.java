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
			m_listener = StockDataEngine.instance().createListener();
			m_listener.subscribe(ENGINEEVENTID.TRADINGDAYSTART, this, "onTradingDayStart");
			m_listener.subscribe(ENGINEEVENTID.TRADINGDAYFINISH, this, "onTradingDayFinish");
			m_listener.subscribe(ENGINEEVENTID.DAYDATAPUSH, this, "onDayDataPush");
		}
		
		public void onTradingDayStart(EngineEventContext ctx, EngineEventObject ev)
		{
			CLog.output("TEST", "onNewDayStart %s %s", ctx.date(), ctx.time());
			List<String> stockIDs = new ArrayList<String>();
			stockIDs.equals("600000");
			m_listener.setInterestMinuteDataID(stockIDs);
		}
		
		public void onTradingDayFinish(EngineEventContext ctx, EngineEventObject ev)
		{
			CLog.output("TEST", "onNewDayFinish %s %s", ctx.date(), ctx.time());
		}
		
		public void onDayDataPush(EngineEventContext ctx, EngineEventObject ev)
		{
			CLog.output("TEST", "onDayDataPush %s %s", ctx.date(), ctx.time());
		}
		
		private EngineListener m_listener;
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
