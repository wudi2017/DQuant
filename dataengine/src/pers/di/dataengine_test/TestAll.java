package pers.di.dataengine_test;

import pers.di.common.*;

public class TestAll {
	public static void main(String[] args) {
		CSystem.start();
		CLog.output("COMMON", "x");
		CTest.ADD_TEST(TestDataEngine.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
