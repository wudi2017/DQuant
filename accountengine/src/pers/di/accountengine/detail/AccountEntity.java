package pers.di.accountengine.detail;

import java.util.*;

import pers.di.accountengine.Account;
import pers.di.accountengine.common.*;
import pers.di.accountengine.detail.AccountStore.StoreEntity;
import pers.di.common.CObjectContainer;

public class AccountEntity extends Account {
	
	public AccountEntity(IAccountOpe cIAccountOpe)
	{
		m_cIAccountOpe = cIAccountOpe;
		
		m_lockedMoney = 100000.0f; // Ĭ������10w
		m_stockSelectList = new ArrayList<String>();
		m_commissionOrderList = new ArrayList<CommissionOrder>();
		m_holdStockInvestigationDaysMap = new HashMap<String, Integer>();
		m_accountStore = new AccountStore(m_cIAccountOpe.ID(), m_cIAccountOpe.password());

		load(); // ��������
		store(null, null); // �洢����
	}
	
	@Override
	public ACCOUNTTYPE type() {
		return m_cIAccountOpe.type();
	}

	@Override
	public String ID() {
		return m_cIAccountOpe.ID();
	}

	@Override
	public String password() {
		return m_cIAccountOpe.password();
	}

	@Override
	public int newDayBegin(String date, String time) {
		load();
		int iNewDayInit = m_cIAccountOpe.newDayInit(date, time);
		return iNewDayInit;
	}

	@Override
	public int newDayEnd(String date, String time) {
		int iNewDayTranEnd = m_cIAccountOpe.newDayTranEnd(date, time);
		if(0 == iNewDayTranEnd)
		{
			// ���µ�������map
			Map<String, Integer> newholdStockInvestigationDaysMap = new HashMap<String, Integer>();
			
			List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
			int iGetHoldStockList = getHoldStockList(date, time, cHoldStockList);
			if(0 == iGetHoldStockList)
			{
				for(int i=0; i<cHoldStockList.size();i++)
				{
					HoldStock cHoldStock = cHoldStockList.get(i);
					newholdStockInvestigationDaysMap.put(cHoldStock.stockID, 0);
				}
				for(Map.Entry<String, Integer> entry:newholdStockInvestigationDaysMap.entrySet()){   
					String key = entry.getKey();
					int iInvestigationDays = 0;
					if(m_holdStockInvestigationDaysMap.containsKey(key))
					{
						iInvestigationDays = m_holdStockInvestigationDaysMap.get(key);
					}
					entry.setValue(iInvestigationDays);
				} 
				for(Map.Entry<String, Integer> entry:newholdStockInvestigationDaysMap.entrySet()){   
					int iInvestigationDays = entry.getValue();
					entry.setValue(iInvestigationDays+1);
				} 
				m_holdStockInvestigationDaysMap.clear();
				m_holdStockInvestigationDaysMap.putAll(newholdStockInvestigationDaysMap);
				
				store(date, time);
			}
			else
			{
				iNewDayTranEnd = -201;
			}
			
			// ���ί�б�
			m_commissionOrderList.clear();
			store(date, time);
		}
		
		return iNewDayTranEnd; 
	}

