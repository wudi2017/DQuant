package pers.di.quantplatform_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.account.*;
import pers.di.account.common.HoldStock;
import pers.di.account.common.TRANACT;
import pers.di.common.*;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.TimePrice;
import pers.di.dataapi_test.CommonTestHelper;
import pers.di.dataengine.*;
import pers.di.quantplatform.*;

public class TestQuantSession_Simple {
	
	public static String s_accountDataRoot = CSystem.getRWRoot() + "\\account";

	public static double s_transactionCostsRatioBuy = 0.02f;
	public static double s_transactionCostsRatioSell = 0.05f;
	
	public static class MockMarketOpe extends IMarketOpe
	{
		@Override
		public int start()
		{
			return 0;
		}
		
		@Override
		public int stop()
		{
			return 0;
		}
		
		@Override
		public int postTradeRequest(TRANACT tranact, String id, int amount, double price) {
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
	
	@CTest.setup
	public static void setup()
	{
		String newestDate = "2019-09-10";
		 List<String> stockIDs = new ArrayList<String>()
			{{add("999999");add("600000");add("300163");add("002468");}};
		CommonTestHelper.InitLocalData(newestDate, stockIDs);
	}
	

	public static class TestStrategy extends QuantStrategy
	{
		@Override
		public void onInit(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onInit %s %s", ctx.date(), ctx.time());
			onInitCalled++;
		}
	
		@Override
		public void onUnInit(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onInit %s %s", ctx.date(), ctx.time());
			onUnInitCalled++;
		}
		
		@Override
		public void onDayStart(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onDayStart %s %s", ctx.date(), ctx.time());
			ctx.addCurrentDayInterestMinuteDataID("600000");
			onDayBeginCalled++;
		}

		@Override
		public void onMinuteData(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onMinuteData %s %s", ctx.date(), ctx.time());

			onEveryMinuteCalled++;
			
			// �������й�Ʊ
			CTest.EXPECT_TRUE(ctx.pool().size()!=0);
			for(int i=0; i<ctx.pool().size(); i++)
			{
				DAStock stock = ctx.pool().get(i);
				//CLog.output("TEST", "stock %s %s", stock.ID(), stock.name());
			}
			
			String StockID = "600000";
			
			// ����ĳֻ��Ʊĳ�շ�ʱ��
			DATimePrices cTimePrices = ctx.pool().get(StockID).timePrices();
			CTest.EXPECT_TRUE(cTimePrices.size()!=0);
			TimePrice currentTimePrice = cTimePrices.get(cTimePrices.size()-1);
			CTest.EXPECT_TRUE(currentTimePrice.time.equals(ctx.time()));

			// CLog.output("TEST", "%s %s %s %.3f", StockID, ctx.date(), cTimePrice.time, cTimePrice.price);
			if(ctx.date().equals("2019-04-02") 
					&& currentTimePrice.time.equals("09:30:00"))
			{
				CTest.EXPECT_DOUBLE_EQ(currentTimePrice.price, 11.15, 2);//11.15
				onTimePricesCheckCount++;
			}
			if(ctx.date().equals("2019-04-15") 
					&& currentTimePrice.time.equals("11:06:00"))
			{
				CTest.EXPECT_DOUBLE_EQ(currentTimePrice.price, 11.41, 2);//11.4073
				onTimePricesCheckCount++;
			}
			if(ctx.date().equals("2019-04-15") 
					&& currentTimePrice.time.equals("14:07:00"))
			{
				CTest.EXPECT_DOUBLE_EQ(currentTimePrice.price, 11.26, 2);//11.261799
				onTimePricesCheckCount++;
			}
			if(ctx.date().equals("2019-04-29") 
					&& currentTimePrice.time.equals("15:00:00"))
			{
				CTest.EXPECT_DOUBLE_EQ(currentTimePrice.price, 11.13, 2);//11.12599
				onTimePricesCheckCount++;
			}
			
			DATimePrices cTimePrices2 = ctx.pool().get("600004").timePrices();
			CTest.EXPECT_LONG_EQ(cTimePrices2.size(), 0);
			
			
			// call account op
			if(ctx.date().compareTo("2019-04-02") == 0 &&
					ctx.time().compareTo("09:30:00") == 0)
			{
				ctx.ap().pushBuyOrder(StockID, 500, ctx.pool().get(StockID).price()); 
			}
			if(ctx.date().compareTo("2019-04-15") == 0 &&
					ctx.time().compareTo("14:07:00") == 0)
			{
				ctx.ap().pushBuyOrder(StockID, 800, ctx.pool().get(StockID).price()); 
			}
			if(ctx.date().compareTo("2019-04-29") == 0 &&
					ctx.time().compareTo("15:00:00") == 0)
			{
				ctx.ap().pushSellOrder(StockID, 1000, ctx.pool().get(StockID).price());
			}
		}

		@Override
		public void onDayFinish(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onDayFinish %s %s", ctx.date(), ctx.time());
			onDayEndCalled++;
			
			String StockID = "600000";
			
			// ����ĳֻ��Ʊ��K��
			DAKLines cKLines = ctx.pool().get(StockID).dayKLines();
			CTest.EXPECT_TRUE(cKLines.size()!=0);
			KLine cCurrentKLine = cKLines.get(cKLines.size()-1);
			CTest.EXPECT_TRUE(cCurrentKLine.date.equals(ctx.date())); // �˴�ֻ���õ�ǰһ������k��

			if(cCurrentKLine.date.equals("2019-04-02"))
			{
				CTest.EXPECT_DOUBLE_EQ(cCurrentKLine.open, 11.15, 2);
				CTest.EXPECT_DOUBLE_EQ(cCurrentKLine.close, 11.09, 2);
				CTest.EXPECT_DOUBLE_EQ(cCurrentKLine.high, 11.17, 2);
				CTest.EXPECT_DOUBLE_EQ(cCurrentKLine.low, 11.06, 2);
			}

		}
	}
	
	public static int onInitCalled = 0;
	public static int onUnInitCalled = 0;
	public static int onDayBeginCalled = 0;
	public static int onDayEndCalled = 0;
	public static int onEveryMinuteCalled = 0;
	public static int onTimePricesCheckCount = 0;
	
	@CTest.test
	public void test_QuantSession()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver(s_accountDataRoot);
		if(0 != cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true)
				|| 0 != cAccoutDriver.reset(10*10000f))
		{
			CLog.error("TEST", "SampleTestStrategy AccoutDriver ERR!");
		}
		Account acc = cAccoutDriver.account();
		
