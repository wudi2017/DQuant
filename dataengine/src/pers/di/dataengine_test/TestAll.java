package pers.di.dataengine_test;

import pers.di.common.*;

public class TestAll {
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(TestStockDataApi.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
