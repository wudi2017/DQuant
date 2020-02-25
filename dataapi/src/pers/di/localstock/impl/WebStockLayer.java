package pers.di.localstock.impl;

import java.util.ArrayList;
import java.util.List;

import pers.di.localstock.common.*;
import pers.di.webstock.WebStock;

public class WebStockLayer {
	public static int getAllStockList(List<StockItem> container)
	{
		List<pers.di.webstock.IWebStock.StockItem> ctnwsStockItem = new ArrayList<pers.di.webstock.IWebStock.StockItem>();
		int error = WebStock.instance().getAllStockList(ctnwsStockItem);
		if(0 == error)
		{
			for(int i = 0; i < ctnwsStockItem.size(); i++)  
	        {  
				pers.di.webstock.IWebStock.StockItem wsStockItem = ctnwsStockItem.get(i);  
	            
				StockItem cStockItem = new StockItem();
				cStockItem.id = wsStockItem.ID;
				cStockItem.name = wsStockItem.name;
				container.add(cStockItem);
	        } 
		}
		return error;
	}
	public static int getStockInfo(String stockID, StockInfo container)
	{
		pers.di.webstock.IWebStock.StockInfo ctnwsStockInfo = new pers.di.webstock.IWebStock.StockInfo();
		int error = WebStock.instance().getStockInfo(stockID, ctnwsStockInfo);
		if(0 == error)
		{ 
			container.name = ctnwsStockInfo.name;
			container.date = ctnwsStockInfo.date;
			container.time = ctnwsStockInfo.time;
			container.allMarketValue = ctnwsStockInfo.allMarketValue;
			container.circulatedMarketValue = ctnwsStockInfo.circulatedMarketValue;
			container.peRatio = ctnwsStockInfo.peRatio;
		}
		return error;
	}
	public static int getDividendPayout(String stockID, List<DividendPayout> container)
	{
		List<pers.di.webstock.IWebStock.DividendPayout> ctnwsDividendPayout = new ArrayList<pers.di.webstock.IWebStock.DividendPayout>();
		int error = WebStock.instance().getDividendPayout(stockID, ctnwsDividendPayout);
		if(0 == error)
		{
			for(int i = 0; i < ctnwsDividendPayout.size(); i++)  
	        {  
				pers.di.webstock.IWebStock.DividendPayout cwsDividendPayout = ctnwsDividendPayout.get(i); 
				
				DividendPayout cDividendPayout = new DividendPayout();
				cDividendPayout.date = cwsDividendPayout.date;
				cDividendPayout.songGu = cwsDividendPayout.songGu;
				cDividendPayout.zhuanGu = cwsDividendPayout.zhuanGu;
				cDividendPayout.paiXi = cwsDividendPayout.paiXi;
				container.add(cDividendPayout);
	        } 
		}
		return error;
	}
	public static int getKLine(String stockID, String beginDate, String endDate, List<KLine> container)
	{
		List<pers.di.webstock.IWebStock.KLine> ctnwsKLine = new ArrayList<pers.di.webstock.IWebStock.KLine>();
		int error = WebStock.instance().getKLine(stockID, beginDate, endDate, ctnwsKLine);
		if(0 == error)
		{
			for(int i = 0; i < ctnwsKLine.size(); i++)  
	        { 
				pers.di.webstock.IWebStock.KLine cwsKLine = ctnwsKLine.get(i);  
				
				KLine cKLine = new KLine();
				cKLine.date = cwsKLine.date;
				cKLine.time = cwsKLine.time;
				cKLine.open = cwsKLine.open;
				cKLine.close = cwsKLine.close;
				cKLine.low = cwsKLine.low;
				cKLine.high = cwsKLine.high;
				cKLine.volume = cwsKLine.volume;
				container.add(cKLine);
	        }
		}
		return error;
	}
	public static int getTransactionRecordHistory(String stockID, String date, List<TransactionRecord> container)
	{
		List<pers.di.webstock.IWebStock.TransactionRecord> ctnwsTradeDetails = new ArrayList<pers.di.webstock.IWebStock.TransactionRecord>();
		int error = WebStock.instance().getTransactionRecordHistory(stockID, date, ctnwsTradeDetails);
		if(0 == error)
		{
			for(int i = 0; i < ctnwsTradeDetails.size(); i++) 
			{
				pers.di.webstock.IWebStock.TransactionRecord cwsTransactionRecord = ctnwsTradeDetails.get(i);
				
				TransactionRecord cTransactionRecord = new TransactionRecord();
				cTransactionRecord.time = cwsTransactionRecord.time;
				cTransactionRecord.price = cwsTransactionRecord.price;
				cTransactionRecord.volume = cwsTransactionRecord.volume; 
				container.add(cTransactionRecord);
				
			}
		}
		return error;
	}
	public static int getRealTimeInfo(List<String> stockIDs, List<RealTimeInfoLite> container)
	{
		List<pers.di.webstock.IWebStock.RealTimeInfoLite> ctnwsRTInfos = new ArrayList<pers.di.webstock.IWebStock.RealTimeInfoLite>();
		int error = WebStock.instance().getRealTimeInfo(stockIDs, ctnwsRTInfos);
		for(int i=0; i<ctnwsRTInfos.size(); i++)
		{
			pers.di.webstock.IWebStock.RealTimeInfoLite cwsRealTimeInfoLite = ctnwsRTInfos.get(i);
			
			RealTimeInfoLite cRealTimeInfoLite = new RealTimeInfoLite();
			cRealTimeInfoLite.stockID = cwsRealTimeInfoLite.stockID;
			cRealTimeInfoLite.name = cwsRealTimeInfoLite.name;
			cRealTimeInfoLite.date = cwsRealTimeInfoLite.date;
			cRealTimeInfoLite.time = cwsRealTimeInfoLite.time;
			cRealTimeInfoLite.curPrice = cwsRealTimeInfoLite.curPrice;
			container.add(cRealTimeInfoLite);
		}
		return error;
	}
}
