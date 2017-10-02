package pers.di.quantplatform_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.accountengine.*;
import pers.di.common.*;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.TimePrice;
import pers.di.dataapi_test.TestCommonHelper;
import pers.di.dataengine.*;
import pers.di.quantplatform.*;

public class TestQuantSession {

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
			
			// 遍历某只股票日K线
			DAKLines cKLines = ctx.pool().get(StockID).dayKLines();
			CTest.EXPECT_TRUE(cKLines.size()!=0);
			CTest.EXPECT_TRUE(cKLines.get(cKLines.size()-1).date.equals(ctx.date()));
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
				//CLog.output("TEST", "%s %s %s %.3f", StockID, ctx.date(), cTimePrice.time, cTimePrice.price);
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
		}

		@Override
		public void onDayFinish(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onDayFinish %s %s", ctx.date(), ctx.time());
			onDayEndCalled++;
		}
	}
	
	public static int onInitCalled = 0;
	public static int onDayBeginCalled = 0;
	public static int onDayEndCalled = 0;
	public static int onEveryMinuteCalled = 0;
	public static int onTimePricesCheckCount = 0;
	
	@CTest.test
	public void test_QuantSession()
	{
		QuantSession qSession = new QuantSession(
				"HistoryTest 2017-01-01 2017-02-03", 
				AccountPool.instance().account("mock001", "18982"), 
				new TestStrategy());
		qSession.run();
		
		CTest.EXPECT_LONG_EQ(onInitCalled, 1);
		CTest.EXPECT_LONG_EQ(onDayBeginCalled, 19);
		CTest.EXPECT_LONG_EQ(onDayEndCalled, 19);
		CTest.EXPECT_LONG_EQ(onEveryMinuteCalled, 19*242);
		CTest.EXPECT_LONG_EQ(onTimePricesCheckCount, 19* (1+242)*242/2 );
	}
	
	public static void main(String[] args) {
		CSystem.start();
		//CLog.config_setTag("TEST", false);
		CTest.ADD_TEST(TestQuantSession.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
