package pers.di.webstockapi;

import java.util.List;

public interface WebStockAPI {

	public class StockItem
	{
		public String ID;    // e.g. 300163
		public String name;  // e.g. �ȷ��²�
	}
	public class StockInfo
	{
		public String name;                  // �ȷ��²�
		public String date;
		public String time;
		public double curPrice;
		public double allMarketValue;        // ����ֵ, unit ��
		public double circulatedMarketValue; // ��ͨ��ֵ�� unit ��
		public double peRatio;
	}
	public class DividendPayout
	{
		public String date; 
		public double songGu; // �͹�
		public double zhuanGu; // ת��
		public double paiXi; // ��Ϣ
	}
	public class KLine
	{
		public String date; // e.g. 2015-09-18
		public String time; // e.g. 13:25:20
		public double open;
		public double close;
		public double low;
		public double high;
		public double volume; // ��λ��
	}
	public class TransactionRecord
	{
		public String time; // e.g. 13:25:20
		public double price; 
		public double volume; // ��λ��
	}
	public class RealTimeInfoLite
	{
		public String stockID;
		public String name;
		public String date;
		public String time;
		public double curPrice;
	}
	
	public int getAllStockList(List<StockItem> container);
	public int getStockInfo(String stockID, StockInfo container);
	public int getDividendPayout(String stockID, List<DividendPayout> container);
	public int getKLine(String stockID, String beginDate, String endDate, List<KLine> container);
	public int getTransactionRecordHistory(String stockID, String date, List<TransactionRecord> container);
	public int getRealTimeInfo(List<String> stockIDs, List<RealTimeInfoLite> container);
	
}