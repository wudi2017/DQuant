package pers.di.quantengine_test;

import pers.di.common.CLog;
import pers.di.quantengine.*;
import pers.di.common.*;

public class TestQuantEngine {

	public static class MyQuantTest extends QuantTriger
	{
		@Override
		public void onHandler(QuantContext ctx) {
			// TODO Auto-generated method stub
			CLog.output("TEST", "onHandler %s\n", ctx.date);
		}
		
	}
	
	public static void main(String[] args) {
		CLog.output("TEST", "TestQuantEngine\n");
		QuantEngine qE = new QuantEngine();
		qE.config();
		qE.run(new MyQuantTest());
	}
}
