package pers.di.account_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.account.*;
import pers.di.account.common.*;
import pers.di.common.*;

public class TestAccountDriver {
	
	public static float s_transactionCostsRatioBuy = 0.02f;
	public static float s_transactionCostsRatioSell = 0.05f;
	
	public static class MockMarketOpe extends IMarketOpe
	{
		@Override
		public int postTradeRequest(TRANACT tranact, String id, int amount, float price) {
			super.dealReply(tranact, id, amount, price, amount*price*s_transactionCostsRatioBuy);
			return 0;
		}
	}
	
	@CTest.test
	public static void test_accountDriver_reset()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver();
		cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true);
		Account acc = cAccoutDriver.account();
		
		// set default
		{
			acc.postTradeOrder(TRANACT.BUY, "600001", 100, 1.01f);
			List<HoldStock> ctnHoldList = new ArrayList<HoldStock>();
			CTest.EXPECT_TRUE(acc.getHoldStockList(ctnHoldList) == 0);
			CTest.EXPECT_TRUE(ctnHoldList.size() != 0);
		}
		
		// call reset
		cAccoutDriver.reset(10*10000f);
		
		// check
		{
			CObjectContainer<Float> ctnMoney = new CObjectContainer<Float>();
			CTest.EXPECT_TRUE(acc.getMoney(ctnMoney) == 0);
			CTest.EXPECT_DOUBLE_EQ(ctnMoney.get(), 10*10000f, 2);
			List<HoldStock> ctnHoldList = new ArrayList<HoldStock>();
			CTest.EXPECT_TRUE(acc.getHoldStockList(ctnHoldList) == 0);
			CTest.EXPECT_TRUE(ctnHoldList.size() == 0);
		}
	}
	
	
	@CTest.test
	public static void test_accountDriver()
	{
//		AccoutDriver cAccoutDriver = new AccoutDriver();
//		cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true);
//		//cAccoutDriver.reset(10*10000f);
//		
//		Account acc = cAccoutDriver.account();
//		acc.postTradeOrder(TRANACT.BUY, "600001", 100, 1.01f);
//		acc.postTradeOrder(TRANACT.BUY, "600001", 100, 2.50f);
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSystem.start();
		CTest.ADD_TEST(TestAccountDriver.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
