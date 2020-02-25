package pers.di.marketope.real;

import java.util.*;

import pers.di.common.CLog;
import pers.di.thsapi.*;

public class WrapperTHSApi {
	
	public static boolean s_bMockFlag = false;
	
	/**********************************************************************************/
	
	public static int s_iInitialize_calledTimes = 0;
	public static int s_iGetAvailableMoney_calledTimes = 0;
	public static int s_iGetTotalAssets_calledTimes = 0;
	public static int s_iGetAllStockMarketValue_calledTimes = 0;
	public static int s_iGetHoldStockList_calledTimes = 0;
	public static int s_iGetCommissionOrderList_calledTimes = 0;
	public static int s_iGetDealOrderList_calledTimes = 0;
	public static int s_iBuyStock_calledTimes = 0;
	public static int s_iSellStock_calledTimes = 0;
	
	public static int initialize()
	{
		s_iInitialize_calledTimes++;
		if(s_bMockFlag)
		{
			CLog.output("TEST", "WrapperTHSApi.initialize called %d times", s_iInitialize_calledTimes);
			return 0;
		}
		else
		{
			return THSApi.initialize();
		}
	}
	
	public static int getAvailableMoney(THSApi.ObjectContainer<Float> availableMoney)
	{
		s_iGetAvailableMoney_calledTimes++;
		if(s_bMockFlag)
		{
			CLog.output("TEST", "WrapperTHSApi.getAvailableMoney called %d times", s_iGetAvailableMoney_calledTimes);
			return 0;
		}
		else
		{
			return THSApi.getAvailableMoney(availableMoney);
		}
	}
	
	public static int getTotalAssets(THSApi.ObjectContainer<Float> totalAssets)
	{
		s_iGetTotalAssets_calledTimes++;
		if(s_bMockFlag)
		{
			CLog.output("TEST", "WrapperTHSApi.getTotalAssets called %d times", s_iGetTotalAssets_calledTimes);
			if(1 == s_iGetTotalAssets_calledTimes)
			{
				totalAssets.set(100000.0f);
			}
			return 0;
		}
		else
		{
			return THSApi.getTotalAssets(totalAssets);
		}
	}
	
	public static int getAllStockMarketValue(THSApi.ObjectContainer<Float> allStockMarketValue)
	{
		s_iGetAllStockMarketValue_calledTimes++;
		if(s_bMockFlag)
		{
			CLog.output("TEST", "WrapperTHSApi.getAllStockMarketValue called %d times", s_iGetAllStockMarketValue_calledTimes);
			return 0;
		}
		else
		{
			return THSApi.getAllStockMarketValue(allStockMarketValue);
		}
	}
	
	public static int getHoldStockList(List<THSApi.HoldStock> container)
	{
		s_iGetHoldStockList_calledTimes++;
		if(s_bMockFlag)
		{
			CLog.output("TEST", "WrapperTHSApi.getHoldStockList called %d times", s_iGetHoldStockList_calledTimes);
			return 0;
		}
		else
		{
			return THSApi.getHoldStockList(container);
		}
	}
	
