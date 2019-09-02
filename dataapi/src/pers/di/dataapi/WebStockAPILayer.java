package pers.di.dataapi;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataapi.common.*;
import pers.di.webstockapi.WebStock;

public class WebStockAPILayer {
	public static int getAllStockList(List<StockItem> container)
	{
		List<pers.di.webstockapi.WebStockAPI.StockItem> ctnwsStockItem = new ArrayList<pers.di.webstockapi.WebStockAPI.StockItem>();
		int error = WebStock.API.getAllStockList(ctnwsStockItem);
		if(0 == error)
		{
			for(int i = 0; i < ctnwsStockItem.size(); i++)  
	        {  
				pers.di.webstockapi.WebStockAPI.StockItem wsStockItem = ctnwsStockItem.get(i);  
	            
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
		pers.di.webstockapi.WebStockAPI.StockInfo ctnwsStockInfo = new pers.di.webstockapi.WebStockAPI.StockInfo();
		int error = WebStock.API.getStockInfo(stockID, ctnwsStockInfo);
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
		List<pers.di.webstockapi.WebStockAPI.DividendPayout> ctnwsDividendPayout = new ArrayList<pers.di.webstockapi.WebStockAPI.DividendPayout>();
		int error = WebStock.API.getDividendPayout(stockID, ctnwsDividendPayout);
		if(0 == error)
		{
			for(int i = 0; i < ctnwsDividendPayout.size(); i++)  
	        {  
				pers.di.webstockapi.WebStockAPI.DividendPayout cwsDividendPayout = ctnwsDividendPayout.get(i); 
				
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
		List<pers.di.webstockapi.WebStockAPI.KLine> ctnwsKLine = new ArrayList<pers.di.webstockapi.WebStockAPI.KLine>();
		int error = WebStock.API.getKLine("000488", "20180706", "20190831", ctnwsKLine);
		if(0 == error)
		{
			for(int i = 0; i < ctnwsKLine.size(); i++)  
	        { 
				pers.di.webstockapi.WebStockAPI.KLine cwsKLine = ctnwsKLine.get(i);  
				
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
		List<pers.di.webstockapi.WebStockAPI.TransactionRecord> ctnwsTradeDetails = new ArrayList<pers.di.webstockapi.WebStockAPI.TransactionRecord>();
		int error = WebStock.API.getTransactionRecordHistory(stockID, date, ctnwsTradeDetails);
		if(0 == error)
		{
			for(int i = 0; i < ctnwsTradeDetails.size(); i++) 
			{
				pers.di.webstockapi.WebStockAPI.TransactionRecord cwsTransactionRecord = ctnwsTradeDetails.get(i);
				
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
		List<pers.di.webstockapi.WebStockAPI.RealTimeInfoLite> ctnwsRTInfos = new ArrayList<pers.di.webstockapi.WebStockAPI.RealTimeInfoLite>();
		int error = WebStock.API.getRealTimeInfo(stockIDs, ctnwsRTInfos);
		for(int i=0; i<ctnwsRTInfos.size(); i++)
		{
			pers.di.webstockapi.WebStockAPI.RealTimeInfoLite cwsRealTimeInfoLite = ctnwsRTInfos.get(i);
			
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
