package pers.di.quantplatform_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.account.*;
import pers.di.account.common.HoldStock;
import pers.di.account.common.TRANACT;
import pers.di.common.*;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.TimePrice;
import pers.di.dataapi_test.TestCommonHelper;
import pers.di.dataengine.*;
import pers.di.quantplatform.*;

public class TestQuantSession_Simple {
	
	public static String s_accountDataRoot = CSystem.getRWRoot() + "\\account";

	public static double s_transactionCostsRatioBuy = 0.02f;
	public static double s_transactionCostsRatioSell = 0.05f;
	
	public static class MockMarketOpe extends IMarketOpe
	{
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
		String newestDate = "2017-08-10";
		 List<String> stockIDs = new ArrayList<String>()
			{{add("999999");add("600000");add("300163");add("002468");}};
		TestCommonHelper.InitLocalData(newestDate, stockIDs);
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
			super.addCurrentDayInterestMinuteDataID("600000");
			onDayBeginCalled++;
		}

		@Override
		public void onMinuteData(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onMinuteData %s %s", ctx.date(), ctx.time());

			onEveryMinuteCalled++;
			
			// 遍历所有股票
			CTest.EXPECT_TRUE(ctx.pool().size()!=0);
			for(int i=0; i<ctx.pool().size(); i++)
			{
				DAStock stock = ctx.pool().get(i);
				//CLog.output("TEST", "stock %s %s", stock.ID(), stock.name());
			}
			
			String StockID = "600000";
			
			// 遍历某只股票某日分时线
			DATimePrices cTimePrices = ctx.pool().get(StockID).timePrices();
			CTest.EXPECT_TRUE(cTimePrices.size()!=0);
			CTest.EXPECT_TRUE(cTimePrices.get(cTimePrices.size()-1).time.equals(ctx.time()));
			boolean bCheckTimePrice1 = false;
			boolean bCheckTimePrice2 = false;
			boolean bCheckTimePrice3 = false;
			boolean bCheckTimePrice4 = false;
			for(int i=0; i<cTimePrices.size(); i++)
			{
				onTimePricesCheckCount++;
				TimePrice cTimePrice = cTimePrices.get(i);
				// CLog.output("TEST", "%s %s %s %.3f", StockID, ctx.date(), cTimePrice.time, cTimePrice.price);
				if(ctx.date().equals("2017-01-16") 
						&& cTimePrice.time.equals("09:30:00"))
				{
					CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 12.33, 2);
					bCheckTimePrice1 = true;
				}
				if(ctx.date().equals("2017-01-16") 
						&& cTimePrice.time.equals("11:06:00"))
				{
					CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 12.24, 2);
					bCheckTimePrice2 = true;
				}
				if(ctx.date().equals("2017-01-16") 
						&& cTimePrice.time.equals("14:40:00"))
				{
					CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 12.61, 2);
					bCheckTimePrice3 = true;
				}
				if(ctx.date().equals("2017-01-17") 
						&& cTimePrice.time.equals("09:30:00"))
				{
					CTest.EXPECT_DOUBLE_EQ(cTimePrice.price, 12.51, 2);
					bCheckTimePrice4 = true;
				}
			}
			if(ctx.date().compareTo("2017-01-16") == 0)
			{
				CTest.EXPECT_TRUE(bCheckTimePrice1);
			}
			if(ctx.date().compareTo("2017-01-16") == 0
					&& ctx.time().equals("11:06:00"))
			{
				CTest.EXPECT_TRUE(bCheckTimePrice2);
			}
			if(ctx.date().compareTo("2017-01-16") == 0
					&& ctx.time().equals("14:41:00"))
			{
				CTest.EXPECT_TRUE(bCheckTimePrice3);
			}
			if(ctx.date().compareTo("2017-01-17") == 0)
			{
				CTest.EXPECT_TRUE(bCheckTimePrice4);
			}
			
			DATimePrices cTimePrices2 = ctx.pool().get("600004").timePrices();
			CTest.EXPECT_LONG_EQ(cTimePrices2.size(), 0);
			
			
			// call account op
			if(ctx.date().compareTo("2017-01-16") == 0 &&
					ctx.time().compareTo("09:30:00") == 0)
			{
				ctx.ap().pushBuyOrder(StockID, 500, ctx.pool().get(StockID).price()); // 500 12.330769~12.331
			}
			if(ctx.date().compareTo("2017-01-16") == 0 &&
					ctx.time().compareTo("14:40:00") == 0)
			{
				ctx.ap().pushBuyOrder(StockID, 800, ctx.pool().get(StockID).price()); // 800 12.611877~12.612
			}
			if(ctx.date().compareTo("2017-01-17") == 0 &&
					ctx.time().compareTo("09:30:00") == 0)
			{
				ctx.ap().pushSellOrder(StockID, 1000, ctx.pool().get(StockID).price()); // 1000 12.507691~12.508
			}
		}

		@Override
		public void onDayFinish(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onDayFinish %s %s", ctx.date(), ctx.time());
			onDayEndCalled++;
			
			String StockID = "600000";
			
			// 遍历某只股票日K线
			DAKLines cKLines = ctx.pool().get(StockID).dayKLines();
			CTest.EXPECT_TRUE(cKLines.size()!=0);
			CTest.EXPECT_TRUE(cKLines.get(cKLines.size()-1).date.equals(ctx.date())); // 此处只能拿到前一交易日k线
			boolean bCheckPrice = false;
			for(int i=0; i<cKLines.size(); i++)
			{
				KLine cKLine = cKLines.get(i);
				//CLog.output("TEST", "date %s close %.3f", cKLine.date, cKLine.close);
				
				if(cKLine.date.equals("2017-01-16"))
				{
					CTest.EXPECT_DOUBLE_EQ(cKLine.open, 12.33, 2);
					CTest.EXPECT_DOUBLE_EQ(cKLine.close, 12.58, 2);
					CTest.EXPECT_DOUBLE_EQ(cKLine.high, 12.62, 2);
					CTest.EXPECT_DOUBLE_EQ(cKLine.low, 12.23, 2);
					bCheckPrice = true;
				}
			}
			if(ctx.date().compareTo("2017-01-16") >= 0)
			{
				CTest.EXPECT_TRUE(bCheckPrice);
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
				"HistoryTest 2017-01-01 2017-02-03", 
				cAccoutDriver, 
				new TestStrategy());
		qSession.run();
		
		// check data
		CTest.EXPECT_LONG_EQ(onInitCalled, 1);
		CTest.EXPECT_LONG_EQ(onUnInitCalled, 1);
		CTest.EXPECT_LONG_EQ(onDayBeginCalled, 19);
		CTest.EXPECT_LONG_EQ(onDayEndCalled, 19);
		CTest.EXPECT_LONG_EQ(onEveryMinuteCalled, 19*242);
		CTest.EXPECT_LONG_EQ(onTimePricesCheckCount, 19* (1+242)*242/2 );
		
		// check acc
		{
			CObjectContainer<Double> ctnMoney = new CObjectContainer<Double>();
			acc.getMoney(ctnMoney);

			double buyCostAll = 500*12.331*s_transactionCostsRatioBuy + 800*12.612*s_transactionCostsRatioBuy;
			double sellCost = 1000*12.508*s_transactionCostsRatioSell;
			double ExpectMoney = 
					10*10000-500*12.331-800*12.612+1000*12.508-buyCostAll-sellCost;
			CTest.EXPECT_DOUBLE_EQ(ctnMoney.get(),ExpectMoney, 2);
			
			List<HoldStock> ctnHoldList = new ArrayList<HoldStock>();
			CTest.EXPECT_TRUE(acc.getHoldStockList(ctnHoldList) == 0);
			CTest.EXPECT_TRUE(ctnHoldList.size() == 1);
			if(ctnHoldList.size() == 1)
			{
				HoldStock cHoldStock = ctnHoldList.get(0);
				CTest.EXPECT_STR_EQ(cHoldStock.stockID, "600000");
				CTest.EXPECT_STR_EQ(cHoldStock.createDate, "2017-01-16");
				
				double expectRefPrimeCostPrice = 0.0f;
				expectRefPrimeCostPrice=(500*12.331+800*12.612+buyCostAll)/(500+800);
				double sellProfit = 1000*(12.507691f - expectRefPrimeCostPrice);
				expectRefPrimeCostPrice=(expectRefPrimeCostPrice*(1300-1000) + sellCost - sellProfit)/(1300-1000);
				CTest.EXPECT_DOUBLE_EQ(cHoldStock.refPrimeCostPrice, expectRefPrimeCostPrice, 2);
			}
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