	public static int getCommissionOrderList(List<THSApi.CommissionOrder> container)
	{
		s_iGetCommissionOrderList_calledTimes++;
		if(s_bMockFlag)
		{
			CLog.output("TEST", "WrapperTHSApi.getCommissionOrderList called %d times", s_iGetCommissionOrderList_calledTimes);
			
			if(s_iGetCommissionOrderList_calledTimes <= 3)
			{
				THSApi.CommissionOrder c1 = new THSApi.CommissionOrder();
				c1.time = "09:50:00";
				c1.stockID = "600000";
				c1.tranAct = THSApi.TRANACT.BUY;
				c1.commissionAmount = 1000;
				c1.commissionPrice = 1.22f;
				c1.dealAmount = 0;
				c1.dealPrice = 0;
				container.add(c1);
			}
			
			if(s_iGetCommissionOrderList_calledTimes > 3 && s_iGetCommissionOrderList_calledTimes < 5)
			{
				THSApi.CommissionOrder c1 = new THSApi.CommissionOrder();
				c1.time = "09:50:00";
				c1.stockID = "600000";
				c1.tranAct = THSApi.TRANACT.BUY;
				c1.commissionAmount = 1000;
				c1.commissionPrice = 1.22f;
				c1.dealAmount = 500;
				c1.dealPrice = 1.21f;
				container.add(c1);
				
				THSApi.CommissionOrder c2 = new THSApi.CommissionOrder();
				c2.time = "10:50:00";
				c2.stockID = "601988";
				c2.tranAct = THSApi.TRANACT.BUY;
				c2.commissionAmount = 20000;
				c2.commissionPrice = 2.0f;
				c2.dealAmount = 0;
				c2.dealPrice = 0;
				container.add(c2);
			}
			
			if(s_iGetCommissionOrderList_calledTimes > 5 && s_iGetCommissionOrderList_calledTimes < 8)
			{
				THSApi.CommissionOrder c1 = new THSApi.CommissionOrder();
				c1.time = "09:50:00";
				c1.stockID = "600000";
				c1.tranAct = THSApi.TRANACT.BUY;
				c1.commissionAmount = 1000;
				c1.commissionPrice = 1.22f;
				c1.dealAmount = 500;
				c1.dealPrice = 1.21f;
				container.add(c1);
				
				THSApi.CommissionOrder c2 = new THSApi.CommissionOrder();
				c2.time = "10:50:00";
				c2.stockID = "601988";
				c2.tranAct = THSApi.TRANACT.BUY;
				c2.commissionAmount = 20000;
				c2.commissionPrice = 2.0f;
				c2.dealAmount = 4000;
				c2.dealPrice = 1.9f; // deal 400 1.9
				container.add(c2);
			}
			
			if(s_iGetCommissionOrderList_calledTimes > 8 && s_iGetCommissionOrderList_calledTimes < 10)
			{
				THSApi.CommissionOrder c1 = new THSApi.CommissionOrder();
				c1.time = "09:50:00";
				c1.stockID = "600000";
				c1.tranAct = THSApi.TRANACT.BUY;
				c1.commissionAmount = 1000;
				c1.commissionPrice = 1.22f;
				c1.dealAmount = 500;
				c1.dealPrice = 1.21f;
				container.add(c1);
				
				THSApi.CommissionOrder c2 = new THSApi.CommissionOrder();
				c2.time = "10:50:00";
				c2.stockID = "601988";
				c2.tranAct = THSApi.TRANACT.BUY;
				c2.commissionAmount = 20000;
				c2.commissionPrice = 2.0f;
				c2.dealAmount = 8000;
				c2.dealPrice = 1.85f; // deal 4000 1.8
				container.add(c2);
			}
			
			if(s_iGetCommissionOrderList_calledTimes > 10)
			{
				THSApi.CommissionOrder c1 = new THSApi.CommissionOrder();
				c1.time = "09:50:00";
				c1.stockID = "600000";
				c1.tranAct = THSApi.TRANACT.BUY;
				c1.commissionAmount = 100;
				c1.commissionPrice = 1.2f;
				c1.dealAmount = 0;
				c1.dealPrice = 0;
				container.add(c1);
				
				THSApi.CommissionOrder c2 = new THSApi.CommissionOrder();
				c2.time = "10:50:00";
				c2.stockID = "601988";
				c2.tranAct = THSApi.TRANACT.BUY;
				c2.commissionAmount = 1000;
				c2.commissionPrice = 2.0f;
				c2.dealAmount = 20000;
				c2.dealPrice = 1.698f; // deal 12000 1.597
				container.add(c2);
			}
			
			return 0;
		}
		else
		{
			return THSApi.getCommissionOrderList(container);
		}
	}
	
	public static int getDealOrderList(List<THSApi.DealOrder> container)
	{
		s_iGetDealOrderList_calledTimes++;
		if(s_bMockFlag)
		{
			CLog.output("TEST", "WrapperTHSApi.getDealOrderList called %d times", s_iGetDealOrderList_calledTimes);
			return 0;
		}
		else
		{
			return THSApi.getDealOrderList(container);
		}
	}
	
	public static int buyStock(String stockId, int amount, float price)
	{
		s_iBuyStock_calledTimes++;
		if(s_bMockFlag)
		{
			CLog.output("TEST", "WrapperTHSApi.buyStock called %d times", s_iBuyStock_calledTimes);
			return 0;
		}
		else
		{
			return THSApi.buyStock(stockId, amount, price);
		}
	}
	
	public static int sellStock(String stockId, int amount, float price)
	{
		s_iSellStock_calledTimes++;
		if(s_bMockFlag)
		{
			CLog.output("TEST", "WrapperTHSApi.sellStock called %d times", s_iSellStock_calledTimes);
			return 0;
		}
		else
		{
			return THSApi.sellStock(stockId, amount, price);
		}
	}
}
