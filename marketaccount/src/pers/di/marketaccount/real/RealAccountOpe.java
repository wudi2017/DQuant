package pers.di.marketaccount.real;

import java.util.*;

import pers.di.account.IMarketOpe;
import pers.di.account.common.HoldStock;
import pers.di.account.common.TRANACT;
import pers.di.common.*;
import pers.di.thsapi.*;
import pers.di.thsapi.THSApi.*;

public class RealAccountOpe extends IMarketOpe {

	public RealAccountOpe()
	{
		m_THSApiInitFlag = THSApi.initialize();
	}
	
	@Override
	public int postTradeRequest(TRANACT tranact, String id, int amount, double price) {
		
		if(0 != m_THSApiInitFlag)
		{
			return -1;
		}
		
		// begin get commission before
		List<THSApi.CommissionOrder> commissionOrdersBefore = new ArrayList<THSApi.CommissionOrder>();
        int ret_getCommission_before = THSApi.getCommissionOrderList(commissionOrdersBefore);
        if(0 != ret_getCommission_before)
        {
        	return -1;
        }
        for(int i=0;i<commissionOrdersBefore.size();i++)
        {
        	THSApi.CommissionOrder cCommissionOrder = commissionOrdersBefore.get(i);
        	CLog.output("TEST", "Before    {%s %s %s %d %.3f %d %.3f}", cCommissionOrder.time, cCommissionOrder.stockID, cCommissionOrder.tranAct.toString(), 
        			cCommissionOrder.commissionAmount, cCommissionOrder.commissionPrice,
        			cCommissionOrder.dealAmount, cCommissionOrder.dealPrice);
        }
        // end get commission before
        
		
		if(tranact.equals(TRANACT.BUY))
		{
			int iBuyRet = THSApi.buyStock(id, amount, (float)price);
			CLog.output("ACCOUNT", " @RealAccountOpe pushBuyOrder err(%d) [%s %d %.3f %.3f] \n", 
					iBuyRet,
					id, amount, price, amount*price);
			
			
		}
		if(tranact.equals(TRANACT.SELL))
		{
			int iSellRet = THSApi.sellStock(id, amount, (float)price);
			CLog.output("ACCOUNT", " @RealAccountOpe pushSellOrder err(%d) [%s %d %.3f %.3f] \n", 
					iSellRet,
					id, amount, price, amount*price);
		}
		
		// begin get commission after
		List<THSApi.CommissionOrder> commissionOrdersAfter = new ArrayList<THSApi.CommissionOrder>();
        int ret_getCommission_after = THSApi.getCommissionOrderList(commissionOrdersAfter);
        if(0 != ret_getCommission_after)
        {
        	return -1;
        }
        for(int i=0;i<commissionOrdersAfter.size();i++)
        {
        	THSApi.CommissionOrder cCommissionOrder = commissionOrdersBefore.get(i);
        	CLog.output("TEST", "After    {%s %s %s %d %.3f %d %.3f}", cCommissionOrder.time, cCommissionOrder.stockID, cCommissionOrder.tranAct.toString(), 
        			cCommissionOrder.commissionAmount, cCommissionOrder.commissionPrice,
        			cCommissionOrder.dealAmount, cCommissionOrder.dealPrice);
        }
        // end get commission after
        
        super.dealReply(tranact, id, amount, price, 0.0f);
        
		return -1;
	}
	
	private int m_THSApiInitFlag;
}