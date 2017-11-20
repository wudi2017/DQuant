package pers.di.account_test;

import pers.di.account.Account;
import pers.di.account.AccoutDriver;
import pers.di.account.IMarketOpe;
import pers.di.account.common.TRANACT;
import pers.di.common.CLog;
import pers.di.common.CSystem;
import pers.di.common.CTest;

public class TestCommisionDealOrlder {
	
	public static String s_accountDataRoot = CSystem.getRWRoot() + "\\account";
	
	public static class MockMarketOpe extends IMarketOpe
	{
		static int iPostTimes600001BUY=0;
		static int iPostTimes600001SELL=0;
		
		@Override
		public int postTradeRequest(TRANACT tranact, String id, int amount, double price) {
			
			// 600001 
			if(0 == id.compareTo("600001"))
			{
				
				if(TRANACT.BUY == tranact)
				{
					iPostTimes600001BUY++;
					
					// 第10次买600001, 成交400股单价10.55，手续费250
					if(10 == iPostTimes600001BUY)
						dealReply(TRANACT.BUY, "600001", 400, 10.55, 250);
				}
				
				if(TRANACT.SELL == tranact)
				{
					iPostTimes600001SELL++;
					
					// 第3次卖600001，成交300股单价9.85，手续费150
					if(3 == iPostTimes600001SELL)
						dealReply(TRANACT.SELL, "600001", 200, 9.85, 150);
				}
				
			}
			
			// 600002 必然成交
			if(0 == id.compareTo("600002"))
			{
				dealReply(tranact, id, amount, price, amount*price*0.02);
			}
			
			return 0;
		}
	}
	
	@CTest.test
	public static void test_commision_sort()
	{
		AccoutDriver cAccoutDriver = new AccoutDriver(s_accountDataRoot);
		cAccoutDriver.load("mock001" ,  new MockMarketOpe(), true);
		cAccoutDriver.reset(10*10000f);
		
		Account acc = cAccoutDriver.account();
		
		cAccoutDriver.setDateTime("2017-10-10", "14:00:01");
		cAccoutDriver.newDayBegin();
		acc.postTradeOrder(TRANACT.BUY, "600001", 600, 10.6f);
		acc.postTradeOrder(TRANACT.BUY, "600001", 300, 12.1f);
		acc.postTradeOrder(TRANACT.BUY, "600001", 200, 12.8f);
		acc.postTradeOrder(TRANACT.BUY, "600001", 300, 9.89f);
		acc.postTradeOrder(TRANACT.BUY, "600002", 300, 21.18f);
		acc.postTradeOrder(TRANACT.BUY, "600001", 300, 11.89f);
		cAccoutDriver.newDayEnd();
		
		cAccoutDriver.setDateTime("2017-10-11", "13:00:01");
		cAccoutDriver.newDayBegin();
		acc.postTradeOrder(TRANACT.BUY, "600001", 100, 11.5f); // chengjiao 100
		acc.postTradeOrder(TRANACT.BUY, "600001", 200, 12.5f); // chengjiao 200
		acc.postTradeOrder(TRANACT.BUY, "600001", 300, 9.18f);
		acc.postTradeOrder(TRANACT.BUY, "600001", 400, 10.96f);// chengjiao 100
		acc.postTradeOrder(TRANACT.BUY, "600002", 100, 24.88f);
		acc.postTradeOrder(TRANACT.BUY, "600002", 200, 23.05f);
		acc.postTradeOrder(TRANACT.BUY, "600001", 500, 9.89f); // chufa chengjiao
		cAccoutDriver.newDayEnd();
		
		cAccoutDriver.setDateTime("2017-10-12", "14:30:01");
		cAccoutDriver.newDayBegin();
		acc.postTradeOrder(TRANACT.SELL, "600001", 200, 8.68f); // chengjiao 100
		acc.postTradeOrder(TRANACT.SELL, "600001", 100, 10.85f);
		acc.postTradeOrder(TRANACT.SELL, "600001", 100, 8.18f); // chufa maichu, chengjiao 100
		cAccoutDriver.newDayEnd();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSystem.start();
		CTest.ADD_TEST(TestCommisionDealOrlder.class);
		CTest.RUN_ALL_TESTS("TestCommisionDealOrlder.");
		CSystem.stop();
	}
	
}
