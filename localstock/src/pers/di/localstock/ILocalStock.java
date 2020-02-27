package pers.di.localstock;

import java.util.List;

import pers.di.common.CListObserver;
import pers.di.common.CObjectObserver;
import pers.di.localstock.common.KLine;
import pers.di.localstock.common.RealTimeInfoLite;
import pers.di.localstock.common.StockInfo;
import pers.di.localstock.common.TimePrice;

public interface ILocalStock {
	public boolean resetDataRoot(String dateRoot);
	public String dataRoot();
	public int updateAllLocalStocks(String dateStr);
	public int updateLocalStocks(String stockID, String dateStr);
	public int buildAllStockIDObserver(CListObserver<String> observer);
	public int buildStockInfoObserver(String id, CObjectObserver<StockInfo> observer);
	public int buildDayKLineListObserver(String stockID, 
			String fromDate, String toDate, CListObserver<KLine> observer);
	public int buildMinTimePriceListObserver(String id, String date, 
			String beginTime, String endTime, CListObserver<TimePrice> observer);
	public int loadRealTimeInfo(List<String> stockIDs, List<RealTimeInfoLite> container);
}
