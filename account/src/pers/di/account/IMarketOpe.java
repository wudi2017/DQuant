package pers.di.account;

import java.util.*;

import pers.di.account.common.*;
import pers.di.account.detail.IMarketDealReplier;
import pers.di.common.*;

/*
 * implement market account interface
 */
abstract public class IMarketOpe {
	
	/*
	 * �����г���������
	 * param tranact: transaction action
	 * id: stock ID
	 * amount: transaction amount
	 * price: transaction price
	 * 
	 * �˷�����Ҫ�г���ʵ��
	 * �ɽ������ dealReply�������ص����û��ɽ���Ϣ
	 */
	abstract public int postTradeRequest(TRANACT tranact, String id, int amount, float price); 
	
	/*
	 * call this, if market deal!
	 */
	public int dealReply(TRANACT tranact, String id, int amount, float price, float cost)
	{
		if(null != m_cIMarketDealReplier)
		{
			m_cIMarketDealReplier.onDeal(tranact, id, amount, price, cost);
		}
		return 0;
	}
	
	
	/*
	 * ********************************************************************************************
	 */
	
	public IMarketOpe()
	{
		m_cIMarketDealReplier = null;
	}
	
	public int registerDealReplier(IMarketDealReplier dealReplier) 
	{
		m_cIMarketDealReplier = dealReplier;
		return 0;
	}
	
	private IMarketDealReplier m_cIMarketDealReplier;
}