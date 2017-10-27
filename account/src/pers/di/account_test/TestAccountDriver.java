package pers.di.account_test;

import pers.di.account.*;
import pers.di.account.common.*;
import pers.di.common.*;

public class TestAccountDriver {
	
	@CTest.test
	public static void test_accountDriver()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver();
		cAccoutDriver.load("mock001", "password", null);
		
		Account acc = cAccoutDriver.account();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSystem.start();
		CTest.ADD_TEST(TestAccountDriver.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
