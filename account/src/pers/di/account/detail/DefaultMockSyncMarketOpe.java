package pers.di.account.detail;

import pers.di.account.IMarketOpe;
import pers.di.account.common.TRANACT;

public class DefaultMockSyncMarketOpe extends IMarketOpe {
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
		
		// ������
		double fTransferFee = s_transactionCostsRatio_TransferFee * amount * price;
		// Ӷ��
		double fPoundage = s_transactionCostsRatio_Poundage * amount * price;
		if(fPoundage < s_transactionCosts_MinPoundage)
		{
			fPoundage = s_transactionCosts_MinPoundage;
		}
		// ��������ӡ��˰
		double fSellStampDuty = 0;
		if(TRANACT.SELL == tranact)
		{
			fSellStampDuty = s_transactionCostsRatio_Sell_StampDuty * amount * price;
		}
		
		double cost = fTransferFee + fPoundage + fSellStampDuty;
		super.dealReply(tranact, id, amount, price, cost);
		
		return 0;
	}
	
	public static double s_transactionCostsRatio_TransferFee = 0.00002; // �����ѱ���(����˫����ȡ)
	public static double s_transactionCostsRatio_Poundage = 0.00025; // �����ѱ���-Ӷ��(����˫����ȡ)
	public static double s_transactionCosts_MinPoundage = 5.0; // ��������Сֵ
	public static double s_transactionCostsRatio_Sell_StampDuty = 0.001; // ӡ��˰����(��������ȡ)
}
