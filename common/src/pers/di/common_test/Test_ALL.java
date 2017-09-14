package pers.di.common_test;

import pers.di.common.CTest;

public class Test_ALL {

	public static void main(String[] args) {
		
		CTest.ADD_TEST(TestCThread.class);
		CTest.ADD_TEST(TestCQThread.class);
		
		CTest.RUN_ALL_TESTS();
	}
	
}
