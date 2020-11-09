package pers.di.quantplatform_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.account.*;
import pers.di.account.common.HoldStock;
import pers.di.account.common.TRANACT;
import pers.di.common.*;
import pers.di.dataengine.*;
import pers.di.localstock.common.KLine;
import pers.di.localstock.common.TimePrice;
import pers.di.localstock_test.CommonTestHelper;
import pers.di.quantplatform.*;

public class TestQuantSession_Simple {
	
	public static String s_accountDataRoot = CSystem.getRWRoot() + "\\account";
	
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
		public void onInit(QuantContext context) {
			//CLog.output("TEST", "TestStrategy.onInit %s %s", ctx.date(), ctx.time());
			onInitCalled++;
		}
	
		@Override
		public void onUnInit(QuantContext context) {
			//CLog.output("TEST", "TestStrategy.onInit %s %s", ctx.date(), ctx.time());
			onUnInitCalled++;
		}
		
		@Override
		public void onDayStart(QuantContext context) {
			//CLog.output("TEST", "TestStrategy.onDayStart %s %s", ctx.date(), ctx.time());
			if (context.date().compareTo("2019-04-02") >= 0 && context.date().compareTo("2019-04-29") <= 0) {
				// day start, 
				// only call addCurrentDayInterestMinuteDataID, 
				// you could get min data in onMinuteTimePrices with IF context.pool().get(stockID).timePrices()
				// else context.pool().get(stockID).timePrices().size() == 0
				context.addCurrentDayInterestMinuteDataID("600000");
			}
			onDayBeginCalled++;
		}

