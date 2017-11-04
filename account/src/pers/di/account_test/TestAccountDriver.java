package pers.di.account_test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pers.di.account.*;
import pers.di.account.common.*;
import pers.di.common.*;

public class TestAccountDriver {
	
	public static String s_accountDataRoot = CSystem.getRWRoot() + "\\account";
	
	public static float s_transactionCostsRatioBuy = 0.02f;
	public static float s_transactionCostsRatioSell = 0.05f;
	
	public static class MockMarketOpe extends IMarketOpe
	{
		@Override
		public int postTradeRequest(TRANACT tranact, String id, int amount, float price) {
			if(tranact == TRANACT.BUY)
			{
				super.dealReply(tranact, id, amount, price, amount*price*s_transactionCostsRatioBuy);
			}
			else if(tranact == TRANACT.SELL)
			{
				super.dealReply(tranact, id, amount, price, amount*price*s_transactionCostsRatioSell);
			}
			return 0;
		}
	}
	
	@CTest.test
	public static void test_accountDriver_reset()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver(s_accountDataRoot);
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
	public static void test_accountDriver_buy()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver(s_accountDataRoot);
		cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true);
		cAccoutDriver.reset(10*10000f);
		
		Account acc = cAccoutDriver.account();
		
		cAccoutDriver.setDateTime("2017-10-10", "14:00:01");
		cAccoutDriver.newDayBegin();
		acc.postTradeOrder(TRANACT.BUY, "600001", 100, 1.60f);
		acc.postTradeOrder(TRANACT.BUY, "600001", 200, 2.00f);
		cAccoutDriver.newDayEnd();
		
		cAccoutDriver.setDateTime("2017-11-11", "13:38:55");
		cAccoutDriver.newDayBegin();
		acc.postTradeOrder(TRANACT.BUY, "300002", 500, 10.6f);
		
		// check
		{
			CObjectContainer<Float> ctnMoney = new CObjectContainer<Float>();
			CTest.EXPECT_TRUE(acc.getMoney(ctnMoney) == 0);
			CTest.EXPECT_DOUBLE_EQ(ctnMoney.get(), 10*10000f - 100*1.60f - 200*2.00f - 500*10.6f, 2);
			List<HoldStock> ctnHoldList = new ArrayList<HoldStock>();
			CTest.EXPECT_TRUE(acc.getHoldStockList(ctnHoldList) == 0);
			CTest.EXPECT_TRUE(ctnHoldList.size() == 2);
			
			for(int i=0; i<ctnHoldList.size(); i++)
			{
				HoldStock cHoldStock = ctnHoldList.get(i);
				if(cHoldStock.stockID.equals("600001"))
				{
					CTest.EXPECT_STR_EQ(cHoldStock.createDate, "2017-10-10");
					CTest.EXPECT_LONG_EQ(cHoldStock.totalAmount, 300);
					CTest.EXPECT_LONG_EQ(cHoldStock.availableAmount, 300);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.totalBuyCost, (100*1.60f+200*2.00f)*s_transactionCostsRatioBuy, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.curPrice, 2.00, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.refPrimeCostPrice, 
							(100*1.60f + 200*2.0f)*(1+s_transactionCostsRatioBuy)/300 , 2);
				}
				if(cHoldStock.stockID.equals("300002"))
				{
					CTest.EXPECT_STR_EQ(cHoldStock.createDate, "2017-11-11");
					CTest.EXPECT_LONG_EQ(cHoldStock.totalAmount, 500);
					CTest.EXPECT_LONG_EQ(cHoldStock.availableAmount, 0);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.totalBuyCost, (500*10.60f)*s_transactionCostsRatioBuy, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.curPrice, 10.6, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.refPrimeCostPrice, 
							(500*10.60f)*(1+s_transactionCostsRatioBuy)/500, 2);
				}
			}
		}
	}
	
	
	@CTest.test
	public static void test_accountDriver_sell()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver(s_accountDataRoot);
		cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true);
		cAccoutDriver.reset(10*10000f);
		
		Account acc = cAccoutDriver.account();
		
		// buy
		cAccoutDriver.setDateTime("2017-10-10", "14:00:01");
		acc.postTradeOrder(TRANACT.BUY, "600001", 100, 1.60f);
		acc.postTradeOrder(TRANACT.BUY, "600001", 200, 2.00f);
		cAccoutDriver.newDayEnd();
		cAccoutDriver.setDateTime("2017-11-11", "13:38:55");
		acc.postTradeOrder(TRANACT.BUY, "300002", 500, 10.6f);
		cAccoutDriver.newDayEnd();
		
		
		// sell
		cAccoutDriver.setDateTime("2017-11-13", "14:44:44");
		acc.postTradeOrder(TRANACT.SELL, "300002", 100, 10.0f);
		cAccoutDriver.newDayEnd();
		
		// check
		{
			CObjectContainer<Float> ctnMoney = new CObjectContainer<Float>();
			CTest.EXPECT_TRUE(acc.getMoney(ctnMoney) == 0);
			float buyCost = 500*10.6f*s_transactionCostsRatioBuy;
			CTest.EXPECT_DOUBLE_EQ(ctnMoney.get(), 
					10*10000f - 100*1.60f - 200*2.00f - 500*10.6f + 100*10.0f
					-100*(buyCost/500)-100*10.0f*s_transactionCostsRatioSell, 2);
			
			List<HoldStock> ctnHoldList = new ArrayList<HoldStock>();
			CTest.EXPECT_TRUE(acc.getHoldStockList(ctnHoldList) == 0);
			CTest.EXPECT_TRUE(ctnHoldList.size() == 2);
			
			for(int i=0; i<ctnHoldList.size(); i++)
			{
				HoldStock cHoldStock = ctnHoldList.get(i);
				if(cHoldStock.stockID.equals("600001"))
				{
					CTest.EXPECT_STR_EQ(cHoldStock.createDate, "2017-10-10");
					CTest.EXPECT_LONG_EQ(cHoldStock.totalAmount, 300);
					CTest.EXPECT_LONG_EQ(cHoldStock.availableAmount, 300);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.totalBuyCost, (100*1.60f+200*2.00f)*s_transactionCostsRatioBuy, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.curPrice, 2.00, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.refPrimeCostPrice, 
							(100*1.60f + 200*2.0f)*(1+s_transactionCostsRatioBuy)/300, 2);
				}
				if(cHoldStock.stockID.equals("300002"))
				{
					CTest.EXPECT_STR_EQ(cHoldStock.createDate, "2017-11-11");
					CTest.EXPECT_LONG_EQ(cHoldStock.totalAmount, 400);
					CTest.EXPECT_LONG_EQ(cHoldStock.availableAmount, 400);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.totalBuyCost, 
							(400*10.60f)*s_transactionCostsRatioBuy, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.curPrice, 10.0, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.refPrimeCostPrice, 
							(500*10.6f-100*10.0f+500*10.6f*s_transactionCostsRatioBuy+100*10.0f*s_transactionCostsRatioSell)/400, 2);
				}
			}
		}
		
		// sell
		cAccoutDriver.setDateTime("2017-11-14", "09:50:01");
		acc.postTradeOrder(TRANACT.SELL, "300002", 400, 5.25f);
		cAccoutDriver.newDayEnd();
		// check
		{
			CObjectContainer<Float> ctnMoney = new CObjectContainer<Float>();
			CTest.EXPECT_TRUE(acc.getMoney(ctnMoney) == 0);
			float cost = 500*10.6f*s_transactionCostsRatioBuy + 100*10.0f*s_transactionCostsRatioSell + 400*5.25f*s_transactionCostsRatioSell;
			CTest.EXPECT_DOUBLE_EQ(ctnMoney.get(), 
					10*10000f - 100*1.60f - 200*2.00f - 500*10.6f + 100*10.0f + 400*5.25f - cost, 2);
			
			List<HoldStock> ctnHoldList = new ArrayList<HoldStock>();
			CTest.EXPECT_TRUE(acc.getHoldStockList(ctnHoldList) == 0);
			CTest.EXPECT_TRUE(ctnHoldList.size() == 1);
			
			for(int i=0; i<ctnHoldList.size(); i++)
			{
				HoldStock cHoldStock = ctnHoldList.get(i);
				if(cHoldStock.stockID.equals("600001"))
				{
					CTest.EXPECT_STR_EQ(cHoldStock.createDate, "2017-10-10");
					CTest.EXPECT_LONG_EQ(cHoldStock.totalAmount, 300);
					CTest.EXPECT_LONG_EQ(cHoldStock.availableAmount, 300);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.totalBuyCost, 
							(100*1.60f+200*2.00f)*s_transactionCostsRatioBuy, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.curPrice, 2.00, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.refPrimeCostPrice, 
							(100*1.60f + 200*2.0f)*(1+s_transactionCostsRatioBuy)/300, 2);
				}
			}
		}
	}
	
	@CTest.test
	public static void test_accountDriver_flushCurrentPrice()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver(s_accountDataRoot);
		cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true);
		cAccoutDriver.reset(10*10000f);
		
		Account acc = cAccoutDriver.account();
		
		cAccoutDriver.setDateTime("2017-10-10", "14:00:01");
		cAccoutDriver.newDayBegin();
		acc.postTradeOrder(TRANACT.BUY, "600001", 100, 1.60f);
		cAccoutDriver.newDayEnd();
		
		cAccoutDriver.setDateTime("2017-10-11", "14:30:01");
		cAccoutDriver.newDayBegin();
		acc.postTradeOrder(TRANACT.BUY, "600001", 200, 2.00f);
		cAccoutDriver.newDayEnd();
		
		List<HoldStock> ctnHoldList = new ArrayList<HoldStock>();
		CTest.EXPECT_TRUE(acc.getHoldStockList(ctnHoldList) == 0);
		CTest.EXPECT_TRUE(ctnHoldList.size() == 1);
		for(int i=0; i<ctnHoldList.size(); i++)
		{
			HoldStock cHoldStock = ctnHoldList.get(i);
			if(cHoldStock.stockID.equals("600001"))
			{
				CTest.EXPECT_STR_EQ(cHoldStock.createDate, "2017-10-10");
				CTest.EXPECT_LONG_EQ(cHoldStock.totalAmount, 300);
				CTest.EXPECT_LONG_EQ(cHoldStock.availableAmount, 300);
				CTest.EXPECT_DOUBLE_EQ(cHoldStock.totalBuyCost, 
						(100*1.60f+200*2.00f)*s_transactionCostsRatioBuy, 2);
				CTest.EXPECT_DOUBLE_EQ(cHoldStock.curPrice, 2.00, 2);
				CTest.EXPECT_DOUBLE_EQ(cHoldStock.refPrimeCostPrice, 
						(100*1.60f + 200*2.0f)*(1+s_transactionCostsRatioBuy)/300, 2);
				
				cAccoutDriver.flushCurrentPrice("600001", 3.14f);
				CTest.EXPECT_DOUBLE_EQ(cHoldStock.curPrice, 3.14, 2);
			}
		}
	}
	
	@CTest.test
	public static void test_accountDriver_CommissionOrder()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver(s_accountDataRoot);
		cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true);
		cAccoutDriver.reset(10*10000f);
		
		Account acc = cAccoutDriver.account();
		
		cAccoutDriver.setDateTime("2017-10-10", "14:00:01");
		cAccoutDriver.newDayBegin();
		acc.postTradeOrder(TRANACT.BUY, "600001", 100, 1.60f);
		cAccoutDriver.newDayEnd();
		
		cAccoutDriver.setDateTime("2017-10-11", "14:30:01");
		cAccoutDriver.newDayBegin();
		acc.postTradeOrder(TRANACT.BUY, "600001", 200, 2.00f);
		cAccoutDriver.setDateTime("2017-10-11", "14:32:01");
		acc.postTradeOrder(TRANACT.SELL, "600001", 100, 1.12f);
		
		List<CommissionOrder> ctnCommissionList = new ArrayList<CommissionOrder>();
		CTest.EXPECT_TRUE(acc.getCommissionOrderList(ctnCommissionList) == 0);
		CTest.EXPECT_LONG_EQ(ctnCommissionList.size(), 2);

		if(ctnCommissionList.size() == 2)
		{
			CommissionOrder cCommissionOrder0 = ctnCommissionList.get(0);
			CTest.EXPECT_STR_EQ(cCommissionOrder0.date, "2017-10-11");
			CTest.EXPECT_STR_EQ(cCommissionOrder0.time, "14:30:01");
			CTest.EXPECT_LONG_EQ(cCommissionOrder0.amount, 200);
			CTest.EXPECT_DOUBLE_EQ(cCommissionOrder0.price, 2.0f, 2);
			CTest.EXPECT_TRUE(0 == cCommissionOrder0.tranAct.compareTo(TRANACT.BUY));
			
			CommissionOrder cCommissionOrder1 = ctnCommissionList.get(1);
			CTest.EXPECT_STR_EQ(cCommissionOrder1.date, "2017-10-11");
			CTest.EXPECT_STR_EQ(cCommissionOrder1.time, "14:32:01");
			CTest.EXPECT_LONG_EQ(cCommissionOrder1.amount, 100);
			CTest.EXPECT_DOUBLE_EQ(cCommissionOrder1.price, 1.12f, 2);
			CTest.EXPECT_TRUE(0 == cCommissionOrder1.tranAct.compareTo(TRANACT.SELL));
		}
		
		cAccoutDriver.newDayEnd();
		
	}
	
	@CTest.test
	public static void test_accountDriver_performance()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver(s_accountDataRoot);
		cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true);
		cAccoutDriver.reset(10*10000f);
		
		Account acc = cAccoutDriver.account();
		
		cAccoutDriver.setDateTime("2017-10-10", "14:00:01");
		cAccoutDriver.newDayBegin();
		acc.postTradeOrder(TRANACT.BUY, "600001", 100, 1.60f);
		acc.postTradeOrder(TRANACT.BUY, "600001", 200, 2.00f);
		acc.postTradeOrder(TRANACT.BUY, "300002", 500, 10.6f);
		
		List<HoldStock> ctnHoldList = new ArrayList<HoldStock>();
		CTest.EXPECT_TRUE(acc.getHoldStockList(ctnHoldList) == 0);
		CTest.EXPECT_TRUE(ctnHoldList.size() == 2);
		
		int iTestTimes=100*10000;
		int iTest = 0;
		for(; iTest<10000*100; iTest++)
		{
			cAccoutDriver.setDateTime("2017-10-10", "14:00:30");
			for(int i=0; i<ctnHoldList.size(); i++)
			{
				HoldStock cHoldStock = ctnHoldList.get(i);
				if(cHoldStock.stockID.equals("600001"))
				{
					cAccoutDriver.flushCurrentPrice("600001", 3.14f);
				}
				if(cHoldStock.stockID.equals("300002"))
				{
					cAccoutDriver.flushCurrentPrice("300002", 6.28f);
				}
			}
		}
		CTest.EXPECT_LONG_EQ(iTest, iTestTimes);
		
		cAccoutDriver.newDayEnd();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSystem.start();
		CTest.ADD_TEST(TestAccountDriver.class);
		CTest.RUN_ALL_TESTS("TestAccountDriver.");
		CLog.output("TEST", "END");
		CSystem.stop();
	}
}
