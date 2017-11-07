package pers.di.account;

import java.util.ArrayList;
import java.util.List;

import pers.di.account.common.*;
import pers.di.common.*;

public abstract class Account {

	public abstract String ID();
	public abstract String date();
	public abstract String time();
	
	public abstract int getMoney(CObjectContainer<Float> ctnMoney);
	
	public abstract int postTradeOrder(TRANACT tranact, String stockID, int amount, float price);
	
	public abstract int getCommissionOrderList(List<CommissionOrder> ctnList);
	
	public abstract int getHoldStockList(List<HoldStock> ctnList);
	
	
	/*************************************************************************************/
	
	public int getTotalAssets(CObjectContainer<Float> ctnTotalAssets)
	{
		int iRet = -1;
		
		CObjectContainer<Float> ctnMoney = new CObjectContainer<Float>();
		int iRetGetMoney = getMoney(ctnMoney);
		
		List<HoldStock> ctnHoldStockList = new ArrayList<HoldStock>();
		int iRetGetHoldList = getHoldStockList(ctnHoldStockList);
		
		if(0==iRetGetMoney && 0==iRetGetHoldList)
		{
			float fTotalAssets = ctnMoney.get();
			for(int i=0; i<ctnHoldStockList.size(); i++)
			{
				fTotalAssets += ctnHoldStockList.get(i).totalAmount*ctnHoldStockList.get(i).curPrice;
			}
			ctnTotalAssets.set(fTotalAssets);
			iRet = 0;
		}
		
		return iRet;
	}
	
	public int getHoldStock(String stockID, CObjectContainer<HoldStock> ctnHoldStock)
	{
		List<HoldStock> ctnList = new ArrayList<HoldStock>();
		int iRet = getHoldStockList(ctnList);
		if(0 == iRet)
		{
			for(int i=0; i<ctnList.size(); i++)
			{
				if(ctnList.get(i).stockID.equals(stockID))
				{
					ctnHoldStock.set(ctnList.get(i));
				}
			}
		}
		return 0;
	}
	
	public String dump()
	{
		String DumpInfo = String.format("---ACCOUNT---INFO--- %s %s\n", date(), time());
		
		CObjectContainer<Float> totalAssets = new CObjectContainer<Float>();
		this.getTotalAssets(totalAssets);
		CObjectContainer<Float> money = new CObjectContainer<Float>();
		this.getMoney(money);
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		this.getHoldStockList(cHoldStockList);
		List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
		this.getCommissionOrderList(cCommissionOrderList);
		
		// 打印资产
		DumpInfo+=String.format("    -TotalAssets: %.3f\n", totalAssets.get());
		DumpInfo+=String.format("    -Money: %.3f\n", money.get());
		float fStockMarketValue = 0.0f;
		for(int i=0; i<cHoldStockList.size(); i++ )
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			fStockMarketValue = fStockMarketValue + cHoldStock.totalAmount * cHoldStock.curPrice;
		}
		DumpInfo+=String.format("    -StockMarketValue: %.3f\n", fStockMarketValue);

		// 打印持股
		for(int i=0; i<cHoldStockList.size(); i++ )
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			DumpInfo+=String.format("    -HoldStock: %s %d %d %.3f %.3f %.3f\n", 
					cHoldStock.stockID,
					cHoldStock.totalAmount, cHoldStock.availableAmount,
					cHoldStock.refPrimeCostPrice, cHoldStock.curPrice, cHoldStock.totalAmount*cHoldStock.curPrice);
		}
				
		// 打印委托单
		for(int i=0; i<cCommissionOrderList.size(); i++ )
		{
			CommissionOrder cCommissionOrder = cCommissionOrderList.get(i);
			String tranOpe = "BUY"; 
			if(cCommissionOrder.tranAct == TRANACT.SELL ) tranOpe = "SELL";
				
			DumpInfo+=String.format("    -CommissionOrder: %s %s %s %d %.3f\n", 
					cCommissionOrder.time, tranOpe, cCommissionOrder.stockID, 
					cCommissionOrder.amount, cCommissionOrder.price);
		}
		
		return DumpInfo;
	}
}
