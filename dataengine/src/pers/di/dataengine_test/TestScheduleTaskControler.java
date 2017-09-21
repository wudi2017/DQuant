package pers.di.dataengine_test;

import java.util.*;

import pers.di.common.*;
import pers.di.dataengine.*;

public class TestScheduleTaskControler {
	
	public static ScheduleTaskController s_ScheduleTaskController = new ScheduleTaskController();
	
	private static List<String> testKeyList = new ArrayList<String>();
	
	public static class TestScheduleTask extends ScheduleTask
	{
		public TestScheduleTask(String name, String time, int pri) {
			super(name, time, pri);
		}

		@Override
		public void doTask(String date, String time) {
			CLog.output("TEST", "TestScheduleTask_%s_%s_%d.doTask", 
					super.getName(), super.getTime(), super.getPriority());
			String testKey = String.format("%s %s %5d", date, super.getTime(), super.getPriority());
			testKeyList.add(testKey);
		}
	}
	
	@CTest.test
	public void test_ScheduleTaskController()
	{
		s_ScheduleTaskController.config("TriggerMode", "HistoryTest 2017-01-01 2017-01-03");
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("80a", "08:00:00", 5));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80b", "08:00:00", 6));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80c", "08:00:00", 7));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80d", "08:00:00", 8));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80e", "08:00:00", 9));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80f", "08:00:00", -2));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80g", "08:00:00", -3));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80h", "08:00:00", -7));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80i", "08:00:00", 12));
		s_ScheduleTaskController.schedule(new TestScheduleTask("80j", "08:00:00", 8));
		
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("82a", "08:02:00", 0));
		s_ScheduleTaskController.schedule(new TestScheduleTask("82b", "08:02:00", 6));
		s_ScheduleTaskController.schedule(new TestScheduleTask("82c", "08:02:00", -9));
		s_ScheduleTaskController.schedule(new TestScheduleTask("82d", "08:02:00", 7));
		s_ScheduleTaskController.schedule(new TestScheduleTask("82e", "08:02:00", 8));
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("07a", "07:00:00", 0));
		s_ScheduleTaskController.schedule(new TestScheduleTask("07b", "07:00:00", 6));
		s_ScheduleTaskController.schedule(new TestScheduleTask("07c", "07:00:00", -9));
		s_ScheduleTaskController.schedule(new TestScheduleTask("07d", "07:00:00", 7));
		s_ScheduleTaskController.schedule(new TestScheduleTask("07e", "07:00:00", 8));
		
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("01", "01:00:00", 8));
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("04", "04:00:00", 8));
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("12", "12:00:00", 8));
		
		s_ScheduleTaskController.schedule(new TestScheduleTask("11", "11:22:00", 8));
		
		s_ScheduleTaskController.run();
		
		for(int i=0; i<testKeyList.size()-1;i++)
		{
			String testKey1 = testKeyList.get(i);
			String testKey2 = testKeyList.get(2);
			CLog.output("TEST", "%s", testKey1);
			CTest.EXPECT_TRUE(testKey1.compareTo(testKey2) < 0);
		}
	}
	
	public static void main(String[] args) {
		CSystem.start();
		CLog.config_setTag("DataEngine", true);
		CTest.ADD_TEST(TestScheduleTaskControler.class);
		CTest.RUN_ALL_TESTS("");
		CSystem.stop();
	}
}
