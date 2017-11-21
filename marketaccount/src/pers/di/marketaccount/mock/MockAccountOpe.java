package pers.di.marketaccount.mock;

import java.util.*;

import pers.di.account.IMarketOpe;
import pers.di.account.common.HoldStock;
import pers.di.account.common.TRANACT;
import pers.di.account.detail.*;
import pers.di.common.*;

public class MockAccountOpe extends IMarketOpe {
	
	public static double s_transactionCostsRatio_TransferFee = 0.00002; // ���������ѱ���
	public static double s_transactionCostsRatio_Poundage = 0.00025; // ���������ѱ��ʣ�Ӷ�𲿷֣�
	public static double s_transactionCosts_MinPoundage = 5.0; // ��С������
	
	public static double s_transactionCostsRatio_Sell_StampDuty = 0.001; // ����ӡ��˰����

	public int postTradeRequest(TRANACT tranact, String id, int amount, double price)
	{
		if(tranact == TRANACT.BUY)
		{
			double dealMoney = amount*price;
			double transferFee = CUtilsMath.saveNDecimal(dealMoney*s_transactionCostsRatio_TransferFee, 2);
			double poundage = CUtilsMath.saveNDecimal(dealMoney*s_transactionCostsRatio_Poundage, 2);
			if(poundage < s_transactionCosts_MinPoundage)
			{
				poundage = s_transactionCosts_MinPoundage;
			}
			double cost = transferFee + poundage;
			super.dealReply(TRANACT.BUY, id, amount, price, cost);
		}
		else if(tranact == TRANACT.SELL)
		{
			double dealMoney = amount*price;
			double transferFee = CUtilsMath.saveNDecimal(dealMoney*s_transactionCostsRatio_TransferFee, 2);
			double poundage = CUtilsMath.saveNDecimal(dealMoney*s_transactionCostsRatio_Poundage, 2);
			if(poundage < s_transactionCosts_MinPoundage)
			{
				poundage = s_transactionCosts_MinPoundage;
			}
			double stampDuty = CUtilsMath.saveNDecimal(dealMoney*s_transactionCostsRatio_Sell_StampDuty, 2);
			double cost = transferFee + poundage + stampDuty;
			super.dealReply(TRANACT.SELL, id, amount, price, cost);
		}
		return 0;
	}
}