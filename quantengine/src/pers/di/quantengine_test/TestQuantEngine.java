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
		public void onHandleData(QuantContext ctx) {
			
			if(ctx.time().equals("09:30:00"))
			{
				CLog.output("TEST", "onHandler %s %s\n", ctx.date(), ctx.time());
			}
				
			// �������й�Ʊ
			for(int i=0; i<ctx.pool().size(); i++)
			{
				DAStock stock = ctx.pool().get(i);
				//CLog.output("TEST", "stock %s %s\n", stock.ID(), stock.name());
			}
			
			// ����ĳֻ��Ʊ��K��
			DAKLines cKLines = ctx.pool().get("600000").dayKLines();
			for(int i=0; i<cKLines.size(); i++)
			{
				KLine cKLine = cKLines.get(i);
				//CLog.output("TEST", "date %s close %.3f\n", cKLine.date, cKLine.close);
			}
			
			// ����ĳֻ��Ʊĳ�շ�ʱ��
			DATimePrices cTimePrices = ctx.pool().get("600000").timePrices();
			for(int i=0; i<cTimePrices.size(); i++)
			{
				TimePrice cTimePrice = cTimePrices.get(i);
				CLog.output("TEST", "time %s price %.3f\n", cTimePrice.time, cTimePrice.price);
			}
		}
	}
	
	public static void main(String[] args) {
		CSystem.start();
		CLog.config_setTag("QEngine", true);
		
		QuantEngine qE = new QuantEngine();
		//qE.config("TrigerMode", "HistoryTest 2017-01-01 2017-02-01");
		qE.config("TrigerMode", "RealTime");
		qE.run(new MyQuantTest());
		
		CSystem.stop();
	}
}
