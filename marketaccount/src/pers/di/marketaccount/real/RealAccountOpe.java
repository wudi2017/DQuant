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
		m_THSApiInitFlag = THSApi.initialize();
		m_dealMonitorMap = new HashMap<String, DealMonitorItem>();
		m_sync = new CSyncObj();
		m_MonitorTimerThread = new MonitorTimerThread(this);
		m_lastConnectCheckTC = 0;
		m_lastDealCheckTC = 0;
	}
	
	@Override
	public int start()
	{
		m_MonitorTimerThread.startThread();
		return 0;
	}
	
	@Override
	public int stop()
	{
		m_MonitorTimerThread.stopThread();
		return 0;
	}

	@Override
	public int postTradeRequest(TRANACT tranact, String id, int amount, double price) {
		
		if(0 != m_THSApiInitFlag)
		{
			return -1;
		}
		
		// get commission before
		List<THSApi.CommissionOrder> commissionOrdersBefore = new ArrayList<THSApi.CommissionOrder>();
        int ret_getCommission_before = THSApi.getCommissionOrderList(commissionOrdersBefore);
        if(0 != ret_getCommission_before)
        {
        	return -1;
        }
        
		// commit
		if(tranact.equals(TRANACT.BUY))
		{
			int iBuyRet = THSApi.buyStock(id, amount, (float)price);
			CLog.output("ACCOUNT", " @RealAccountOpe pushBuyOrder err(%d) [%s %d %.3f %.3f] \n", 
					iBuyRet,
					id, amount, price, amount*price);
		}
		if(tranact.equals(TRANACT.SELL))
		{
			int iSellRet = THSApi.sellStock(id, amount, (float)price);
			CLog.output("ACCOUNT", " @RealAccountOpe pushSellOrder err(%d) [%s %d %.3f %.3f] \n", 
					iSellRet,
					id, amount, price, amount*price);
		}
		
		// get commission after
		for(int iGetTimes=0; iGetTimes<10; iGetTimes++)
		{
			CThread.msleep(1000);
			// get 1 times
			List<THSApi.CommissionOrder> commissionOrdersAfter = new ArrayList<THSApi.CommissionOrder>();
	        int ret_getCommission_after = THSApi.getCommissionOrderList(commissionOrdersAfter);
	        if(0 != ret_getCommission_after)
	        {
	        	return -1;
	        }
	        
	        
	        // check changed
	        boolean bFindNewCommit = false;
	        String sKey = "";
	        for(int iAfter=0;iAfter<commissionOrdersAfter.size();iAfter++)
	        {
	        	THSApi.CommissionOrder cCommissionOrderAfter = commissionOrdersAfter.get(iAfter);
	        	
	        	boolean bExitInBefore = false;
	        	for(int iBefore=0;iBefore<commissionOrdersBefore.size();iBefore++)
		        {
		        	THSApi.CommissionOrder cCommissionOrderBefore = commissionOrdersBefore.get(iBefore);
		        	if(cCommissionOrderBefore.time.equals(cCommissionOrderAfter.time))
		        	{
		        		bExitInBefore = true;
		        		break;
		        	}
		        }
	        	
	        	if(!bExitInBefore) // find the new commit
	        	{
	        		bFindNewCommit = true;
	        		sKey = cCommissionOrderAfter.stockID + "_" + cCommissionOrderAfter.time;
	        		break;
	        	}
	        }
	        
	        // find new commit and add to monitor, for waiting dealReply
	        if(bFindNewCommit)
	        {
	        	m_sync.Lock();
	        	if(!m_dealMonitorMap.containsKey(sKey))
	        	{
	        		DealMonitorItem cDealMonitorItem = new DealMonitorItem();
	        		cDealMonitorItem.stockID = id;
	        		cDealMonitorItem.tranact = tranact;
	        		cDealMonitorItem.dealAmountCallback = 0;
	        		cDealMonitorItem.dealAvePrice = 0.0f;
	        		m_dealMonitorMap.put(sKey, cDealMonitorItem);
	        	}
	        	m_sync.UnLock();
	        	break;
	        }
		}
        
		return 0;
	}
	
	public void onTimer()
	{
		long CurTC = CUtilsDateTime.GetCurrentTimeMillis();
		if(CurTC - m_lastConnectCheckTC > 1000*60) // 试图重连检查
		{
			this.reconnectCheck();
			m_lastConnectCheckTC = CurTC;
		}
		if(CurTC - m_lastDealCheckTC > 1000*3) // 成交检查
		{
			this.dealCheck();
			m_lastDealCheckTC = CurTC;
		}
	}
	
	private void reconnectCheck()
	{
		int ret = -1;
		THSApi.ObjectContainer<Float> container = new THSApi.ObjectContainer<Float>();
		for(int iCheckTimes=0; iCheckTimes<5; iCheckTimes++)
		{
			ret =  THSApi.getTotalAssets(container);
			if(0 == ret)
			{
				break;
			}
			else
			{
				m_THSApiInitFlag = THSApi.initialize();
				if(0 == m_THSApiInitFlag)
				{
					break;
				}
				else
				{
					CLog.error("ACCOUNT", "RealAccountOpe reconnectCheck THSApi.initialize err(%d)", m_THSApiInitFlag);
				}
			}
			CThread.msleep(1000);
		}
	}
	
	private void dealCheck()
	{
		if(0 != m_THSApiInitFlag)
		{
			return;
		}
		
		//CLog.output("TEST", "onMonitorDeal");
		// get commission 
		List<THSApi.CommissionOrder> commissionOrders = new ArrayList<THSApi.CommissionOrder>();
        int ret_getCommission = THSApi.getCommissionOrderList(commissionOrders);
        if(0 == ret_getCommission)
        {
        	m_sync.Lock();
        	
    		for (Map.Entry<String, DealMonitorItem> entry : m_dealMonitorMap.entrySet()) { 
    			  
    			  String CommissionKey = entry.getKey();
    			  DealMonitorItem cDealMonitorItem = entry.getValue();
    			  
    			  // find commission
    			  THSApi.CommissionOrder cRealCommision = null;
    			  for(int iCommit=0; iCommit<commissionOrders.size(); iCommit++)
    			  {
    				  THSApi.CommissionOrder cCommisionTmp = commissionOrders.get(iCommit);
    				  String keyCheck = cCommisionTmp.stockID + "_" + cCommisionTmp.time;
    				  if(CommissionKey.equals(keyCheck))
    				  {
    					  cRealCommision = cCommisionTmp;
    					  break;
    				  }
    			  }
    			  
    			  // check dealcallback
    			  if(null != cRealCommision)
    			  {
    				  int commissionAmount = cRealCommision.commissionAmount;
					  int dealAmount = cRealCommision.dealAmount;
					  
					  CLog.output("TEST", "CommissionKey:%s dealAmountCallback:%d (commissionAmount:%d dealAmount:%d)", 
							  CommissionKey, cDealMonitorItem.dealAmountCallback, commissionAmount, dealAmount);
					  
					  if(dealAmount > cDealMonitorItem.dealAmountCallback) // 存在新成交，需要回调
					  {
						  if(0 == cDealMonitorItem.dealAmountCallback) // 首次成交
						  {
							  
						  }
						  
						  // calc param & callback
						  String stockID = cDealMonitorItem.stockID;
						  TRANACT tranact = cDealMonitorItem.tranact;
						  int amount = dealAmount - cDealMonitorItem.dealAmountCallback;
						  float price = cRealCommision.dealPrice;
						  float cost = 0.0f;

						  super.dealReply(tranact, stockID, amount, price, 0.0f);
					  }
    			  }
    			  
    		}
    		
    		m_sync.UnLock();
        }
	}
	
	public static class MonitorTimerThread extends CThread
	{
		public MonitorTimerThread(RealAccountOpe cRealAccountOpe)
		{
			m_RealAccountOpe = cRealAccountOpe;
		}
		
		@Override
		public void run() {
			while(!this.checkQuit())
			{
				m_RealAccountOpe.onTimer();
				this.Wait(1000);
			}
		}
		
		private RealAccountOpe m_RealAccountOpe;
	}
	
	public static class DealMonitorItem
	{
		public String stockID;            // ID
		public TRANACT tranact;          // 交易方向
		public int dealAmountCallback;  // 已成交回调数量
		public float dealAvePrice; // 已成交均价
	}
	
	// 实盘账户初始化标志
	private int m_THSApiInitFlag;
	
	// 监控项,已回调成交数量
	// "601988_13:20:12",DealMonitorItem
	private Map<String, DealMonitorItem> m_dealMonitorMap;
	private CSyncObj m_sync;
	private MonitorTimerThread m_MonitorTimerThread;
	private long m_lastConnectCheckTC;
	private long m_lastDealCheckTC;
	
}