package pers.di.account_test;

import pers.di.account.*;
import pers.di.account.common.*;
import pers.di.common.*;

public class TestAccountDriver {
	
	public static class MockMarketOpe extends IMarketOpe
	{
		@Override
		public int postTradeRequest(TRANACT tranact, String id, int amount, float price) {
			super.dealReply(tranact, id, amount, price, 0.0f);
			return 0;
		}
	}
	
	@CTest.test
	public static void test_accountDriver()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver();
		cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true);
		cAccoutDriver.reset(10*10000f);
		
		Account acc = cAccoutDriver.account();
		acc.postTradeOrder(TRANACT.BUY, "600001", 100, 1.01f);
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSystem.start();
		CTest.ADD_TEST(TestAccountDriver.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
