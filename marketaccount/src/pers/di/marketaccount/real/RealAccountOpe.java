package pers.di.marketaccount.real;

import java.util.*;

import pers.di.account.IMarketAccountOpe;
import pers.di.account.common.HoldStock;
import pers.di.common.*;
import pers.di.thsapi.*;
import pers.di.thsapi.THSApi.*;

public class RealAccountOpe extends IMarketAccountOpe {

	public RealAccountOpe()
	{
	}
	
	@Override
	public int pushBuyOrder(String id, int amount, float price) {
		int iBuyRet = THSApi.buyStock(id, amount, price);
		CLog.output("ACCOUNT", " @RealAccountOpe pushBuyOrder err(%d) [%s %d %.3f %.3f] \n", 
				iBuyRet,
				id, amount, price, amount*price);
		return iBuyRet;
	}

	@Override
	public int pushSellOrder(String id, int amount, float price) {
		int iSellRet = THSApi.sellStock(id, amount, price);
		CLog.output("ACCOUNT", " @RealAccountOpe pushSellOrder err(%d) [%s %d %.3f %.3f] \n", 
				iSellRet,
				id, amount, price, amount*price);
		return 0;
	}

	@Override
	public int getAvailableMoney(CObjectContainer<Float> ctnAvailableMoney) {
		
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
	public int getHoldStockList(List<HoldStock> out_list) {
		
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
	
	public int initialize(String accountID, String password)
	{
		int iInitRet = THSApi.initialize();
		CLog.output("ACCOUNT", " @RealAccountOpe initialize(%d)\n", iInitRet);
		return 0;
	}
}