package pers.di.dataengine_test;

import java.util.*;

import pers.di.common.*;
import pers.di.dataengine.*;
import pers.di.dataengine.StockDataEngine.EngineTask_MinuteDataPush;

public class TestScheduleTaskControler {
	
	public static ScheduleTaskController s_ScheduleTaskController = null;
	
	private static List<String> testKeyList = new ArrayList<String>();
	
	public static class TestScheduleTask extends ScheduleTask
	{
		public TestScheduleTask(String name, String time, int pri) {
			super(name, time, pri);
		}

		@Override
		public void doTask(String date, String time) {
			String testKey = String.format("%s %s %05d", date, super.getTime(), super.getPriority());
			//CLog.output("TEST", "TestScheduleTask.doTask testKey=%s", testKey);
			testKeyList.add(testKey);
		}
	}
	
	@CTest.test
	public void test_ScheduleTaskController_History()
	{
		s_ScheduleTaskController = new ScheduleTaskController();
		testKeyList.clear();
		
		s_ScheduleTaskController.config("TriggerMode", "HistoryTest 2016-01-01 2017-01-03");
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("80a", "08:00:00", 5));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80b", "08:00:00", 6));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80c", "08:00:00", 7));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80d", "08:00:00", 8));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80e", "08:00:00", 9));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80f", "08:00:00", 1));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80g", "08:00:00", 0));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80h", "08:00:00", 0));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80i", "08:00:00", 12));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80j", "08:00:00", 8));
		
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("82a", "08:02:00", 0));
		s_ScheduleTaskController.schedule(new TestScheduleTask("82b", "08:02:00", 6));
		s_ScheduleTaskController.schedule(new TestScheduleTask("82c", "08:02:00", 3));
		s_ScheduleTaskController.schedule(new TestScheduleTask("82d", "08:02:00", 7));
		s_ScheduleTaskController.schedule(new TestScheduleTask("82e", "08:02:00", 8));
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("07a", "07:00:00", 0));
		s_ScheduleTaskController.schedule(new TestScheduleTask("07b", "07:00:00", 6));
		s_ScheduleTaskController.schedule(new TestScheduleTask("07c", "07:00:00", 1));
		s_ScheduleTaskController.schedule(new TestScheduleTask("07d", "07:00:00", 7));
		s_ScheduleTaskController.schedule(new TestScheduleTask("07e", "07:00:00", 8));
		
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("01", "01:00:00", 8));
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("04", "04:00:00", 8));
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("12", "12:00:00", 8));
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("11", "11:22:00", 8));
		
		s_ScheduleTaskController.run();
		
		CTest.EXPECT_TRUE(testKeyList.size() > 100);
		CLog.output("TEST", "test_ScheduleTaskController_History call task count %d", testKeyList.size());
		
		for(int i=0; i<testKeyList.size()-1;i++)
		{
			String testKey1 = testKeyList.get(i);
			String testKey2 = testKeyList.get(i+1);
			//CLog.output("TEST", "%s", testKey1);
			if(testKey1.compareTo(testKey2) <= 0)
			{
				CTest.EXPECT_TRUE(true);
			}
			else
			{
				CTest.EXPECT_TRUE(false);
			}
		}
		
		s_ScheduleTaskController = null;
	}
	
	@CTest.test
	public void performacecheck()
	{
		s_ScheduleTaskController = new ScheduleTaskController();
		s_ScheduleTaskController.config("TriggerMode", "HistoryTest 2016-01-01 2017-01-03");
		
		for(String time="09:30:00"; time.compareTo("11:30:00")<=0; 
				time=CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(time, 60))
		{
			s_ScheduleTaskController.schedule(new TestScheduleTask("", time, 0));
		}
		for(String time="13:00:00"; time.compareTo("15:00:00")<=0; 
				time=CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(time, 60))
		{
			s_ScheduleTaskController.schedule(new TestScheduleTask("", time, 0));
		}
		
		s_ScheduleTaskController.run();
		
		s_ScheduleTaskController = null;
	}
	public static class TestInsertThread extends CThread
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			this.msleep(3000);
			String curTime = CUtilsDateTime.GetCurTimeStr();
			String curTime_add2s = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(curTime, 2);
			s_ScheduleTaskController.schedule(new TestScheduleTask("TT", curTime_add2s, 0));
		}
		
	}
	
	//@CTest.test
	public void test_ScheduleTaskController_Realtime()
	{
		s_ScheduleTaskController = new ScheduleTaskController();
		testKeyList.clear();
		
		s_ScheduleTaskController.config("TriggerMode", "Realtime");
		
		String curDate = CUtilsDateTime.GetCurDateStr();
		String curTime = CUtilsDateTime.GetCurTimeStr();
		String curTime_add5s = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(curTime, 10);
		String curTime_add10s = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(curTime, 10);
		String curTime_add20s = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(curTime, 20);
		s_ScheduleTaskController.schedule(new TestScheduleTask("x", curTime_add10s, 5));
//		s_ScheduleTaskController.schedule(new TestScheduleTask("y", curTime_add10s, 1));
//		s_ScheduleTaskController.schedule(new TestScheduleTask("z", curTime_add20s, 6));
		
		TestInsertThread cTestThread = new TestInsertThread();
		cTestThread.startThread();
		s_ScheduleTaskController.run();
	}
	
	public static void main(String[] args) {
		//CSystem.start();
		//CLog.config_setTag("DataEngine", true);
		CTest.ADD_TEST(TestScheduleTaskControler.class);
		CTest.RUN_ALL_TESTS("TestScheduleTaskControler.");
		//CSystem.stop();
	}
}
