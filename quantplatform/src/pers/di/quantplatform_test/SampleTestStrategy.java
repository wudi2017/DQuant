package pers.di.quantplatform_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.accountengine.*;
import pers.di.common.*;
import pers.di.dataapi.common.*;
import pers.di.dataapi_test.TestCommonHelper;
import pers.di.quantplatform.*;
import pers.di.quantplatform.dataaccessor.*;

public class SampleTestStrategy {
	
	public static class TestStrategy extends QuantStrategy
	{
		@Override
		public void onInit(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onInit %s %s", ctx.date(), ctx.time());
		}
	
		@Override
		public void onDayStart(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onDayStart %s %s", ctx.date(), ctx.time());
			super.addCurrentDayInterestMinuteDataID("600001");
		}

		@Override
		public void onMinuteData(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onMinuteData %s %s", ctx.date(), ctx.time());

			// 遍历所有股票
			for(int i=0; i<ctx.pool().size(); i++)
			{
				DAStock stock = ctx.pool().get(i);
				//CLog.output("TEST", "stock %s %s", stock.ID(), stock.name());
			}
			
			String StockID = "600000";
			
			// 遍历某只股票日K线
			DAKLines cKLines = ctx.pool().get(StockID).dayKLines();
			for(int i=0; i<cKLines.size(); i++)
			{
				KLine cKLine = cKLines.get(i);
				//CLog.output("TEST", "date %s close %.3f", cKLine.date, cKLine.close);
			}
			
			// 遍历某只股票某日分时线
			DATimePrices cTimePrices = ctx.pool().get(StockID).timePrices(ctx.date());
			for(int i=0; i<cTimePrices.size(); i++)
			{
				TimePrice cTimePrice = cTimePrices.get(i);
				//CLog.output("TEST", "%s %s %s %.3f", StockID, ctx.date(), cTimePrice.time, cTimePrice.price);
			}
			
			// 调用账户代理发送交易命令
			ctx.ap().pushBuyOrder("", 0, 100);
		}

		@Override
		public void onDayFinish(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onDayFinish %s %s", ctx.date(), ctx.time());
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
		QuantSession qSession = new QuantSession(
				"HistoryTest 2017-01-01 2017-02-03", 
				AccountPool.instance().account("mock001", "18982"), 
				new TestStrategy());
		qSession.run();
	}
	
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(SampleTestStrategy.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
