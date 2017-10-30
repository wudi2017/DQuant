package pers.di.account;

import pers.di.account.common.TRANACT;

public abstract class IMarketDealReplier {
	public abstract void onDeal(TRANACT tranact, String id, int amount, float price, float cost);
}
