package pers.di.dataapi_test;

import pers.di.common.CSystem;
import pers.di.common.CTest;
import pers.di.dataapi_test.TestStockDataApi;

public class TestAll {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSystem.start();
		CTest.ADD_TEST(TestStockDataApi.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}

}
