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
	public static void test_accountDriver_buy()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver();
		cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true);
		cAccoutDriver.reset(10*10000f);
		
		Account acc = cAccoutDriver.account();
		
		cAccoutDriver.setDateTime("2017-10-10", "14:00:01");
		acc.postTradeOrder(TRANACT.BUY, "600001", 100, 1.60f);
		acc.postTradeOrder(TRANACT.BUY, "600001", 200, 2.00f);
		//cAccoutDriver.newDayEnd();
		
		cAccoutDriver.setDateTime("2017-11-11", "13:38:55");
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
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.avePrimeCostPrice, 1.867, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.curPrice, 2.00, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.cost, (100*1.60f+200*2.00f)*s_transactionCostsRatioBuy, 2);
				}
				if(cHoldStock.stockID.equals("300002"))
				{
					CTest.EXPECT_STR_EQ(cHoldStock.createDate, "2017-11-11");
					CTest.EXPECT_LONG_EQ(cHoldStock.totalAmount, 500);
					CTest.EXPECT_LONG_EQ(cHoldStock.availableAmount, 0);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.avePrimeCostPrice, 10.6, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.curPrice, 10.6, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.cost, (500*10.60f)*s_transactionCostsRatioBuy, 2);
				}
			}
		}
	}
	
	
	@CTest.test
	public static void test_accountDriver_sell()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver();
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
			CTest.EXPECT_DOUBLE_EQ(ctnMoney.get(), 10*10000f - 100*1.60f - 200*2.00f - 500*10.6f + 100*10.0f, 2);
			
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
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.avePrimeCostPrice, 1.867, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.curPrice, 2.00, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.cost, (100*1.60f+200*2.00f)*s_transactionCostsRatioBuy, 2);
				}
				if(cHoldStock.stockID.equals("300002"))
				{
					CTest.EXPECT_STR_EQ(cHoldStock.createDate, "2017-11-11");
					CTest.EXPECT_LONG_EQ(cHoldStock.totalAmount, 400);
					CTest.EXPECT_LONG_EQ(cHoldStock.availableAmount, 400);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.avePrimeCostPrice, (500*10.6f-100*10.0f)/400, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.curPrice, 10.0, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.cost, (500*10.60f)*s_transactionCostsRatioBuy + (100*10.0f)*s_transactionCostsRatioSell, 2);
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
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.avePrimeCostPrice, 1.867, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.curPrice, 2.00, 2);
					CTest.EXPECT_DOUBLE_EQ(cHoldStock.cost, (100*1.60f+200*2.00f)*s_transactionCostsRatioBuy, 2);
				}
			}
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
		CTest.RUN_ALL_TESTS("TestAccountDriver.test_accountDriver_sell");
		CSystem.stop();
	}
}
