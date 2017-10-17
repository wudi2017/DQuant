package pers.di.accountengine_test;

import pers.di.accountengine.*;
import pers.di.accountengine.common.*;
import pers.di.common.*;

public class TestAccountDriver {
	
	@CTest.test
	public static void test_accountDriver()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver();
		cAccoutDriver.load(ACCOUNTTYPE.MOCK, "mock001", "password");
		
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
