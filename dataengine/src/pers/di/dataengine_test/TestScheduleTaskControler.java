package pers.di.dataengine_test;

import pers.di.common.*;
import pers.di.dataengine.*;

public class TestScheduleTaskControler {
	
	public static ScheduleTaskController s_ScheduleTaskController = new ScheduleTaskController();
	
	
	@CTest.test
	public void test_ScheduleTaskController()
	{
		s_ScheduleTaskController.config_RunDatePeriod("2017-01-01", "2017-12-01");
		s_ScheduleTaskController.run();
	}
	
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(TestScheduleTaskControler.class);
		CTest.RUN_ALL_TESTS("");
		CSystem.stop();
	}
}
