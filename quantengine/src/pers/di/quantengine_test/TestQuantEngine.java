package pers.di.quantengine_test;

import pers.di.common.CLog;
import pers.di.dataengine.common.*;
import pers.di.quantengine.*;
import pers.di.quantengine.dataaccessor.*;
import pers.di.common.*;

public class TestQuantEngine {

	public static class MyQuantTest extends QuantTriger
	{
		@Override
		public void onHandler(QuantContext ctx) {
			// TODO Auto-generated method stub
			CLog.output("TEST", "onHandler %s %s\n", ctx.date, ctx.time);
			
			// 遍历所有股票
			for(int i=0; i<ctx.pool.size(); i++)
			{
				DAStock stock = ctx.pool.get(i);
				CLog.output("TEST", "stock %s %s\n", stock.ID(), stock.name());
			}
			
			// 遍历某只股票日K线
			DAKLines cKLines = ctx.pool.get("600000").dayKLines();
			for(int i=0; i<cKLines.size(); i++)
			{
				KLine cKLine = cKLines.get(i);
				CLog.output("TEST", "date %s close %.3f\n", cKLine.date, cKLine.close);
			}
			
			// 遍历某只股票某日分时线
			DATimePrices cTimePrices = ctx.pool.get("600000").timePrices(ctx.date);
			for(int i=0; i<cTimePrices.size(); i++)
			{
				TimePrice cTimePrice = cTimePrices.get(i);
				CLog.output("TEST", "time %s price %.3f\n", cTimePrice.time, cTimePrice.price);
			}
			
		}
	}
	
	public static void main(String[] args) {
		CLog.output("TEST", "TestQuantEngine\n");
		QuantEngine qE = new QuantEngine();
		qE.config("TrigerMode", "HistoryTest 2017-01-01 2017-01-03");
		// qE.config("TrigerMode", "RealTime");
		qE.run(new MyQuantTest());
	}
}
