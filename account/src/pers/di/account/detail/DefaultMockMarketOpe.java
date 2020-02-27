package pers.di.account.detail;

import pers.di.account.IMarketOpe;
import pers.di.account.common.TRANACT;

public class DefaultMockMarketOpe extends IMarketOpe {
	@Override
	public int start() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int stop() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int postTradeRequest(TRANACT tranact, String id, int amount, double price) {
		// TODO Auto-generated method stub
		if(tranact == TRANACT.BUY)
		{
			super.dealReply(tranact, id, amount, price, amount*price*s_transactionCostsRatioBuy);
		}
		else if(tranact == TRANACT.SELL)
		{
			super.dealReply(tranact, id, amount, price, amount*price*s_transactionCostsRatioSell);
		}
		return 0;
	}

	private static double s_transactionCostsRatioBuy = 0.0025f;
	private static double s_transactionCostsRatioSell = 0.0025f;
}
