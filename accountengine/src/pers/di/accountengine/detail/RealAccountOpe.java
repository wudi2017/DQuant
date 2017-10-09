package pers.di.accountengine.detail;

import java.util.*;

import pers.di.accountengine.common.ACCOUNTTYPE;
import pers.di.accountengine.common.HoldStock;
import pers.di.common.*;
import pers.di.thsapi.*;
import pers.di.thsapi.THSApi.*;

public class RealAccountOpe extends IAccountOpe {

	public RealAccountOpe()
	{
		int iInitRet = THSApi.initialize();
		CLog.output("ACCOUNT", " @RealAccountOpe Constructerr(%d)\n", iInitRet);
	}
	
	@Override
	public String ID()
	{
		return "THSAccount";
	}
	
	@Override
	public String password()
	{
		return "defaultPassword";
	}
	
	@Override
	public ACCOUNTTYPE type()
	{
		return ACCOUNTTYPE.REAL;
	}
	
	@Override
	public int newDayInit(String date, String time) {
		int iInitRet = THSApi.initialize();
		CLog.output("ACCOUNT", " @RealAccountOpe newDayInit err(%d) [%s %s]\n", 
				iInitRet,
				date, time);
		return iInitRet;
	}

	@Override
	public int newDayTranEnd(String date, String time) {
		// do nothing
		return 0;
	}
	
	@Override
	public int pushBuyOrder(String date, String time, String id, int amount, float price) {
		int iBuyRet = THSApi.buyStock(id, amount, price);
		CLog.output("ACCOUNT", " @RealAccountOpe pushBuyOrder err(%d) [%s %s] [%s %d %.3f %.3f] \n", 
				iBuyRet, 
				date, time,
				id, amount, price, amount*price);
		return iBuyRet;
	}

	@Override
	public int pushSellOrder(String date, String time, String id, int amount, float price) {
		int iSellRet = THSApi.sellStock(id, amount, price);
		CLog.output("ACCOUNT", " @RealAccountOpe pushSellOrder err(%d) [%s %s] [%s %d %.3f %.3f] \n", 
				iSellRet, 
				date, time,
				id, amount, price, amount*price);
		return 0;
	}

	@Override
	public int getAvailableMoney(String date, String time, CObjectContainer<Float> ctnAvailableMoney) {
		
		ObjectContainer<Float> container = new ObjectContainer<Float>();
        int ret = THSApi.getAvailableMoney(container);
		CLog.output("ACCOUNT", " @RealAccountOpe getAvailableMoney err(%d) availableMoney(%.3f) \n", 
				ret, container.get());
		if(0 == ret)
		{
			ctnAvailableMoney.set(container.get());
		}
		
		return ret;
	}
	
	@Override
	public int getMoney(String date, String time, CObjectContainer<Float> ctnMoney) {
		
		List<pers.di.thsapi.THSApi.HoldStock> containerHoldStock = new ArrayList<pers.di.thsapi.THSApi.HoldStock>();
        int retHoldStock = THSApi.getHoldStockList(containerHoldStock);
		CLog.output("ACCOUNT", " @RealAccountOpe getHoldStockList err(%d) HoldStockList size(%d) \n", 
				retHoldStock, containerHoldStock.size());
		
		ObjectContainer<Float> containerTotalAssets = new ObjectContainer<Float>();
        int retTotalAssets =  THSApi.getTotalAssets(containerTotalAssets);
		CLog.output("ACCOUNT", " @RealAccountOpe getTotalAssets err(%d) TotalAssets(%.3f) \n", 
				retTotalAssets, containerTotalAssets.get());
		
		Float out_totalAssets = 0.0f;
		if(0 == retHoldStock && 0 == retTotalAssets)
		{
			out_totalAssets = containerTotalAssets.get();
			for(int i=0;i<containerHoldStock.size();i++)
	        {
				pers.di.thsapi.THSApi.HoldStock cHoldStock = containerHoldStock.get(i);
				out_totalAssets = out_totalAssets - cHoldStock.totalAmount * cHoldStock.curPrice;
	        }
			
			ctnMoney.set(out_totalAssets);
			return 0;
		}
		else
		{
			return -98;
		}
	}
	
	@Override
	public int getHoldStockList(String date, String time, List<HoldStock> out_list) {
		
		out_list.clear();
		
		List<pers.di.thsapi.THSApi.HoldStock> containerHoldStock = new ArrayList<pers.di.thsapi.THSApi.HoldStock>();
        int retHoldStock = THSApi.getHoldStockList(containerHoldStock);
        
		CLog.output("ACCOUNT", " @RealAccountOpe getHoldStockList err(%d) HoldStockList size(%d) \n", 
				retHoldStock, containerHoldStock.size());
		
        for(int i=0;i<containerHoldStock.size();i++)
        {
        	pers.di.thsapi.THSApi.HoldStock cHoldStock = containerHoldStock.get(i);
        	
        	HoldStock cNewItem = new HoldStock();
        	cNewItem.stockID = cHoldStock.stockID;
        	cNewItem.totalAmount = cHoldStock.totalAmount;
        	cNewItem.availableAmount = cHoldStock.availableAmount;
        	cNewItem.refPrimeCostPrice = cHoldStock.refPrimeCostPrice;
        	cNewItem.curPrice = cHoldStock.curPrice;
        	cNewItem.investigationDays = 0;

			out_list.add(cNewItem);
        }
	        
		return retHoldStock;
		
	}
}