package pers.di.dataapi;

import java.util.List;

import pers.di.dataapi.common.*;

//import pers.di.webstockapi.WebStockAPI.DividendPayout;
//import pers.di.webstockapi.WebStockAPI.KLine;
//import pers.di.webstockapi.WebStockAPI.RealTimeInfoLite;
//import pers.di.webstockapi.WebStockAPI.StockInfo;
//import pers.di.webstockapi.WebStockAPI.StockItem;
//import pers.di.webstockapi.WebStockAPI.TransactionRecord;

public class WebAPILayer {
	public int getAllStockList(List<StockItem> container)
	{
		return 0;
	}
	public int getStockInfo(String stockID, StockInfo container)
	{
		return 0;
	}
	public int getDividendPayout(String stockID, List<DividendPayout> container)
	{
		return 0;
	}
	public int getKLine(String stockID, String beginDate, String endDate, List<KLine> container)
	{
		return 0;
	}
	public int getTransactionRecordHistory(String stockID, String date, List<TransactionRecord> container)
	{
		return 0;
	}
	public int getRealTimeInfo(List<String> stockIDs, List<RealTimeInfoLite> container)
	{
		return 0;
	}
}
