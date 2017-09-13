package pers.di.quantengine_test;

import pers.di.common.CLog;
import pers.di.dataengine.common.*;
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

public class TestQuantEngine {
	
	public static class MyQuantTest extends QuantTriger
	{
		@Override
		public void onHandleData(QuantContext ctx) {
			
			if(ctx.time().equals("09:30:00"))
			{
				CLog.output("TEST", "onHandler %s %s\n", ctx.date(), ctx.time());
			}
				
			// 遍历所有股票
			for(int i=0; i<ctx.pool().size(); i++)
			{
				DAStock stock = ctx.pool().get(i);
				//CLog.output("TEST", "stock %s %s\n", stock.ID(), stock.name());
			}
			
			String StockID = "600000";
			
			// 遍历某只股票日K线
			DAKLines cKLines = ctx.pool().get(StockID).dayKLines();
			for(int i=0; i<cKLines.size(); i++)
			{
				KLine cKLine = cKLines.get(i);
				//CLog.output("TEST", "date %s close %.3f\n", cKLine.date, cKLine.close);
			}
			
			// 遍历某只股票某日分时线
			DATimePrices cTimePrices = ctx.pool().get(StockID).timePrices();
			for(int i=0; i<cTimePrices.size(); i++)
			{
				TimePrice cTimePrice = cTimePrices.get(i);
//				CLog.output("TEST", "stockID:%s date %s time %s price %.3f\n", 
//						StockID, ctx.date(), cTimePrice.time, cTimePrice.price);
			}
		}
	}
	
	public static void main(String[] args) {
		CSystem.start();
		//CLog.config_setTag("QEngine", true);
		
		QuantEngine qE = new QuantEngine();
		qE.config("TrigerMode", "HistoryTest 2017-01-01 2017-01-20");
		//qE.config("TrigerMode", "RealTime");
		qE.run(new MyQuantTest());
		
		CSystem.stop();
	}
}
