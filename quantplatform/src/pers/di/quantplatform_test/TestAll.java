package pers.di.quantplatform_test;

import pers.di.common.CSystem;
import pers.di.common.CTest;

public class TestAll {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSystem.start();
		CTest.ADD_TEST(TestQuantSession_Simple.class);
		CTest.ADD_TEST(TestQuantStrategy_Standard.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}

}
