package pers.di.marketaccount_test;

import java.util.*;

import pers.di.account.IAccount;
import pers.di.account.AccountController;
import pers.di.account.common.HoldStock;
import pers.di.account.common.TRANACT;
import pers.di.account.detail.*;
import pers.di.common.*;
import pers.di.marketaccount.mock.MockAccountOpe;
import pers.di.marketaccount.real.RealAccountOpe;
import pers.di.marketaccount.real.WrapperTHSApi;
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
		for(int iTest=0; iTest<1; iTest++)
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
	        
	        CThread.msleep(1000);
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
	
	public static class TestAccReplier extends IMarketDealReplier
	{

		@Override
		public void onDeal(TRANACT tranact, String stockID, int amount, double price, double cost) {
			// TODO Auto-generated method stub
			CLog.output("TEST", "TestAccReplier.onDeal tranact:%s stockID:%s amount:%d price:%.3f cost:%.3f" , 
					tranact.toString(), stockID, amount, price, cost);
		}
		
	}
	
	@CTest.test
	public static void test_realaccountope_single()
	{
		WrapperTHSApi.s_bMockFlag = false;
		
		TestAccReplier cTestAccReplier = new TestAccReplier();
		RealAccountOpe cRealAccountOpe = new RealAccountOpe();
		cRealAccountOpe.registerDealReplier(cTestAccReplier);
		cRealAccountOpe.start();

		int iBuy = cRealAccountOpe.postTradeRequest(TRANACT.BUY, "601988", 20000, 2.0f);
		CLog.output("TEST", "testRealAccountOpe.postTradeRequest iBuy(%d)", iBuy);
	
//		int iSell = cRealAccountOpe.postTradeRequest(TRANACT.SELL, "601988", 200, 2.1f);
//		CLog.output("TEST", "testRealAccountOpe.postTradeRequest iBuy(%d)", iSell);
		
		CThread.msleep(1000*60);
		
		cRealAccountOpe.stop();
	}
	
	@CTest.test
	public static void test_mockaccountope_single()
	{
		TestAccReplier cTestAccReplier = new TestAccReplier();
		MockAccountOpe MockAccountOpe = new MockAccountOpe();
		MockAccountOpe.registerDealReplier(cTestAccReplier);
		MockAccountOpe.start();

		int iBuy = MockAccountOpe.postTradeRequest(TRANACT.BUY, "601988", 20000, 2.0f);
		CLog.output("TEST", "test_mockaccountope.postTradeRequest iBuy(%d)", iBuy);
		
//		int iSell = cRealAccountOpe.postTradeRequest(TRANACT.SELL, "601988", 200, 4.1f);
//		CLog.output("TEST", "test_mockaccountope.postTradeRequest iBuy(%d)", iSell);
		
		CThread.msleep(1000*3);
		
		MockAccountOpe.stop();
	}
	
	@CTest.test
	public static void testRealAccountOpe_withAccDriver()
	{
		AccountController cAccountController = new AccountController(s_accountDataRoot);
		cAccountController.load("real001" ,  new RealAccountOpe(), true);
		cAccountController.reset(10*10000f);
		cAccountController.start();
		
		IAccount acc = cAccountController.account();
		acc.postTradeOrder(TRANACT.BUY, "601988", 200, 4.0f);
		
		CThread.msleep(1000*60*5);
		cAccountController.stop();
	}

	@CTest.test
	public static void test_mockaccountope_withAccDriver()
	{
		AccountController cAccountController = new AccountController(s_accountDataRoot);
		cAccountController.load("mock001" ,  new MockAccountOpe(), true);
		cAccountController.reset(10*10000f);
		cAccountController.start();
		
		IAccount acc = cAccountController.account();
		
		// buy
		cAccountController.setDateTime("2016-12-02", "14:55:02");
		cAccountController.newDayBegin();
		acc.postTradeOrder(TRANACT.BUY, "300348", 2000, 28.931f);
		cAccountController.newDayEnd();

		// sell
		cAccountController.setDateTime("2016-12-06", "14:33:29");
		acc.postTradeOrder(TRANACT.SELL, "300348", 2000, 28.301f);
		cAccountController.newDayEnd();
		
		CThread.msleep(1000*3);
		cAccountController.stop();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSystem.start();
		CTest.ADD_TEST(TestRealAccountOpe.class);
		CTest.RUN_ALL_TESTS("TestRealAccountOpe.test_realaccountope_single");
		CSystem.stop();
	}
}
