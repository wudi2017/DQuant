package pers.di.quantengine_test;

import pers.di.common.CLog;
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
			
			ctx.das.get("600000").dayKLines();
			
			ctx.das.get("600000").timePrices(ctx.date);
			
		}
	}
	
	public static void main(String[] args) {
		CLog.output("TEST", "TestQuantEngine\n");
		QuantEngine qE = new QuantEngine();
		qE.config("TrigerMode", "HistoryTest 2017-01-01 2017-01-03");
		qE.run(new MyQuantTest());
	}
}
