package pers.di.marketaccount.real;

import java.util.*;

import pers.di.accountengine.IMarketAccountOpe;
import pers.di.accountengine.common.ACCOUNTTYPE;
import pers.di.accountengine.common.HoldStock;
import pers.di.common.*;
import pers.di.thsapi.*;
import pers.di.thsapi.THSApi.*;

public class RealAccountOpe extends IMarketAccountOpe {

	public RealAccountOpe()
	{
	}
	
	@Override
	public ACCOUNTTYPE type()
	{
		return ACCOUNTTYPE.REAL;
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
	public int newDayInit() {
		int iInitRet = THSApi.initialize();
		CLog.output("ACCOUNT", " @RealAccountOpe newDayInit err(%d)\n", 
				iInitRet);
		return iInitRet;
	}

	@Override
	public int newDayTranEnd() {
		// do nothing
		return 0;
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
	public int getMoney(CObjectContainer<Float> ctnMoney) {
		
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