		QuantSession qSession = new QuantSession(
				"HistoryTest 2019-04-01 2019-04-30", 
				cAccoutDriver, 
				new TestStrategy());
		qSession.run();
		
		// check data
		CTest.EXPECT_LONG_EQ(onInitCalled, 1);
		CTest.EXPECT_LONG_EQ(onUnInitCalled, 1);
		CTest.EXPECT_LONG_EQ(onDayBeginCalled, 21);
		CTest.EXPECT_LONG_EQ(onDayEndCalled, 21);
		CTest.EXPECT_LONG_EQ(onEveryMinuteCalled, 21*242);
		CTest.EXPECT_LONG_EQ(onTimePricesCheckCount, 4 );
		
		// check acc
		{
			CObjectContainer<Double> ctnMoney = new CObjectContainer<Double>();
			acc.getMoney(ctnMoney);

			double buyCostAll = 500*11.15*s_transactionCostsRatioBuy + 800*11.26*s_transactionCostsRatioBuy;
			double sellCost = 1000*11.13*s_transactionCostsRatioSell;
			double ExpectMoney = 
					10*10000-500*11.15-800*11.26+1000*11.13-buyCostAll-sellCost;
			CTest.EXPECT_DOUBLE_EQ(ctnMoney.get(), ExpectMoney, 2);
			
			List<HoldStock> ctnHoldList = new ArrayList<HoldStock>();
			CTest.EXPECT_TRUE(acc.getHoldStockList(ctnHoldList) == 0);
			CTest.EXPECT_TRUE(ctnHoldList.size() == 1);

			HoldStock cHoldStock = ctnHoldList.get(0);
			CTest.EXPECT_STR_EQ(cHoldStock.stockID, "600000");
			CTest.EXPECT_STR_EQ(cHoldStock.createDate, "2019-04-02");
			double expectRefPrimeCostPrice = 0.0f;
			expectRefPrimeCostPrice=(500*11.15+800*11.26+buyCostAll)/(500+800);
			double sellProfit = 1000*(11.13 - expectRefPrimeCostPrice);
			expectRefPrimeCostPrice=(expectRefPrimeCostPrice*(1300-1000) + sellCost - sellProfit)/(1300-1000);
			CTest.EXPECT_DOUBLE_EQ(cHoldStock.refPrimeCostPrice, expectRefPrimeCostPrice, 2);
		}
		
	}
	
	public static void main(String[] args) {
		CSystem.start();
		//CLog.config_setTag("TEST", false);
		CTest.ADD_TEST(TestQuantSession_Simple.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
