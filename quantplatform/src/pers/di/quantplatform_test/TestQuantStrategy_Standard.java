package pers.di.quantplatform_test;

import java.util.*;

import pers.di.account.*;
import pers.di.account.common.*;
import pers.di.common.*;
import pers.di.dataengine.*;
import pers.di.localstock.LocalStock;
import pers.di.localstock.common.*;
import pers.di.quantplatform.*;
import pers.di.quantplatform_test.TestQuantSession_Simple.TestStrategy;

public class TestQuantStrategy_Standard {
	
	public static String s_accountDataRoot = CSystem.getRWRoot() + "\\account";
	public static double s_transactionCostsRatioBuy = 0.0016f;
	public static double s_transactionCostsRatioSell = 0.0f;
	
	public static class MockMarketOpe extends IMarketOpe
	{
		@Override
		public int start()
		{
			return 0;
		}
		
		@Override
		public int stop()
		{
			return 0;
		}
		
		@Override
		public int postTradeRequest(TRANACT tranact, String id, int amount, double price) {
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
				int iCmp = Double.compare(c1.fPriority, c2.fPriority);
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
		public double fPriority;
	}
	
	public static class TestStrategy extends QuantStrategy
	{
		public TestStrategy()
		{
			m_seletctID = new ArrayList<String>();
		}
		
		@Override
		public void onInit(QuantContext context) {
			
		}
		
		@Override
		public void onUnInit(QuantContext context) {
			
		}
	
		@Override
		public void onDayStart(QuantContext context) {
			//CLog.output("TEST", "TestStrategy.onDayStart %s %s", context.date(), context.time());

			// add hold stock to InterestMinuteDataIDs
			context.addCurrentDayInterestMinuteDataIDs(context.accountProxy().getHoldStockIDList());
			// add select to InterestMinuteDataIDs
			context.addCurrentDayInterestMinuteDataIDs(m_seletctID);
		}
		
		public void onHandleBuy(QuantContext context)
		{
			// find want create IDs
			List<String> cIntentCreateList = new ArrayList<String>();
			for(int i=0; i<m_seletctID.size(); i++)
			{
				String stockID = m_seletctID.get(i);
				DAStock cDAStock = context.pool().get(stockID);

				double fYesterdayClosePrice = cDAStock.dayKLines().lastPrice();
				double fNowPrice = cDAStock.price();
				double fRatio = (fNowPrice - fYesterdayClosePrice)/fYesterdayClosePrice;
				
//							CLog.output("TEST", "TestStrategy.onMinuteData %s %s [%s %.3f]", 
//									context.date(), context.time(), stockID, fRatio);
				
				if(fRatio < -0.02)
				{
					cIntentCreateList.add(stockID); // 当天跌幅2%后买入
				}
			}
			
			List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
			int iRetHoldStockList = context.accountProxy().getHoldStockList(cHoldStockList);
			List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
			int iRetBuyCommissionOrderList =  context.accountProxy().getCommissionOrderList(cCommissionOrderList);
			
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
				int iRetTotalAssets = context.accountProxy().getTotalAssets(totalAssets);
				CObjectContainer<Double> money = new CObjectContainer<Double>();
				int iRetMoney = context.accountProxy().getMoney(money);
				if(0 == iRetTotalAssets && 0 == iRetMoney)
				{
					double fMaxPositionRatio = 0.3333f;
					Double dMaxPositionMoney = totalAssets.get()*fMaxPositionRatio; // 最大买入仓位钱
					Double dMaxMoney = 10000*100.0; // 最大买入钱
					Double buyMoney = Math.min(dMaxMoney, dMaxPositionMoney);
					buyMoney = Math.min(buyMoney, money.get());
					
					double curPrice = context.pool().get(createID).price();
					int amount = (int)(buyMoney/curPrice);
					amount = amount/100*100; // 买入整手化
					context.accountProxy().pushBuyOrder(createID, amount, curPrice); // 500 12.330769
				}
				else
				{
					CLog.debug("TEST", "getTotalAssets failed\n");
				}
			}
		}
		
		public void onHandleSell(QuantContext context)
		{
			List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
			int iRetHoldStockList = context.accountProxy().getHoldStockList(cHoldStockList);
			
			for(int i=0; i<cHoldStockList.size(); i++)
			{
				HoldStock cHoldStock = cHoldStockList.get(i);
				boolean bSell = false;
				DAStock cDAStock = context.pool().get(cHoldStock.stockID);
				double curPrice = cDAStock.price();
				
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
					context.accountProxy().pushSellOrder(cHoldStock.stockID, cHoldStock.availableAmount, curPrice); 
				}
			}
		}

		@Override
		public void onMinuteData(QuantContext context) {
			//CLog.output("TEST", "TestStrategy.onMinuteData %s %s", context.date(), context.time());
			onHandleSell(context);
			onHandleBuy(context);
		}

