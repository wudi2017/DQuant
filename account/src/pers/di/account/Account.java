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
}
