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
	}
	
	@Override
	public int postTradeRequest(TRANACT tranact, String id, int amount, double price) {
		if(tranact.equals(TRANACT.BUY))
		{
			int iBuyRet = THSApi.buyStock(id, amount, (float)price);
			CLog.output("ACCOUNT", " @RealAccountOpe pushBuyOrder err(%d) [%s %d %.3f %.3f] \n", 
					iBuyRet,
					id, amount, price, amount*price);
			
			super.dealReply(tranact, id, amount, price, 0.0f);
			
			return iBuyRet;
		}
		if(tranact.equals(TRANACT.SELL))
		{
			int iSellRet = THSApi.sellStock(id, amount, (float)price);
			CLog.output("ACCOUNT", " @RealAccountOpe pushSellOrder err(%d) [%s %d %.3f %.3f] \n", 
					iSellRet,
					id, amount, price, amount*price);
			
			super.dealReply(tranact, id, amount, price, 0.0f);
			
			return iSellRet;
		}
		return -1;
	}
}