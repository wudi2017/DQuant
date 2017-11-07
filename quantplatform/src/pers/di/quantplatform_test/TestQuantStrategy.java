package pers.di.quantplatform_test;

import java.util.*;

import pers.di.account.*;
import pers.di.account.common.*;
import pers.di.common.*;
import pers.di.dataapi.StockDataApi;
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
			CLog.output("TEST", "TestStrategy.onDayStart %s %s", ctx.date(), ctx.time());
			for(int i=0; i<m_seletctID.size(); i++)
			{
				String stockID = m_seletctID.get(i);
				super.addCurrentDayInterestMinuteDataID(stockID);
			}
		}
		
		public void onHandleBuy(QuantContext ctx)
		{
			// find want create IDs
			List<String> cIntentCreateList = new ArrayList<String>();
			for(int i=0; i<m_seletctID.size(); i++)
			{
				String stockID = m_seletctID.get(i);
				DAStock cDAStock = ctx.pool().get(stockID);

				float fYesterdayClosePrice = cDAStock.dayKLines().lastPrice();
				float fNowPrice = cDAStock.price();
				float fRatio = (fNowPrice - fYesterdayClosePrice)/fYesterdayClosePrice;
				
//							CLog.output("TEST", "TestStrategy.onMinuteData %s %s [%s %.3f]", 
//									ctx.date(), ctx.time(), stockID, fRatio);
				
				if(fRatio < -0.02)
				{
					cIntentCreateList.add(stockID);
				}
			}
			
			List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
			int iRetHoldStockList = ctx.ap().getHoldStockList(cHoldStockList);
			List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
			int iRetBuyCommissionOrderList =  ctx.ap().getCommissionOrderList(cCommissionOrderList);
			
			// remove already hold
			Iterator<String> it = cIntentCreateList.iterator();
			while(it.hasNext()){
			    String curIntentID = it.next();
			    
			    boolean bExitInHoldOrCommit = false;
			    for(int i=0; i<cHoldStockList.size(); i++)
				{
					if(curIntentID.equals(cHoldStockList.get(i).stockID))
					{
						bExitInHoldOrCommit = true;
					}
				}
			    if(!bExitInHoldOrCommit)
			    {
			    	 for(int i=0; i<cCommissionOrderList.size(); i++)
					{
						if(curIntentID.equals(cCommissionOrderList.get(i).stockID))
						{
							bExitInHoldOrCommit = true;
						}
					}
			    }
			    
			    if(bExitInHoldOrCommit){
			        it.remove();
			    }
			}
			
			// filter
			int create_max_count = 3;
			
			int alreadyCount = 0;
			int buyStockCount = 0;
			if(0 == iRetHoldStockList 
					&& 0 == iRetBuyCommissionOrderList)
			{
				for(int i=0;i<cHoldStockList.size();i++)
				{
					HoldStock cHoldStock = cHoldStockList.get(i);
					if(cHoldStock.totalAmount > 0)
					{
						alreadyCount++;
					}
				}
				for(int i=0;i<cCommissionOrderList.size();i++)
				{
					CommissionOrder cCommissionOrder = cCommissionOrderList.get(i);
					if(cCommissionOrder.tranAct == TRANACT.SELL) 
					{
						continue;
					}
					
					boolean bExitInHold = false;
					for(int j=0;j<cHoldStockList.size();j++)
					{
						HoldStock cHoldStock = cHoldStockList.get(j);
						if(cHoldStock.stockID.equals(cCommissionOrder.stockID))
						{
							bExitInHold = true;
							break;
						}
					}
					if(!bExitInHold)
					{
						alreadyCount++;
					}
				}
				buyStockCount = create_max_count - alreadyCount;
				buyStockCount = Math.min(buyStockCount,cIntentCreateList.size());
			}
			
			// calc buy mount 
			for(int i = 0; i< buyStockCount; i++)
			{
				String createID = cIntentCreateList.get(i);

				// 买入量
				CObjectContainer<Double> totalAssets = new CObjectContainer<Double>();
				int iRetTotalAssets = ctx.ap().getTotalAssets(totalAssets);
				CObjectContainer<Double> money = new CObjectContainer<Double>();
				int iRetMoney = ctx.ap().getMoney(money);
				if(0 == iRetTotalAssets && 0 == iRetMoney)
				{
					float fMaxPositionRatio = 0.3333f;
					Double dMaxPositionMoney = totalAssets.get()*fMaxPositionRatio; // 最大买入仓位钱
					Double dMaxMoney = 10000*100.0; // 最大买入钱
					Double buyMoney = Math.min(dMaxMoney, dMaxPositionMoney);
					buyMoney = Math.min(buyMoney, money.get());
					
					float curPrice = ctx.pool().get(createID).price();
					int amount = (int)(buyMoney/curPrice);
					amount = amount/100*100; // 买入整手化
					ctx.ap().pushBuyOrder(createID, amount, curPrice); // 500 12.330769
				}
				else
				{
					CLog.output("TEST", "getTotalAssets failed\n");
				}
			}
		}
		
		public void onHandleSell(QuantContext ctx)
		{
			List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
			int iRetHoldStockList = ctx.ap().getHoldStockList(cHoldStockList);
			
			for(int i=0; i<cHoldStockList.size(); i++)
			{
				HoldStock cHoldStock = cHoldStockList.get(i);
				boolean bSell = false;
				DAStock cDAStock = ctx.pool().get(cHoldStock.stockID);
				float curPrice = cDAStock.price();
				
				// 调查天数控制
				int investigationDays = 0;
				while(true)
				{
					String sIndexDate = cDAStock.dayKLines().get(cDAStock.dayKLines().size()-1-investigationDays).date;
					if(cHoldStock.createDate.compareTo(sIndexDate) <= 0)
					{
						investigationDays++;
					}
					else
					{
						break;
					}
				}
				if(investigationDays >= 3) 
				{
					bSell = true;
				}
				
				// 止盈止损卖
				if(cHoldStock.refProfitRatio() > 0.05 || cHoldStock.refProfitRatio() < -0.05) 
				{
					bSell = true;
				}
				
				if(bSell && cHoldStock.availableAmount > 0)
				{
					ctx.ap().pushSellOrder(cHoldStock.stockID, cHoldStock.availableAmount, curPrice); 
				}
			}
		}

		@Override
		public void onMinuteData(QuantContext ctx) {
			//CLog.output("TEST", "TestStrategy.onMinuteData %s %s", ctx.date(), ctx.time());
			onHandleSell(ctx);
			onHandleBuy(ctx);
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
				if(cDAStock.ID().compareTo("000001") >= 0 && cDAStock.ID().compareTo("000200") <= 0 &&
						cDAStock.dayKLines().lastDate().equals(ctx.date())) {	
					
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
			CLog.output("TEST", "%s", logStr);
			// output acc info
			String accInfo = ctx.ap().dump();
			CLog.output("TEST", "dump account\n%s", accInfo);
			
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
		
		CObjectContainer<Double> totalAssets = new CObjectContainer<Double>();
		int iRetTotalAssets = acc.getTotalAssets(totalAssets);
		CObjectContainer<Double> money = new CObjectContainer<Double>();
		int iRetMoney = acc.getMoney(money);
		CTest.EXPECT_DOUBLE_EQ(totalAssets.get(), 107575.483, 3);
		CTest.EXPECT_DOUBLE_EQ(money.get(), 107575.483, 3);
	}
	
	@CTest.test
	public void test_Detail()
	{
		String stockID = "000060";
		CListObserver<TimePrice> obsTimePriceList = new CListObserver<TimePrice>();
		int errObsTimePriceList = StockDataApi.instance().buildMinTimePriceListObserver(stockID, "2016-03-10", 
				"09:00:00", "15:00:00", obsTimePriceList);
		for(int i=0; i<obsTimePriceList.size(); i++)
		{
			TimePrice cTimePrice = obsTimePriceList.get(i);
			CLog.output("TEST", "%s %.3f", cTimePrice.time, cTimePrice.price);
		}
	}
	
	public static void main(String[] args) {
		CSystem.start();
		CTest.ADD_TEST(TestQuantStrategy.class);
		CTest.RUN_ALL_TESTS("TestQuantStrategy.test_QuantStragety");
		CSystem.stop();
	}
}
