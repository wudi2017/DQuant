package pers.di.marketaccount_test;

import java.util.*;

import pers.di.account.Account;
import pers.di.account.AccoutDriver;
import pers.di.account.common.HoldStock;
import pers.di.account.common.TRANACT;
import pers.di.account.detail.*;
import pers.di.account_test.TestAccountDriver.MockMarketOpe;
import pers.di.common.*;
import pers.di.marketaccount.mock.MockAccountOpe;
import pers.di.marketaccount.real.RealAccountOpe;

public class TestRealAccountOpe {
	
	public static String s_accountDataRoot = CSystem.getRWRoot() + "\\account";

	@CTest.test
	public static void testRealAccountOpe()
	{
		RealAccountOpe cRealAccountOpe = new RealAccountOpe();
		
//		int iInit = cRealAccountOpe.newDayInit();
//		CTest.EXPECT_LONG_EQ(0, iInit);
//		
//		int iEnd = cRealAccountOpe.newDayTranEnd();
//		CTest.EXPECT_LONG_EQ(0, iEnd);
		
//		int iBuy = cRealAccountOpe.pushBuyOrder(CUtilsDateTime.GetCurDateStr(), CUtilsDateTime.GetCurTimeStr(), 
//				"601988", 100, 0.99f);
//		CTest.EXPECT_LONG_EQ(0, iBuy);
//		
//		int iSell = cRealAccountOpe.pushBuyOrder(CUtilsDateTime.GetCurDateStr(), CUtilsDateTime.GetCurTimeStr(), 
//				"601988", 100, 0.99f);
//		CTest.EXPECT_LONG_EQ(0, iSell);
		
//		CObjectContainer<Float> ctnAvailableMoney = new CObjectContainer<Float>();
//		int iAvailableMoney = cRealAccountOpe.getAvailableMoney(ctnAvailableMoney);
//		CTest.EXPECT_LONG_EQ(0, iAvailableMoney);
		
//		CObjectContainer<Float> ctnMoney = new CObjectContainer<Float>();
//		int iMoney = cRealAccountOpe.getMoney(ctnMoney);
//		CTest.EXPECT_LONG_EQ(0, iMoney);
//		CTest.EXPECT_TRUE(ctnMoney.get() > 100.0f);
		
//		List<HoldStock> ctnHoldStock = new ArrayList<HoldStock>();
//		int iHold = cRealAccountOpe.getHoldStockList(ctnHoldStock);
//		CTest.EXPECT_LONG_EQ(0, iHold);
	}
	
	@CTest.test
	public static void test_mockaccountope()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver(s_accountDataRoot);
		cAccoutDriver.load("mock001" ,  new MockAccountOpe(), true);
		cAccoutDriver.reset(10*10000f);
		
		Account acc = cAccoutDriver.account();
		
		// buy
		cAccoutDriver.setDateTime("2016-12-02", "14:55:02");
		cAccoutDriver.newDayBegin();
		acc.postTradeOrder(TRANACT.BUY, "300348", 2000, 28.931f);
		cAccoutDriver.newDayEnd();

		// sell
		cAccoutDriver.setDateTime("2016-12-06", "14:33:29");
		acc.postTradeOrder(TRANACT.SELL, "300348", 2000, 28.301f);
		cAccoutDriver.newDayEnd();
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSystem.start();
		CTest.ADD_TEST(TestRealAccountOpe.class);
		CTest.RUN_ALL_TESTS("");
		CSystem.stop();
	}
}
