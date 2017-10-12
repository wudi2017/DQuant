package pers.di.accountengine.detail;

import java.util.*;

import pers.di.accountengine.Account;
import pers.di.accountengine.common.*;
import pers.di.accountengine.detail.AccountStore.StoreEntity;
import pers.di.common.CObjectContainer;
import pers.di.common.CUtilsDateTime;

public class AccountEntity extends Account {
	
	public AccountEntity(IAccountOpe cIAccountOpe)
	{
		m_date = CUtilsDateTime.GetCurDateStr();
		m_time = CUtilsDateTime.GetCurTimeStr();
		m_cIAccountOpe = cIAccountOpe;
		
		m_lockedMoney = 100000.0f; // 默认锁定10w
		m_stockSelectList = new ArrayList<String>();
		m_commissionOrderList = new ArrayList<CommissionOrder>();
		m_holdStockInvestigationDaysMap = new HashMap<String, Integer>();
		m_accountStore = new AccountStore(m_cIAccountOpe.ID(), m_cIAccountOpe.password());

		load(); // 加载数据
		store(); // 存储数据
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
	public int getTotalAssets(CObjectContainer<Float> ctnTotalAssets) {
		float all_marketval = 0.0f;
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		int iRetHoldStock = getHoldStockList(cHoldStockList);
		for(int i=0;i<cHoldStockList.size();i++)
		{
			HoldStock cHoldStock = cHoldStockList.get(i);
			all_marketval = all_marketval + cHoldStock.curPrice*cHoldStock.totalAmount;
		}
		CObjectContainer<Float> money = new  CObjectContainer<Float>();
		int iRetMoney = getMoney(money);
		ctnTotalAssets.set(all_marketval + money.get());
		if(0 == iRetHoldStock && 0 == iRetMoney)
		{
			return 0;
		}
		return -99;
	}

	@Override
	public int getMoney(CObjectContainer<Float> ctnMoney) {
		
		CObjectContainer<Float> ctnOriginMoney= new CObjectContainer<Float>();
		int iRetGetAvailableMoney = m_cIAccountOpe.getAvailableMoney(ctnOriginMoney);
		
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
	public int getAvailableMoney(CObjectContainer<Float> ctnAvailableMoney) {
		
		CObjectContainer<Float> ctnOriginAvailableMoney= new CObjectContainer<Float>();
		int iRetGetAvailableMoney = m_cIAccountOpe.getAvailableMoney(ctnOriginAvailableMoney);
		
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
	public int pushBuyOrder(String stockID, int amount, float price) {
		int ret = m_cIAccountOpe.pushBuyOrder(stockID, amount, price);
		if(0 == ret)
		{
			CommissionOrder cCommissionOrder = new CommissionOrder();
			cCommissionOrder.time = m_time;
			cCommissionOrder.tranAct = TRANACT.BUY;
			cCommissionOrder.stockID = stockID;
			cCommissionOrder.amount = amount;
			cCommissionOrder.price = price;
			m_commissionOrderList.add(cCommissionOrder);
		}
		store();
		return ret;
	}

	@Override
	public int pushSellOrder(String stockID, int amount, float price) {
		int ret = m_cIAccountOpe.pushSellOrder(stockID, amount, price);
		if(0 == ret)
		{
			CommissionOrder cCommissionOrder = new CommissionOrder();
			cCommissionOrder.time = m_time;
			cCommissionOrder.tranAct = TRANACT.SELL;
			cCommissionOrder.stockID = stockID;
			cCommissionOrder.amount = amount;
			cCommissionOrder.price = price;
			m_commissionOrderList.add(cCommissionOrder);
		}
		store();
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
	public int getHoldStockList(List<HoldStock> ctnList) {
		int iGetHoldStockList = m_cIAccountOpe.getHoldStockList(ctnList);
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
	public int getHoldStock(String stockID, CObjectContainer<HoldStock> ctnHoldStock) {
		List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
		int iRet = getHoldStockList(cHoldStockList);
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
	
	public int newDayBegin() {
		load();
		int iNewDayInit = m_cIAccountOpe.newDayInit();
		return iNewDayInit;
	}

	public int newDayEnd() {
		int iNewDayTranEnd = m_cIAccountOpe.newDayTranEnd();
		if(0 == iNewDayTranEnd)
		{
			// 更新调查天数map
			Map<String, Integer> newholdStockInvestigationDaysMap = new HashMap<String, Integer>();
			
			List<HoldStock> cHoldStockList = new ArrayList<HoldStock>();
			int iGetHoldStockList = getHoldStockList(cHoldStockList);
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
				
				store();
			}
			else
			{
				iNewDayTranEnd = -201;
			}
			
			// 清空委托表
			m_commissionOrderList.clear();
			store();
		}
		
		return iNewDayTranEnd; 
	}
	
	// 加载锁定资金，选股表，股票调查天数表
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
	// 保存选股表
	private void store()
	{
		StoreEntity cStoreEntity = new StoreEntity();
		cStoreEntity.date = m_date;
		cStoreEntity.time = m_time;
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

	private String m_date;
	private String m_time;
	private IAccountOpe m_cIAccountOpe;
	
	private float m_lockedMoney;
	private List<String> m_stockSelectList; // 选股列表
	private List<CommissionOrder> m_commissionOrderList; // 委托列表
	private Map<String, Integer> m_holdStockInvestigationDaysMap; // 持股调查表
	private AccountStore m_accountStore;
}
