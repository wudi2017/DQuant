package pers.di.marketaccount.real;

import java.util.*;

import pers.di.account.IMarketOpe;
import pers.di.account.common.HoldStock;
import pers.di.account.common.TRANACT;
import pers.di.common.*;
import pers.di.thsapi.*;
import pers.di.thsapi.THSApi.*;

public class RealAccountOpe extends IMarketOpe {
	
	public static double s_transactionCostsRatio_TransferFee = 0.00002; // 买卖过户费比率
	public static double s_transactionCostsRatio_Poundage = 0.00025; // 买卖手续费比率（佣金部分）
	public static double s_transactionCosts_MinPoundage = 5.0; // 最小手续费
	
	public static double s_transactionCostsRatio_Sell_StampDuty = 0.001; // 卖出印花税比率
	
	public RealAccountOpe()
	{
		m_THSApiInitFlag = WrapperTHSApi.initialize();
		CLog.output("ACCOUNT", "RealAccountOpe WrapperTHSApi.initialize m_THSApiInitFlag = %d", m_THSApiInitFlag);
		m_dealMonitorMap = new HashMap<String, DealMonitorItem>();
		m_sync = new CSyncObj();
		m_MonitorTimerThread = new MonitorTimerThread(this);
		m_lastConnectCheckTC = 0;
		m_lastDealCheckTC = 0;
		m_lastClearDealMonitorTC = 0;
	}
	
	@Override
	public int start()
	{
		m_MonitorTimerThread.startThread();
		CLog.output("ACCOUNT", "RealAccountOpe start");
		return 0;
	}
	
	@Override
	public int stop()
	{
		m_MonitorTimerThread.stopThread();
		CLog.output("ACCOUNT", "RealAccountOpe stop");
		return 0;
	}

	@Override
	public int postTradeRequest(TRANACT tranact, String id, int amount, double price) {
		
		if(0 != m_THSApiInitFlag)
		{
			CLog.error("ACCOUNT", "RealAccountOpe postTradeRequest failed, m_THSApiInitFlag = %d", m_THSApiInitFlag);
			return -1;
		}
		
		// get commission before
		List<THSApi.CommissionOrder> commissionOrdersBefore = new ArrayList<THSApi.CommissionOrder>();
        int ret_getCommission_before = WrapperTHSApi.getCommissionOrderList(commissionOrdersBefore);
        if(0 != ret_getCommission_before)
        {
        	CLog.error("ACCOUNT", "RealAccountOpe postTradeRequest getCommissionOrderList failed, ret_getCommission_before = %d", ret_getCommission_before);
        	return -1;
        }
        
		// commit
		if(tranact.equals(TRANACT.BUY))
		{
			int iBuyRet = WrapperTHSApi.buyStock(id, amount, (float)price);
			CLog.output("ACCOUNT", " @RealAccountOpe pushBuyOrder err(%d) [%s %d %.3f %.3f] \n", 
					iBuyRet,
					id, amount, price, amount*price);
			if(0 != iBuyRet)
			{
				return iBuyRet;
			}
		}
		if(tranact.equals(TRANACT.SELL))
		{
			int iSellRet = WrapperTHSApi.sellStock(id, amount, (float)price);
			CLog.output("ACCOUNT", " @RealAccountOpe pushSellOrder err(%d) [%s %d %.3f %.3f] \n", 
					iSellRet,
					id, amount, price, amount*price);
			if(0 != iSellRet)
			{
				return iSellRet;
			}
		}
		
		// get commission after
		for(int iGetTimes=0; iGetTimes<10; iGetTimes++)
		{
			CThread.msleep(1000);
			// get 1 times
			List<THSApi.CommissionOrder> commissionOrdersAfter = new ArrayList<THSApi.CommissionOrder>();
	        int ret_getCommission_after = WrapperTHSApi.getCommissionOrderList(commissionOrdersAfter);
	        if(0 != ret_getCommission_after)
	        {
	        	CLog.error("ACCOUNT", "RealAccountOpe postTradeRequest getCommissionOrderList failed, ret_getCommission_after = %d", ret_getCommission_after);
	        	return -1;
	        }
	        
	        
	        // check changed
	        THSApi.CommissionOrder newCommitItem = null;
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
	        		newCommitItem = cCommissionOrderAfter;
	        		break;
	        	}
	        }
	        
	        // find new commit and add to monitor, for waiting dealReply
	        if(null != newCommitItem && newCommitItem.stockID.equals(id))
	        {
	        	m_sync.Lock();
	        	String sKey = newCommitItem.stockID + "_" + newCommitItem.time;
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
		if(CurTC - m_lastClearDealMonitorTC > 1000*60*5)
		{
			this.clearMonitorCheck();
			m_lastClearDealMonitorTC = CurTC;
		}
	}
	
