package pers.di.marketaccount_test;

import java.util.*;

import pers.di.account.Account;
import pers.di.account.AccoutDriver;
import pers.di.account.common.HoldStock;
import pers.di.account.common.TRANACT;
import pers.di.account.detail.*;
import pers.di.account_test.TestAccountDriver.MockMarketOpe;
import pers.di.common.*;
import pers.di.marketaccount.mock.MockAccountOpe;
import pers.di.marketaccount.real.RealAccountOpe;

import pers.di.thsapi.*;

public class TestRealAccountOpe {
	
	public static String s_accountDataRoot = CSystem.getRWRoot() + "\\account";
	
	
	@CTest.test
	public static void testTHSApi()
	{
		Formatter fmt = new Formatter(System.out);
		CLog.output("TEST", "### main begin\n");
		
		// 初始化测试
		{
			int iInitRet = THSApi.initialize();
			CLog.output("TEST", "CATHSAccount.initialize err(%d)", iInitRet);
		}
		        
		// 可用资金测试
		{
			THSApi.ObjectContainer<Float> container = new THSApi.ObjectContainer<Float>();
	        int ret = THSApi.getAvailableMoney(container);
	        CLog.output("TEST", "CATHSAccount.getAvailableMoney err(%d) AvailableMoney(%.2f)", ret, container.get());
		}

		// 总资产测试
		{
			THSApi.ObjectContainer<Float> container = new THSApi.ObjectContainer<Float>();
	        int ret =  THSApi.getTotalAssets(container);
	        CLog.output("TEST", "CATHSAccount.getTotalAssets err(%d) TotalAssets(%.2f)", ret, container.get());
		}

		// 所有股票总市值测试
		{
			THSApi.ObjectContainer<Float> container = new THSApi.ObjectContainer<Float>();
	        int ret =  THSApi.getAllStockMarketValue(container);
	        CLog.output("TEST", "CATHSAccount.getAllStockMarketValue err(%d) AllStockMarketValue(%.2f)", ret, container.get());
		}

		// 持股列表测试
		{
			List<THSApi.HoldStock> container = new ArrayList<THSApi.HoldStock>();
	        int ret = THSApi.getHoldStockList(container);
	        CLog.output("TEST", "CATHSAccount.getHoldStockList err(%d) resultList size(%d)", ret, container.size());
	        for(int i=0;i<container.size();i++)
	        {
	        	THSApi.HoldStock cHoldStock = container.get(i);
	        	CLog.output("TEST", "    {%s %d %d %.3f %.3f %.3f}", cHoldStock.stockID, cHoldStock.totalAmount, cHoldStock.availableAmount,
	        			cHoldStock.refProfitLoss, cHoldStock.refPrimeCostPrice, cHoldStock.curPrice);
	        }
		}
				
		// 委托列表测试
		{
			List<THSApi.CommissionOrder> container = new ArrayList<THSApi.CommissionOrder>();
	        int ret = THSApi.getCommissionOrderList(container);
	        CLog.output("TEST", "CATHSAccount.getCommissionOrderList err(%d) resultList size(%d)", ret, container.size());
	        for(int i=0;i<container.size();i++)
	        {
	        	THSApi.CommissionOrder cCommissionOrder = container.get(i);
	        	CLog.output("TEST", "    {%s %s %s %d %.3f %d %.3f}", cCommissionOrder.time, cCommissionOrder.stockID, cCommissionOrder.tranAct.toString(), 
	        			cCommissionOrder.commissionAmount, cCommissionOrder.commissionPrice,
	        			cCommissionOrder.dealAmount, cCommissionOrder.dealPrice);
	        }
		}
				
		// 成交列表测试
		{
			List<THSApi.DealOrder> container = new ArrayList<THSApi.DealOrder>();
	        int ret = THSApi.getDealOrderList(container);
	        CLog.output("TEST", "CATHSAccount.getDealOrderList err(%d) resultList size(%d)", ret, container.size());
	        for(int i=0;i<container.size();i++)
	        {
	        	THSApi.DealOrder cDealOrder = container.get(i);
	        	CLog.output("TEST", "    {%s %s %s %d %.3f}", cDealOrder.time, cDealOrder.stockID, cDealOrder.tranAct.toString(), 
	        			cDealOrder.dealAmount, cDealOrder.dealPrice);
	        }
		}
		            
		// 买入委托测试
		{
	        // int iBuyRet = THSApi.buyStock("601988", 100, 0.07f);
	        // CLog.output("TEST", "CATHSAccount.buyStock err(%d)", iBuyRet);
		}
        
		// 卖出委托测试
		{
	        // int iSellRet = THSApi.sellStock("601988", 100, 190.7f);
	        // CLog.output("TEST", "CATHSAccount.sellStock err(%d)", iSellRet);
		}
	}
	
