package pers.di.common_test;

import pers.di.common.CSystem;
import pers.di.common.CTest;
import pers.di.common.CThread;

public class Test_ALL {

	public static void main(String[] args) {
		CSystem.start();
		
		CTest.ADD_TEST(TestCThread.class);
		CTest.ADD_TEST(TestCQThread.class);
		CTest.ADD_TEST(TestCUtilsDateTime.class);
		
		
		CTest.RUN_ALL_TESTS("");
		CSystem.stop();
	}
	
}