	private void reconnectCheck()
	{
		int ret = -1;
		THSApi.ObjectContainer<Float> container = new THSApi.ObjectContainer<Float>();
		for(int iCheckTimes=0; iCheckTimes<5; iCheckTimes++)
		{
			ret =  WrapperTHSApi.getTotalAssets(container);
			if(0 == ret)
			{
				break;
			}
			else
			{
				m_THSApiInitFlag = WrapperTHSApi.initialize();
				if(0 == m_THSApiInitFlag)
				{
					break;
				}
				else
				{
					CLog.error("ACCOUNT", "RealAccountOpe reconnectCheck WrapperTHSApi.initialize err(%d)", m_THSApiInitFlag);
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
        int ret_getCommission = WrapperTHSApi.getCommissionOrderList(commissionOrders);
        if(0 == ret_getCommission)
        {
        	m_sync.Lock();
        	
        	Iterator<Map.Entry<String, DealMonitorItem>> it = m_dealMonitorMap.entrySet().iterator(); 
        	while(it.hasNext())
        	{
        		Map.Entry<String, DealMonitorItem> entry= it.next();
        		
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
    				CLog.output("ACCOUNT", "CommissionKey:%s dealAmountCallback:%d (commissionAmount:%d dealAmount:%d)", 
    						CommissionKey, cDealMonitorItem.dealAmountCallback, cRealCommision.commissionAmount, cRealCommision.dealAmount);
					  
					if(cRealCommision.dealAmount > cDealMonitorItem.dealAmountCallback) // 存在新成交，需要回调
					{
						int curDealAmount = cRealCommision.dealAmount - cDealMonitorItem.dealAmountCallback;
						float curDealPrice = (cRealCommision.dealAmount * cRealCommision.dealPrice
								- cDealMonitorItem.dealAmountCallback * cDealMonitorItem.dealAvePrice)
								/curDealAmount;
						  
						// 本次过户费
						double fTransferFee = s_transactionCostsRatio_TransferFee * curDealAmount * curDealPrice;
						  
						// 本次佣金
						double fPoundage = 0.0f;
						if(0 == cDealMonitorItem.dealAmountCallback) // 首次成交
						{
							double fSTDPoundage = s_transactionCostsRatio_Poundage * curDealAmount * curDealPrice;
							if(fSTDPoundage < s_transactionCosts_MinPoundage)
							{
								fPoundage = s_transactionCosts_MinPoundage; // 最小值
							}
							else
							{
								fPoundage = fSTDPoundage; // 标准值
							}
						}
						else
						{
							double fAllSTDPoundage = s_transactionCostsRatio_Poundage * cRealCommision.dealAmount * cRealCommision.dealPrice;
							if(fAllSTDPoundage <= s_transactionCosts_MinPoundage) // 标准值小于最小值，本次为0
							{
								fPoundage = 0;
							}
							else
							{
								double lastCallbackPoundage = s_transactionCostsRatio_Poundage * cDealMonitorItem.dealAmountCallback * cDealMonitorItem.dealAvePrice;
								if(lastCallbackPoundage < s_transactionCosts_MinPoundage)
								{
									fPoundage = fAllSTDPoundage - s_transactionCosts_MinPoundage; // 上次佣金小于最小值，则为全体标准值-最小值
								}
								else
								{
									fPoundage = s_transactionCostsRatio_Poundage * curDealAmount * curDealPrice; // 上次已经大于最小值，则为本次成交标准值
								}
							}
						}

						// 本次卖出印花税
						double fSellStampDuty = 0;
						if(TRANACT.SELL == cDealMonitorItem.tranact)
						{
							fSellStampDuty = s_transactionCostsRatio_Sell_StampDuty * curDealAmount * curDealPrice;
						}
						  
						// calc param & callback
						String stockID = cDealMonitorItem.stockID;
						TRANACT tranact = cDealMonitorItem.tranact;
						int amount = curDealAmount;
						float price = curDealPrice;
						double cost = fTransferFee + fPoundage + fSellStampDuty;

						super.dealReply(tranact, stockID, amount, price, cost);
						  
						// update cDealMonitorItem
						cDealMonitorItem.dealAmountCallback += amount;
						cDealMonitorItem.dealAvePrice = cRealCommision.dealPrice;
					}
					
					// if all deal all, remove the deal monitor
					if(cDealMonitorItem.dealAmountCallback == cRealCommision.commissionAmount)
					{
						it.remove(); 
					}
    			} 
    		}
    		m_sync.UnLock();
        }
	}
	
	private void clearMonitorCheck()
	{
		String timeStr = CUtilsDateTime.GetCurTimeStr();
		if(timeStr.compareTo("00:00:00") >= 0 && timeStr.compareTo("00:10:00") <= 0)
		{
			// in 00:00:00 - 00:10:00, clear all DealMonitorItem
			m_sync.Lock();
			
			Iterator<Map.Entry<String, DealMonitorItem>> it = m_dealMonitorMap.entrySet().iterator(); 
			while(it.hasNext())
			{ 
				Map.Entry<String, DealMonitorItem> entry= it.next(); 
				
				String key= entry.getKey(); 
				DealMonitorItem cDealMonitorItem = entry.getValue();
				
				it.remove(); 
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
	private long m_lastClearDealMonitorTC;
	
}