		@Override
		public void onDayFinish(QuantContext context) {
			//CLog.output("TEST", "TestStrategy.onDayFinish %s %s", context.date(), context.time());
			
			m_seletctID.clear();
			
			int iCheckCount = 0;
			
			// select strategy
			List<SelectResult> cSelectResultList = new ArrayList<SelectResult>();
			for(int i=0; i<context.pool().size(); i++)
			{
				DAStock cDAStock = context.pool().get(i);
				
				// stock set 
				if(cDAStock.ID().compareTo("600000") >= 0 && cDAStock.ID().compareTo("600100") <= 0 &&
						cDAStock.dayKLines().lastDate().equals(context.date())) {	
					
					DAKLines cDAKLines = cDAStock.dayKLines();
					int iSize = cDAKLines.size();
					if(iSize > 4)
					{
						iCheckCount++;
						
						KLine cStockDayCur = cDAKLines.get(iSize-1);
						KLine cStockDayBefore1 = cDAKLines.get(iSize-2);
						KLine cStockDayBefore2 = cDAKLines.get(iSize-3);

						// 连续两天纯阴线
						if(cStockDayCur.close < cStockDayCur.open //当天是阴线
								&& cStockDayCur.close < cStockDayBefore1.close //当天收盘价低于昨天收盘价
								&& cStockDayBefore1.close < cStockDayBefore1.open // 昨天是阴线
								&& cStockDayBefore1.close < cStockDayBefore2.close // 昨天收盘价小于前天收盘价
								)
						{
							SelectResult cSelectResult = new SelectResult();
							cSelectResult.stockID = cDAStock.ID();
							cSelectResult.fPriority = cStockDayBefore2.close - cStockDayCur.close; // 优先级为跌价格
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
			logStr += String.format("Checked(%d) Selected (%d) [ ", iCheckCount, iAddCount);
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
			CLog.debug("TEST", "%s", logStr);
			// output acc info
			String accInfo = context.accountProxy().dump();
			CLog.debug("TEST", "dump account\n%s", accInfo);
			
//			// test check
//			if(context.date().equals("2016-03-11"))
//			{
//				CObjectContainer<Double> totalAssets = new CObjectContainer<Double>();
//				int iRetTotalAssets = context.accountProxy().getTotalAssets(totalAssets);
//				CObjectContainer<Double> money = new CObjectContainer<Double>();
//				int iRetMoney = context.accountProxy().getMoney(money);
//				CTest.EXPECT_DOUBLE_EQ(totalAssets.get(), 100085.782, 3);
//				CTest.EXPECT_DOUBLE_EQ(money.get(), 3083.782, 3);
//			}
//			if(context.date().equals("2016-03-28"))
//			{
//				CObjectContainer<Double> totalAssets = new CObjectContainer<Double>();
//				int iRetTotalAssets = context.accountProxy().getTotalAssets(totalAssets);
//				CObjectContainer<Double> money = new CObjectContainer<Double>();
//				int iRetMoney = context.accountProxy().getMoney(money);
//				CTest.EXPECT_DOUBLE_EQ(totalAssets.get(), 104522.797, 3);
//				CTest.EXPECT_DOUBLE_EQ(money.get(), 37826.397, 3);
//			}
			
		}
		
		private List<String> m_seletctID;
	}
	
	@CTest.test
	public void test_QuantStragety()
	{
		AccountController cAccountController = new AccountController(s_accountDataRoot);
		if(0 != cAccountController.open("mock001" ,  new MockMarketOpe(), true)
				|| 0 != cAccountController.reset(10*10000f))
		{
			CLog.error("TEST", "SampleTestStrategy AccountController ERR!");
		}
		IAccount acc = cAccountController.account();
		
		Quant.instance().run("HistoryTest 2019-05-01 2019-06-30", cAccountController, new TestStrategy());
		
		CObjectContainer<Double> totalAssets = new CObjectContainer<Double>();
		int iRetTotalAssets = acc.getTotalAssets(totalAssets);
		CObjectContainer<Double> money = new CObjectContainer<Double>();
		int iRetMoney = acc.getMoney(money);
		CTest.EXPECT_DOUBLE_EQ(totalAssets.get(), 107575.481, 3);
		CTest.EXPECT_DOUBLE_EQ(money.get(), 107575.481, 3);
	}
	
	@CTest.test
	public void test_Detail()
	{
		String stockID = "000060";
		CListObserver<TimePrice> obsTimePriceList = new CListObserver<TimePrice>();
		int errObsTimePriceList = LocalStock.instance().buildMinTimePriceListObserver(stockID, "2016-03-10", 
				"09:00:00", "15:00:00", obsTimePriceList);
		for(int i=0; i<obsTimePriceList.size(); i++)
		{
			TimePrice cTimePrice = obsTimePriceList.get(i);
			CLog.debug("TEST", "%s %.3f", cTimePrice.time, cTimePrice.price);
		}
	}
	
	public static void main(String[] args) {
		CSystem.start();
//		CLog.config_setTag("TEST", false);
//		CLog.config_setTag("ACCOUNT", false);
		CTest.ADD_TEST(TestQuantStrategy_Standard.class);
		CTest.RUN_ALL_TESTS("TestQuantStrategy_Standard.test_QuantStragety");
		CSystem.stop();
	}
}
