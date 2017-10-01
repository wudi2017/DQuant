package pers.di.quantengine_test;

import pers.di.common.CLog;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.TimePrice;
import pers.di.dataapi.common.*;
import pers.di.quantengine.*;
import pers.di.quantengine.dataaccessor.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import pers.di.common.*;

public class SampleQuantEngine {
	
	public static class MyQuantTest extends QuantTriger
	{
		@Override
		public void onDayBegin(QuantContext ctx) {
			CLog.output("TEST", "onDayBegin %s %s", ctx.date(), ctx.time());
			ctx.subscribeMinuteData("600000");
		}
		
		@Override
		public void onEveryMinute(QuantContext ctx) {
			
			CLog.output("TEST", "onEveryMinute %s %s", ctx.date(), ctx.time());
			
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
			DATimePrices cTimePrices = ctx.pool().get(StockID).timePrices();
			for(int i=0; i<cTimePrices.size(); i++)
			{
				TimePrice cTimePrice = cTimePrices.get(i);
				//CLog.output("TEST", "%s %s %s %.3f", StockID, ctx.date(), cTimePrice.time, cTimePrice.price);
			}
		}

		@Override
		public void onDayEnd(QuantContext ctx) {
			CLog.output("TEST", "onDayEnd %s %s", ctx.date(), ctx.time());
		}
	}
	
	public static void main(String[] args) {
		CSystem.start();
		//CLog.config_setTag("QEngine", true);
		
		QuantEngine qE = new QuantEngine();
		qE.config("TrigerMode", "HistoryTest 2017-01-01 2017-01-03");
		//qE.config("TrigerMode", "RealTime");
		qE.run(new MyQuantTest());
		
		CSystem.stop();
	}
}
