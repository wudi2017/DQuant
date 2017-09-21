package pers.di.dataengine_test;

import pers.di.common.*;

public class TestAll {
	public static void main(String[] args) {
		CSystem.start();
		//CLog.config_setTag("TEST", false);
		CTest.ADD_TEST(TestStockDataApi.class);
		CTest.ADD_TEST(TestScheduleTaskControler.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
