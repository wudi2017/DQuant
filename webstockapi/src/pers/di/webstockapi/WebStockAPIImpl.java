package pers.di.webstockapi;

import java.util.List;

import pers.di.webstockapi.func.*;

public class WebStockAPIImpl implements WebStockAPI {
	
	public WebStockAPIImpl()
	{
		m_DataWebStockAllList = new DataWebStockAllList();
		m_DataWebStockInfo = new DataWebStockInfo();
		m_DataWebStockDayK = new DataWebStockDayK();
		m_TransactionRecordHistory = new TransactionRecordHistory();
		m_DataWebStockRealTimeInfo = new DataWebStockRealTimeInfo();
		m_DataWebStockDividendPayout = new DataWebStockDividendPayout();
	}

	@Override
	public int getAllStockList(List<StockItem> container) {
		return m_DataWebStockAllList.getAllStockList(container);
	}

	@Override
	public int getStockInfo(String stockID, StockInfo container) {
		return m_DataWebStockInfo.getStockInfo(stockID, container);
	}

	@Override
	public int getKLine(String stockID, String beginDate, String endDate, List<KLine> container) {
		return m_DataWebStockDayK.getKLine(stockID, beginDate, endDate, container);
	}

	@Override
	public int getTransactionRecordHistory(String stockID, String date, List<TransactionRecord> container) {
		return m_TransactionRecordHistory.getTransactionRecordHistory(stockID, date, container);
	}

	@Override
	public int getRealTimeInfo(List<String> stockIDs, List<RealTimeInfoLite> container) {
		return m_DataWebStockRealTimeInfo.getRealTimeInfo(stockIDs, container);
	}

	@Override
	public int getDividendPayout(String stockID, List<DividendPayout> container) {
		return m_DataWebStockDividendPayout.getDividendPayout(stockID, container);
	}

	private DataWebStockAllList m_DataWebStockAllList;
	private DataWebStockInfo m_DataWebStockInfo;
	private DataWebStockDayK m_DataWebStockDayK;
	private TransactionRecordHistory m_TransactionRecordHistory;
	private DataWebStockRealTimeInfo m_DataWebStockRealTimeInfo;
	private DataWebStockDividendPayout m_DataWebStockDividendPayout;
}
