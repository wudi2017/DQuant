package pers.di.quantplatform_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.account.*;
import pers.di.account_test.TestAccountDriver.MockMarketOpe;
import pers.di.common.*;
import pers.di.dataapi.common.*;
import pers.di.dataapi_test.TestCommonHelper;
import pers.di.dataengine.*;
import pers.di.quantplatform.*;

public class SampleTestStrategy {
	
	public static String s_accountDataRoot = CSystem.getRWRoot() + "\\account";
	
	public static class TestStrategy extends QuantStrategy
	{
		@Override
		public void onInit(QuantContext ctx) {
			CLog.output("TEST", "TestStrategy.onInit %s %s", ctx.date(), ctx.time());
		}
	
		@Override
		public void onDayStart(QuantContext ctx) {
			CLog.output("TEST", "TestStrategy.onDayStart %s %s", ctx.date(), ctx.time());
			super.addCurrentDayInterestMinuteDataID("600000");
		}

		@Override
		public void onMinuteData(QuantContext ctx) {
			CLog.output("TEST", "TestStrategy.onMinuteData %s %s", ctx.date(), ctx.time());

			// 遍历某只股票某日分时线
			
			if(ctx.time().equals("09:50:00"))
			{
				String StockID = "600000";
				DATimePrices cTimePrices = ctx.pool().get(StockID).timePrices(ctx.date());
				for(int i=0; i<cTimePrices.size(); i++)
				{
					TimePrice cTimePrice = cTimePrices.get(i);
					CLog.output("TEST", "    %s %s %.3f", StockID,cTimePrice.time, cTimePrice.price);
				}
				
				// 调用账户代理发送交易命令
				ctx.ap().pushBuyOrder(StockID, 0, 100);
			}
		}

		@Override
		public void onDayFinish(QuantContext ctx) {
			CLog.output("TEST", "TestStrategy.onDayFinish %s %s", ctx.date(), ctx.time());
			
			// 遍历所有股票
			CLog.output("TEST", "    Traversal All Stocks");
			for(int i=0; i<ctx.pool().size(); i++)
			{
				DAStock stock = ctx.pool().get(i);
				CLog.output("TEST", "        stock %s %s", stock.ID(), stock.name());
			}
			
			// 遍历某只股票日K线
			String StockID = "600000";
			CLog.output("TEST", "    Traversal day KLine : %s", StockID);
			DAKLines cKLines = ctx.pool().get(StockID).dayKLines();
			for(int i=0; i<cKLines.size(); i++)
			{
				if(i > cKLines.size() - 10)
				{
					KLine cKLine = cKLines.get(i);
					CLog.output("TEST", "        date %s close %.3f", cKLine.date, cKLine.close);
				}
			}
		}
	}
	
	@CTest.setup
	public static void init()
	{
		String newestDate = "2017-08-10";
		 List<String> stockIDs = new ArrayList<String>()
			{{add("999999");add("600000");add("300163");add("002468");}};
		TestCommonHelper.InitLocalData(newestDate, stockIDs);
	}
	
	@CTest.test
	public static void Sample()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver(s_accountDataRoot);
		if(0 != cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true)
				|| 0 != cAccoutDriver.reset(10*10000f))
		{
			CLog.error("TEST", "SampleTestStrategy AccoutDriver ERR!");
		}
		
		QuantSession qSession = new QuantSession(
				"HistoryTest 2017-01-01 2017-01-03", 
				cAccoutDriver, 
				new TestStrategy());
		
		QuantSession.run();
	}
	
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(SampleTestStrategy.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
