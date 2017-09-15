package pers.di.quantengine_test;

import pers.di.common.CLog;
import pers.di.common.CSystem;
import pers.di.common.CTest;
import pers.di.dataengine.common.KLine;
import pers.di.dataengine.common.TimePrice;
import pers.di.quantengine.QuantContext;
import pers.di.quantengine.QuantEngine;
import pers.di.quantengine.QuantTriger;
import pers.di.quantengine.dataaccessor.DAKLines;
import pers.di.quantengine.dataaccessor.DAStock;
import pers.di.quantengine.dataaccessor.DATimePrices;

public class TestQuantEngine {
	
	public static class TestTriger extends QuantTriger
	{

		@Override
		public void onDayBegin(QuantContext ctx) {
			//CLog.output("TEST", "onDayBegin %s %s", ctx.date(), ctx.time());
			ctx.subscribeMinuteData("600000");
			onDayBeginCalled++;
		}

		@Override
		public void onEveryMinute(QuantContext ctx) {
			onEveryMinuteCalled++;
			
			//CLog.output("TEST", "onEveryMinute %s %s", ctx.date(), ctx.time());
			
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
		public void onDayEnd(QuantContext ctx) {
			//CLog.output("TEST", "onDayEnd %s %s", ctx.date(), ctx.time());
			onDayEndCalled++;
		}
		
	}
	
	public static int onDayBeginCalled = 0;
	public static int onDayEndCalled = 0;
	public static int onEveryMinuteCalled = 0;
	public static int onTimePricesCheckCount = 0;
	
	@CTest.test
	public static void test_QuantEngine()
	{
		QuantEngine qE = new QuantEngine();
		qE.config("TrigerMode", "HistoryTest 2017-01-01 2017-02-03");
		//qE.config("TrigerMode", "RealTime");
		qE.run(new TestTriger());
		
		CTest.EXPECT_LONG_EQ(onDayBeginCalled, 19);
		CTest.EXPECT_LONG_EQ(onDayEndCalled, 19);
		CTest.EXPECT_LONG_EQ(onEveryMinuteCalled, 19*242);
		CTest.EXPECT_LONG_EQ(onTimePricesCheckCount, 19* (1+242)*242/2 );
	}
	
	
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(TestQuantEngine.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