	@Override
	public int getTotalAssets(String date, String time, CObjectContainer<Float> ctnTotalAssets) {
		float all_marketval = 0.0f;
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		int iRetHoldStock = getHoldStockList(date, time, cHoldStockList);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			all_marketval = all_marketval + cHoldStock.curPrice*cHoldStock.totalAmount;
		}
		CObjectContainer<Float> money = new  CObjectContainer<Float>();
		int iRetMoney = getMoney(date, time, money);
		ctnTotalAssets.set(all_marketval + money.get());
		if(0 == iRetHoldStock && 0 == iRetMoney)
		{
			return 0;
		}
		return -99;
	}

	@Override
	public int getMoney(String date, String time, CObjectContainer<Float> ctnMoney) {
		
		CObjectContainer<Float> ctnOriginMoney= new CObjectContainer<Float>();
		int iRetGetAvailableMoney = m_cIAccountOpe.getAvailableMoney(date, time, ctnOriginMoney);
		
		CObjectContainer<Float> lockedMoney= new CObjectContainer<Float>();
		int iRetGetLockedMoney = this.getLockedMoney(lockedMoney);
		
		Float money = ctnOriginMoney.get() - lockedMoney.get();
		if(money < 0)
		{
			money = 0.0f;
		}
		ctnMoney.set(money);
		
		return iRetGetAvailableMoney + iRetGetLockedMoney;
	}

	@Override
	public int getAvailableMoney(String date, String time, CObjectContainer<Float> ctnAvailableMoney) {
		
		CObjectContainer<Float> ctnOriginAvailableMoney= new CObjectContainer<Float>();
		int iRetGetAvailableMoney = m_cIAccountOpe.getAvailableMoney(date, time, ctnOriginAvailableMoney);
		
		CObjectContainer<Float> lockedMoney= new CObjectContainer<Float>();
		int iRetGetLockedMoney = this.getLockedMoney(lockedMoney);
		
		Float availableMoney = ctnOriginAvailableMoney.get() - lockedMoney.get();
		if(availableMoney < 0)
		{
			availableMoney = 0.0f;
		}
		ctnAvailableMoney.set(availableMoney);
		
		return iRetGetAvailableMoney + iRetGetLockedMoney;
	}

	@Override
	public int pushBuyOrder(String date, String time, String stockID, int amount, float price) {
		int ret = m_cIAccountOpe.pushBuyOrder(date, time, stockID, amount, price);
		if(0 == ret)
		{
			CommissionOrder cCommissionOrder = new CommissionOrder();
			cCommissionOrder.time = time;
			cCommissionOrder.tranAct = TRANACT.BUY;
			cCommissionOrder.stockID = stockID;
			cCommissionOrder.amount = amount;
			cCommissionOrder.price = price;
			m_commissionOrderList.add(cCommissionOrder);
		}
		store(date, time);
		return ret;
	}

	@Override
	public int pushSellOrder(String date, String time, String stockID, int amount, float price) {
		int ret = m_cIAccountOpe.pushSellOrder(date, time, stockID, amount, price);
		if(0 == ret)
		{
			CommissionOrder cCommissionOrder = new CommissionOrder();
			cCommissionOrder.time = time;
			cCommissionOrder.tranAct = TRANACT.SELL;
			cCommissionOrder.stockID = stockID;
			cCommissionOrder.amount = amount;
			cCommissionOrder.price = price;
			m_commissionOrderList.add(cCommissionOrder);
		}
		store(date, time);
		return ret;
	}

	@Override
	public int getCommissionOrderList(List<CommissionOrder> ctnList) {
		ctnList.clear();
		ctnList.addAll(m_commissionOrderList);
		return 0;
	}

	@Override
	public int getBuyCommissionOrderList(List<CommissionOrder> ctnList) {
		ctnList.clear();
		List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
		int iRet = this.getCommissionOrderList(cCommissionOrderList);
		for(int i= 0;i<cCommissionOrderList.size();i++)
		{
			CommissionOrder cCommissionOrder = cCommissionOrderList.get(i);
			if(cCommissionOrder.tranAct == TRANACT.BUY)
			{
				CommissionOrder cNewCommissionOrder = new CommissionOrder();
				cNewCommissionOrder.CopyFrom(cCommissionOrder);
				ctnList.add(cNewCommissionOrder);
			}
		}
		return iRet;
	}

	@Override
	public int getSellCommissionOrderList(List<CommissionOrder> ctnList) {
		ctnList.clear();
		List<CommissionOrder> cCommissionOrderList = new ArrayList<CommissionOrder>();
		int iRet = this.getCommissionOrderList(cCommissionOrderList);
		for(int i= 0;i<cCommissionOrderList.size();i++)
		{
			CommissionOrder cCommissionOrder = cCommissionOrderList.get(i);
			if(cCommissionOrder.tranAct == TRANACT.SELL)
			{
				CommissionOrder cNewCommissionOrder = new CommissionOrder();
				cNewCommissionOrder.CopyFrom(cCommissionOrder);
				ctnList.add(cNewCommissionOrder);
			}
		}
		return iRet;
	}

	@Override
	public int getHoldStockList(String date, String time, List<HoldStock> ctnList) {
		int iGetHoldStockList = m_cIAccountOpe.getHoldStockList(date, time, ctnList);
		if(0 == iGetHoldStockList)
		{
			for(int i=0;i<ctnList.size();i++)
	        {
	        	HoldStock cHoldStock = ctnList.get(i);
	        	if(m_holdStockInvestigationDaysMap.containsKey(cHoldStock.stockID))
	        	{
	        		cHoldStock.investigationDays = m_holdStockInvestigationDaysMap.get(cHoldStock.stockID);
	        	}
	        	else
	        	{
	        		cHoldStock.investigationDays = 0;
	        	}
	        }
		}
		return iGetHoldStockList;
	}

	@Override
	public int getHoldStock(String date, String time, String stockID, CObjectContainer<HoldStock> ctnHoldStock) {
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		int iRet = getHoldStockList(date, time, cHoldStockList);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			if(cHoldStockList.get(i).stockID.equals(stockID))
			{
				HoldStock cNewHoldStock = cHoldStockList.get(i);
				ctnHoldStock.set(cNewHoldStock);
				break;
			}
		}
		return iRet;
	}
	
	// ���������ʽ�ѡ�ɱ�����Ʊ����������
	private void load()
	{
		StoreEntity cStoreEntity = m_accountStore.load();
		if(null != cStoreEntity)
		{
			// load lockedMoney
			if(null != cStoreEntity.lockedMoney)
				m_lockedMoney = cStoreEntity.lockedMoney;
			
			// load stockSelectList
		    m_stockSelectList.clear();
		    if(null != cStoreEntity.stockSelectList)
		    	m_stockSelectList.addAll(cStoreEntity.stockSelectList);
		    
		    // 
		    m_commissionOrderList.clear();
		    if(null != cStoreEntity.commissionOrderList)
		    	m_commissionOrderList.addAll(cStoreEntity.commissionOrderList);
		    
		    // load holdStockInvestigationDaysMap
		    m_holdStockInvestigationDaysMap.clear();
			if(null != cStoreEntity.holdStockInvestigationDaysMap)
		    	m_holdStockInvestigationDaysMap.putAll(cStoreEntity.holdStockInvestigationDaysMap);
		}
	}
	// ����ѡ�ɱ�
	private void store(String date, String time)
	{
		StoreEntity cStoreEntity = new StoreEntity();
		cStoreEntity.date = date;
		cStoreEntity.time = time;
		// locked money
		cStoreEntity.lockedMoney = m_lockedMoney;
		// stockSelectList
		cStoreEntity.stockSelectList = m_stockSelectList;
		// commissionOrderList
		cStoreEntity.commissionOrderList = m_commissionOrderList;
		// holdStockInvestigationDaysMap
		cStoreEntity.holdStockInvestigationDaysMap = m_holdStockInvestigationDaysMap;
		m_accountStore.store(cStoreEntity);
	}
	
	public int getLockedMoney(CObjectContainer<Float> ctnLockedMoney)
	{
		ctnLockedMoney.set(m_lockedMoney);
		return 0;
	}
	
	/*
	 * ******************************************************************************************
	 */

	private IAccountOpe m_cIAccountOpe;
	
	private float m_lockedMoney;
	private List<String> m_stockSelectList; // ѡ���б�
	private List<CommissionOrder> m_commissionOrderList; // ί���б�
	private Map<String, Integer> m_holdStockInvestigationDaysMap; // �ֹɵ����
	private AccountStore m_accountStore;
}