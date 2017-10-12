package pers.di.accountengine.detail;

import java.util.*;

import pers.di.accountengine.common.ACCOUNTTYPE;
import pers.di.accountengine.common.HoldStock;
import pers.di.accountengine.detail.MockAccountOpeStore.StoreEntity;
import pers.di.common.*;

public class MockAccountOpe extends IAccountOpe {
	
	public MockAccountOpe(String accountID, String password, boolean connectFlag)
	{
		super();
		
		m_transactionCostsRatio = 0.0016f;
		
		m_accountID = accountID;
		m_password = password;
		
		m_dataSyncFlag = connectFlag;
		m_mockAccountOpeStore = new MockAccountOpeStore(m_accountID, m_password);

		// ����������
		{
			m_money = 200000.00f; // ģ���˻�Ĭ��20w
			m_holdStockList = new ArrayList<HoldStock>();
			
			//load
			load();
			
			//store
			store();
		}

		CLog.output("ACCOUNT", " @MockAccountOpe Construct AccountID:%s Password:%s money:%.2f transactionCostsRatio:%.4f\n", 
				m_accountID, password, m_money, m_transactionCostsRatio);
	}
	
	@Override
	public String ID()
	{
		return m_accountID;
	}
	
	@Override
	public String password()
	{
		return m_password;
	}
	
	@Override
	public ACCOUNTTYPE type()
	{
		return ACCOUNTTYPE.MOCK;
	}
	
	@Override
	public int newDayInit() 
	{
		return 0; 
	}

	@Override
	public int newDayTranEnd() {
		
		// ���гֹɾ�����
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			cHoldStock = m_holdStockList.get(i);
			cHoldStock.availableAmount = cHoldStock.totalAmount;
		}
		
		// �����ּ�
		List<HoldStock> list = new ArrayList<HoldStock>();
		getHoldStockList(list); 
		store(); // ����
		return 0;
	}
	
	@Override
	public int pushBuyOrder(String stockID, int amount, float price) {
		
		// ��������׼��
		int maxBuyAmount = (int)(m_money/price);
		int realBuyAmount = Math.min(maxBuyAmount, amount);
		realBuyAmount = realBuyAmount/100*100; 
		if(realBuyAmount <=0 ) 
		{
			return 0;
		}
		
		// ��ȡ���ж���
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			HoldStock cTmpHoldStock = m_holdStockList.get(i);
			if(cTmpHoldStock.stockID == stockID)
			{
				cHoldStock = cTmpHoldStock;
				break;
			}
		}
		if(null == cHoldStock)
		{
			HoldStock cNewHoldStock = new HoldStock();
			cNewHoldStock.stockID = stockID;
			cNewHoldStock.curPrice = price;
			m_holdStockList.add(cNewHoldStock);
			cHoldStock = cNewHoldStock;
		}
		
		// ���ö��� (���׷���ֱ�������ڲο��ɱ�����)
		float transactionCosts = m_transactionCostsRatio*price*realBuyAmount;
		int oriTotalAmount = cHoldStock.totalAmount;
		float oriHoldAvePrice = cHoldStock.refPrimeCostPrice;
		cHoldStock.totalAmount = cHoldStock.totalAmount + realBuyAmount;
		cHoldStock.refPrimeCostPrice = (oriHoldAvePrice*oriTotalAmount + price*realBuyAmount + transactionCosts)/cHoldStock.totalAmount;
		cHoldStock.curPrice = price;
		
		m_money = m_money - realBuyAmount*price;
		
		CLog.output("ACCOUNT", " @MockAccountOpe pushBuyOrder [%s %d %.3f %.3f(%.3f) %.3f] \n",
				stockID, realBuyAmount, price, realBuyAmount*price, transactionCosts, m_money);
		
		store();
		
		return 0;
	}

	@Override
	public int pushSellOrder(String stockID, int amount, float price) {
		
		// ��ȡ���ж���
		HoldStock cHoldStock = null;
		for(int i = 0; i< m_holdStockList.size(); i++)
		{
			HoldStock cTmpHoldStock = m_holdStockList.get(i);
			if(cTmpHoldStock.stockID.equals(stockID))
			{
				cHoldStock = cTmpHoldStock;
				break;
			}
		}
		
		if(null != cHoldStock)
		{
			// ��������׼��
			int realSellAmount = Math.min(cHoldStock.totalAmount, amount);
			realSellAmount = realSellAmount/100*100;
			
			// ���ö��� (���׷�����������Ǯ�п۳�)
			float transactionCosts = m_transactionCostsRatio*price*realSellAmount;
			int oriTotalAmount = cHoldStock.totalAmount;
			float oriHoldAvePrice = cHoldStock.refPrimeCostPrice;
			cHoldStock.totalAmount = cHoldStock.totalAmount - realSellAmount;
			cHoldStock.curPrice = price;
			m_money = m_money + price*realSellAmount - transactionCosts;
			if(cHoldStock.totalAmount == 0) // �����򲻼�������۸� ����
			{
				cHoldStock.refPrimeCostPrice = 0.0f;
			}
			else
			{
				cHoldStock.refPrimeCostPrice = (oriHoldAvePrice*oriTotalAmount - price*realSellAmount - transactionCosts)/cHoldStock.totalAmount;
			}
			
			// ��ּ���
			if(cHoldStock.totalAmount == 0)
			{
				m_holdStockList.remove(cHoldStock);
			}
			
			CLog.output("ACCOUNT", " @MockAccountOpe pushSellOrder [%s %d %.3f %.3f(%.3f) %.3f] \n", 
					stockID, realSellAmount, price, realSellAmount*price, transactionCosts, m_money);

			store();
			
			return 0;
		}
	
		return 0;
	}

	@Override
	public int getAvailableMoney(CObjectContainer<Float> ctnAvailableMoney) {
		ctnAvailableMoney.set(m_money);
		return 0;
	}
	
	@Override
	public int getMoney(CObjectContainer<Float> ctnMoney) {
		ctnMoney.set(m_money);
		return 0;
	}
	
	@Override
	public int getHoldStockList(List<HoldStock> out_list) {
		out_list.addAll(m_holdStockList);
		return 0;
	}
	
	private void load()
	{
		if(m_dataSyncFlag)
		{
			StoreEntity cStoreEntity = m_mockAccountOpeStore.load();
			if(null != cStoreEntity)
			{
			    m_money = cStoreEntity.money;
			    m_holdStockList.clear();
			    m_holdStockList.addAll(cStoreEntity.holdStockList);
			}
		}
	}
	
	private void store()
	{
		if(m_dataSyncFlag)
		{
			StoreEntity cStoreEntity = new StoreEntity();
			cStoreEntity.money = m_money;
			cStoreEntity.holdStockList = m_holdStockList;
			m_mockAccountOpeStore.store(cStoreEntity);
		}
	}

	/**
	 * ��Ա-----------------------------------------------------------------------
	 */

	private float m_transactionCostsRatio;
	private String m_accountID;
	private String m_password;
	
	private boolean m_dataSyncFlag;
	private MockAccountOpeStore m_mockAccountOpeStore;
	
	private float m_money;
	private List<HoldStock> m_holdStockList;
}