	@CTest.test
	public static void testRealAccountOpe()
	{
		RealAccountOpe cRealAccountOpe = new RealAccountOpe();
		
//		int iInit = cRealAccountOpe.newDayInit();
//		CTest.EXPECT_LONG_EQ(0, iInit);
//		
//		int iEnd = cRealAccountOpe.newDayTranEnd();
//		CTest.EXPECT_LONG_EQ(0, iEnd);
		
//		int iBuy = cRealAccountOpe.pushBuyOrder(CUtilsDateTime.GetCurDateStr(), CUtilsDateTime.GetCurTimeStr(), 
//				"601988", 100, 0.99f);
//		CTest.EXPECT_LONG_EQ(0, iBuy);
//		
//		int iSell = cRealAccountOpe.pushBuyOrder(CUtilsDateTime.GetCurDateStr(), CUtilsDateTime.GetCurTimeStr(), 
//				"601988", 100, 0.99f);
//		CTest.EXPECT_LONG_EQ(0, iSell);
		
//		CTHSApi.ObjectContainer<Float> ctnAvailableMoney = new CTHSApi.ObjectContainer<Float>();
//		int iAvailableMoney = cRealAccountOpe.getAvailableMoney(ctnAvailableMoney);
//		CTest.EXPECT_LONG_EQ(0, iAvailableMoney);
		
//		CTHSApi.ObjectContainer<Float> ctnMoney = new CTHSApi.ObjectContainer<Float>();
//		int iMoney = cRealAccountOpe.getMoney(ctnMoney);
//		CTest.EXPECT_LONG_EQ(0, iMoney);
//		CTest.EXPECT_TRUE(ctnMoney.get() > 100.0f);
		
//		List<HoldStock> ctnHoldStock = new ArrayList<HoldStock>();
//		int iHold = cRealAccountOpe.getHoldStockList(ctnHoldStock);
//		CTest.EXPECT_LONG_EQ(0, iHold);
	}
	
	@CTest.test
	public static void test_mockaccountope()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver(s_accountDataRoot);
		cAccoutDriver.load("mock001" ,  new MockAccountOpe(), true);
		cAccoutDriver.reset(10*10000f);
		
		Account acc = cAccoutDriver.account();
		
		// buy
		cAccoutDriver.setDateTime("2016-12-02", "14:55:02");
		cAccoutDriver.newDayBegin();
		acc.postTradeOrder(TRANACT.BUY, "300348", 2000, 28.931f);
		cAccoutDriver.newDayEnd();

		// sell
		cAccoutDriver.setDateTime("2016-12-06", "14:33:29");
		acc.postTradeOrder(TRANACT.SELL, "300348", 2000, 28.301f);
		cAccoutDriver.newDayEnd();
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSystem.start();
		CTest.ADD_TEST(TestRealAccountOpe.class);
		CTest.RUN_ALL_TESTS("TestRealAccountOpe.testTHSApi");
		CSystem.stop();
	}
}
