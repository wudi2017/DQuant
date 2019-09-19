package pers.di.account;

import java.util.ArrayList;
import java.util.List;

import pers.di.account.common.*;
import pers.di.common.*;

public abstract class IAccount {
	
	public static interface ICallback
	{
		public enum CALLBACKTYPE
		{
			INVALID,
			CHANGED,
		}
		abstract public void onNotify(CALLBACKTYPE cb);
	}
	
	public abstract boolean aceessLock();
	public abstract boolean aceessUnLock();

	public abstract String ID();
	public abstract String date();
	public abstract String time();
	
	public abstract int getMoney(CObjectContainer<Double> ctnMoney);
	
	public abstract int postTradeOrder(TRANACT tranact, String stockID, int amount, double price);
	
	public abstract int getCommissionOrderList(List<CommissionOrder> ctnList);
	
	public abstract int getDealOrderList(List<DealOrder> ctnList);
	
	public abstract int getHoldStockList(List<HoldStock> ctnList);
	
	public abstract void registerCallback(ICallback cb);
	
	
	/*************************************************************************************/
	
	public int getTotalAssets(CObjectContainer<Double> ctnTotalAssets)
	{
		this.aceessLock();
		
		int iRet = -1;
		
		CObjectContainer<Double> ctnMoney = new CObjectContainer<Double>();
		int iRetGetMoney = getMoney(ctnMoney);
		
		List<HoldStock> ctnHoldStockList = new ArrayList<HoldStock>();
		int iRetGetHoldList = getHoldStockList(ctnHoldStockList);
		
		if(0==iRetGetMoney && 0==iRetGetHoldList)
		{
			Double dTotalAssets = ctnMoney.get();
			for(int i=0; i<ctnHoldStockList.size(); i++)
			{
				dTotalAssets += ctnHoldStockList.get(i).totalAmount*ctnHoldStockList.get(i).curPrice;
			}
			ctnTotalAssets.set(dTotalAssets);
			iRet = 0;
		}
		
		this.aceessUnLock();
		
		return iRet;
	}
	
	public int getTotalStockMarketValue(CObjectContainer<Double> ctnTotalStockMarketValue)
	{
		this.aceessLock();
		
		int iRet = -1;

		List<HoldStock> ctnHoldStockList = new ArrayList<HoldStock>();
		int iRetGetHoldList = getHoldStockList(ctnHoldStockList);
		
		if(0==iRetGetHoldList)
		{
			Double dTotalStockMarketValue = 0.0;
			for(int i=0; i<ctnHoldStockList.size(); i++)
			{
				dTotalStockMarketValue += ctnHoldStockList.get(i).totalAmount*ctnHoldStockList.get(i).curPrice;
			}
			ctnTotalStockMarketValue.set(dTotalStockMarketValue);
			iRet = 0;
		}
		
		this.aceessUnLock();
		
		return iRet;
	}
	
	public int getHoldStock(String stockID, CObjectContainer<HoldStock> ctnHoldStock)
	{
		this.aceessLock();
		
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
		
		this.aceessUnLock();
		return 0;
	}
	
	public String dump()
	{
		this.aceessLock();
		
		String DumpInfo = String.format("---ACCOUNT---INFO--- %s %s", date(), time());
		
		CObjectContainer<Double> totalAssets = new CObjectContainer<Double>();
		this.getTotalAssets(totalAssets);
		CObjectContainer<Double> money = new CObjectContainer<Double>();
		this.getMoney(money);
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		this.getHoldStockList(cHoldStockList);
		List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
		this.getCommissionOrderList(cCommissionOrderList);
		List<DealOrder> cDealOrderList = new ArrayList<DealOrder>();
		this.getDealOrderList(cDealOrderList);
		
		// ��ӡ�ʲ�
		DumpInfo+=String.format("\n    -TotalAssets: %.3f", totalAssets.get());
		DumpInfo+=String.format("\n    -Money: %.3f", money.get());
		double fStockMarketValue = 0.0;
		for(int i=0; i<cHoldStockList.size(); i++ )
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			fStockMarketValue = fStockMarketValue + cHoldStock.totalAmount*cHoldStock.curPrice;
		}
		DumpInfo+=String.format("\n    -StockMarketValue: %.3f", fStockMarketValue);

		// ��ӡ�ֹ�
		for(int i=0; i<cHoldStockList.size(); i++ )
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			DumpInfo+=String.format("\n    -HoldStock: %s %d %d %.3f %.3f %.3f", 
					cHoldStock.stockID,
					cHoldStock.totalAmount, cHoldStock.availableAmount,
					cHoldStock.refPrimeCostPrice, cHoldStock.curPrice, 
					cHoldStock.totalAmount*cHoldStock.curPrice);
		}
				
		// ��ӡί�е�
		for(int i=0; i<cCommissionOrderList.size(); i++ )
		{
			CommissionOrder cCommissionOrder = cCommissionOrderList.get(i);
			String tranOpe = "BUY"; 
			if(cCommissionOrder.tranAct == TRANACT.SELL ) tranOpe = "SELL";
				
			DumpInfo+=String.format("\n    -CommissionOrder: %s %s %s %s %d %.3f", 
					cCommissionOrder.date, cCommissionOrder.time, tranOpe, cCommissionOrder.stockID, 
					cCommissionOrder.amount, cCommissionOrder.price);
		}
		
		// ��ӡ�ɽ���
		for(int i=0; i<cDealOrderList.size(); i++ )
		{
			DealOrder cDealOrder = cDealOrderList.get(i);
			String tranOpe = ""; 
			if(cDealOrder.tranAct == TRANACT.BUY ) 
			{
				tranOpe = "BUY";
				DumpInfo+=String.format("\n    -DealOrder: %s %s %s %s %d %.3f (BC%.3f)", 
						cDealOrder.date, cDealOrder.time, tranOpe, cDealOrder.stockID, 
						cDealOrder.amount, cDealOrder.price, cDealOrder.cost);
			}
			else if(cDealOrder.tranAct == TRANACT.SELL)
			{
				tranOpe = "SELL";
				DumpInfo+=String.format("\n    -DealOrder: %s %s %s %s %d %.3f (SC-%.3f)", 
						cDealOrder.date, cDealOrder.time, tranOpe, cDealOrder.stockID, 
						cDealOrder.amount, cDealOrder.price, cDealOrder.cost);
			}
		}
		
		this.aceessUnLock();
		
		return DumpInfo;
	}
}