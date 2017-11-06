package pers.di.quantplatform_test;

import java.util.*;

import pers.di.account.*;
import pers.di.account.common.*;
import pers.di.common.*;
import pers.di.dataapi.common.*;
import pers.di.dataapi_test.TestCommonHelper;
import pers.di.dataengine.*;
import pers.di.quantplatform.*;

public class TestQuantStrategy {
	
	public static String s_accountDataRoot = CSystem.getRWRoot() + "\\account";
	public static float s_transactionCostsRatioBuy = 0.0016f;
	public static float s_transactionCostsRatioSell = 0.0f;
	
	public static class MockMarketOpe extends IMarketOpe
	{
		@Override
		public int postTradeRequest(TRANACT tranact, String id, int amount, float price) {
			if(tranact == TRANACT.BUY)
			{
				super.dealReply(tranact, id, amount, price, amount*price*s_transactionCostsRatioBuy);
			}
			else if(tranact == TRANACT.SELL)
			{
				super.dealReply(tranact, id, amount, price, amount*price*s_transactionCostsRatioSell);
			}
			return 0;
		}
	}
	
	/*
	 * SelectResultWrapper类，用于选股优先级排序
	 */
	static private class SelectResult {
		
		// 优先级从大到小排序
		static public class SelectResultCompare implements Comparator 
		{
			public int compare(Object object1, Object object2) {
				SelectResult c1 = (SelectResult)object1;
				SelectResult c2 = (SelectResult)object2;
				int iCmp = Float.compare(c1.fPriority, c2.fPriority);
				if(iCmp > 0) 
					return -1;
				else if(iCmp < 0) 
					return 1;
				else
					return 0;
			}
		}
		
		public SelectResult(){
			stockID = "";
			fPriority = 0.0f;
		}
		public String stockID;
		public float fPriority;
	}
	
	public static class TestStrategy extends QuantStrategy
	{
		public TestStrategy()
		{
			m_seletctID = new ArrayList<String>();
		}
		
		@Override
		public void onInit(QuantContext ctx) {
			
		}
	
		@Override
		public void onDayStart(QuantContext ctx) {
		}

		@Override
		public void onMinuteData(QuantContext ctx) {
		}

		@Override
		public void onDayFinish(QuantContext ctx) {
			CLog.output("TEST", "TestStrategy.onDayFinish %s %s", ctx.date(), ctx.time());
			
			m_seletctID.clear();
			
			// select strategy
			List<SelectResult> cSelectResultList = new ArrayList<SelectResult>();
			for(int i=0; i<ctx.pool().size(); i++)
			{
				DAStock cDAStock = ctx.pool().get(i);
				
				// stock set 
				if(cDAStock.ID().compareTo("000001") >= 0 && cDAStock.ID().compareTo("000200") <= 0) {	
					
					DAKLines cDAKLines = cDAStock.dayKLines();
					int iSize = cDAKLines.size();
					if(iSize > 4)
					{
						KLine cStockDayCur = cDAKLines.get(iSize-1);
						KLine cStockDayBefore1 = cDAKLines.get(iSize-2);
						KLine cStockDayBefore2 = cDAKLines.get(iSize-3);

						if(cStockDayCur.close < cStockDayCur.open 
								&& cStockDayCur.close < cStockDayBefore1.close
								&& cStockDayBefore1.close < cStockDayBefore1.open
								&& cStockDayBefore1.close < cStockDayBefore2.close
								)
						{
							SelectResult cSelectResult = new SelectResult();
							cSelectResult.stockID = cDAStock.ID();
							cSelectResult.fPriority = cStockDayBefore2.close - cStockDayCur.close;
							cSelectResultList.add(cSelectResult);
						}
					}
					
				}
			}
			Collections.sort(cSelectResultList, new SelectResult.SelectResultCompare());
			
			int maxSelectCnt = 3;
			int iSelectCount = cSelectResultList.size();
			int iAddCount = iSelectCount>maxSelectCnt?maxSelectCnt:iSelectCount;
			for(int i=0; i<iAddCount; i++)
			{
				m_seletctID.add(cSelectResultList.get(i).stockID);
			}
			
			// output Selected log
			String logStr = "";
			logStr += String.format("Selected (%d) [ ", iAddCount);
			if(iAddCount == 0) logStr += "null ";
			for(int i=0; i< iAddCount; i++)
			{
				String stockId = m_seletctID.get(i);
				logStr += String.format("%s ", stockId);
				if (i >= 7 && m_seletctID.size()-1 > 8) {
					logStr += String.format("... ", stockId);
					break;
				}
			}
			logStr += String.format("]");
			CLog.output("TEST", "%s\n", logStr);
		}
		
		private List<String> m_seletctID;
	}
	
	@CTest.test
	public void test_QuantStragety()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver(s_accountDataRoot);
		if(0 != cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true)
				|| 0 != cAccoutDriver.reset(10*10000f))
		{
			CLog.error("TEST", "SampleTestStrategy AccoutDriver ERR!");
		}
		Account acc = cAccoutDriver.account();
		
		QuantSession qSession = new QuantSession(
				"HistoryTest 2016-03-01 2016-04-01", 
				cAccoutDriver, 
				new TestStrategy());
		qSession.run();
	}
	
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(TestQuantStrategy.class);
		CTest.RUN_ALL_TESTS();
		CSystem.stop();
	}
}