		@Override
		public void onMinuteData(QuantContext context) {
			//CLog.output("TEST", "TestStrategy.onMinuteData %s %s", ctx.date(), ctx.time());

			onEveryMinuteCalled++;
			
			// 遍历所有股票
			CTest.EXPECT_TRUE(context.pool().size()!=0);
			for(int i=0; i<context.pool().size(); i++)
			{
				DAStock stock = context.pool().get(i);
				//CLog.output("TEST", "stock %s %s", stock.ID(), stock.name());
			}
			
			String StockID = "600000";
			
			// 遍历某只股票某日分时线
			// 2019-04-02 ~ 2019-04-02 has min data 600000, other day no min data 600000
			// because call addCurrentDayInterestMinuteDataID 600000 in 2019-04-02 ~ 2019-04-02
			DATimePrices cTimePrices = context.pool().get(StockID).timePrices();
			if (context.date().compareTo("2019-04-02") >= 0 && context.date().compareTo("2019-04-29") <= 0) {
				CTest.EXPECT_TRUE(cTimePrices.size()!=0);
			} else {
				CTest.EXPECT_TRUE(cTimePrices.size()==0);
				return;
			}
			
			
			TimePrice currentTimePrice = cTimePrices.get(cTimePrices.size()-1);
			CTest.EXPECT_TRUE(currentTimePrice.time.equals(context.time()));

			//CLog.output("TEST", "%s %s %s %.3f", StockID, context.date(), currentTimePrice.time, currentTimePrice.price);
			if(context.date().equals("2019-04-02") 
					&& currentTimePrice.time.equals("09:30:00"))
			{
				CTest.EXPECT_DOUBLE_EQ(currentTimePrice.price, 11.15, 2);//11.15
				onTimePricesCheckCount++;
			}
			if(context.date().equals("2019-04-15") 
					&& currentTimePrice.time.equals("11:06:00"))
			{
				CTest.EXPECT_DOUBLE_EQ(currentTimePrice.price, 11.41, 2);//11.4073
				onTimePricesCheckCount++;
			}
			if(context.date().equals("2019-04-15") 
					&& currentTimePrice.time.equals("14:07:00"))
			{
				CTest.EXPECT_DOUBLE_EQ(currentTimePrice.price, 11.26, 2);//11.261799
				onTimePricesCheckCount++;
			}
			if(context.date().equals("2019-04-29") 
					&& currentTimePrice.time.equals("15:00:00"))
			{
				CTest.EXPECT_DOUBLE_EQ(currentTimePrice.price, 11.13, 2);//11.12599
				onTimePricesCheckCount++;
			}
			
			DATimePrices cTimePrices2 = context.pool().get("600004").timePrices();
			CTest.EXPECT_LONG_EQ(cTimePrices2.size(), 0);
			
			
			// call account op
			if(context.date().compareTo("2019-04-02") == 0 &&
					context.time().compareTo("09:30:00") == 0)
			{
				//CLog.output("TEST", "pushBuyOrder 500 %f", context.pool().get(StockID).price());
				context.accountProxy().pushBuyOrder(StockID, 500, context.pool().get(StockID).price()); 
			}
			if(context.date().compareTo("2019-04-15") == 0 &&
					context.time().compareTo("14:07:00") == 0)
			{
				//CLog.output("TEST", "pushBuyOrder 800 %f", context.pool().get(StockID).price());
				context.accountProxy().pushBuyOrder(StockID, 800, context.pool().get(StockID).price()); 
			}
			if(context.date().compareTo("2019-04-29") == 0 &&
					context.time().compareTo("15:00:00") == 0)
			{
				//CLog.output("TEST", "pushSellOrder 1000 %f", context.pool().get(StockID).price());
				context.accountProxy().pushSellOrder(StockID, 1000, context.pool().get(StockID).price());
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
			KLine cCurrentKLine = cKLines.get(cKLines.size()-1);
			CTest.EXPECT_TRUE(cCurrentKLine.date.equals(ctx.date())); // 此处只能拿到前一交易日k线

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
		AccountController cAccountController = new AccountController(s_accountDataRoot);
		if(0 != cAccountController.open("mock001" , true)
				|| 0 != cAccountController.reset(10*10000f))
		{
			CLog.error("TEST", "SampleTestStrategy AccountController ERR!");
		}
		IAccount acc = cAccountController.account();
		
		Quant.instance().run("HistoryTest 2019-04-01 2019-04-30", cAccountController, new TestStrategy());
		
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

			double buyCostAll = getTranCost(TRANACT.BUY,500,11.15) + getTranCost(TRANACT.BUY,800,11.26);// 5.1115 5.18016
			buyCostAll = CUtilsMath.saveNDecimal(buyCostAll, 3);
			double sellCost = getTranCost(TRANACT.SELL,1000,11.13); // 11.3526
			sellCost = CUtilsMath.saveNDecimal(sellCost, 3);
			double ExpectMoney = 
					10*10000-500*11.15-800*11.26+1000*11.13-buyCostAll-sellCost;
			CLog.debug("TEST", "ExpectMoney %f", ExpectMoney);
			CLog.debug("TEST", "ctnMoney %f", ctnMoney.get());
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
		
		cAccountController.close();
	}
	
	private static double getTranCost(TRANACT tranact, int amount, double price) {
		double s_transactionCostsRatio_TransferFee = 0.00002; // 过户费比率(买卖双边收取)
		double s_transactionCostsRatio_Poundage = 0.00025; // 手续费比率-佣金(买卖双边收取)
		double s_transactionCosts_MinPoundage = 5.0; // 手续费最小值
		double s_transactionCostsRatio_Sell_StampDuty = 0.001; // 印花税比率(卖单边收取)
		// 过户费
		double fTransferFee = s_transactionCostsRatio_TransferFee * amount * price;
		// 佣金
		double fPoundage = s_transactionCostsRatio_Poundage * amount * price;
		if(fPoundage < s_transactionCosts_MinPoundage)
		{
			fPoundage = s_transactionCosts_MinPoundage;
		}
		// 本次卖出印花税
		double fSellStampDuty = 0;
		if(TRANACT.SELL == tranact)
		{
			fSellStampDuty = s_transactionCostsRatio_Sell_StampDuty * amount * price;
		}
		return fTransferFee + fPoundage + fSellStampDuty;
	}
	
	public static void main(String[] args) {
		CSystem.start();
		//CLog.config_setTag("TEST", false);
		CTest.ADD_TEST(TestQuantSession_Simple.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
