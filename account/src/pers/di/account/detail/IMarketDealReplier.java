package pers.di.account.detail;

import pers.di.account.common.TRANACT;

public abstract class IMarketDealReplier {
	public abstract void onDeal(TRANACT tranact, String stockID, int amount, double price, double cost);
}
