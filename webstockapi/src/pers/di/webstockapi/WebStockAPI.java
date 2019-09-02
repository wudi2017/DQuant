package pers.di.webstockapi;

import java.util.List;

import pers.di.dataapi.common.KLine;

public interface WebStockAPI {

	public class StockItem
	{
		public String ID;
		public String name;
	}
	public class StockInfo
	{
		public String name;
		public String date;
		public String time;
		public double curPrice;
		public double allMarketValue; // 总市值, unit 亿
		public double circulatedMarketValue; // 流通市值： unit 亿
		public double peRatio;
	}
	public class DividendPayout implements Comparable
	{
		public String date; 
		public double songGu; // 送股
		public double zhuanGu; // 转送
		public double paiXi; // 派息
		@Override
		public int compareTo(Object o) {
			DividendPayout sdto = (DividendPayout)o;
		    return this.date.compareTo(sdto.date);
		}
	}
	public class KLine implements Comparable
	{
		public String date; // e.g. 2015-09-18
		public String time; // e.g. 13:25:20
		public double open;
		public double close;
		public double low;
		public double high;
		public double volume;
		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub
			KLine sdto = (KLine)arg0;
			int iRet = 0;
			// date compare
			if(null != this.date && null != sdto.date)
			{
				iRet = this.date.compareTo(sdto.date);
				if(0 == iRet)
				{
					// time compare
					if(null != this.time && null != sdto.time)
					{
						return this.time.compareTo(sdto.time);
					}
					else if(null != this.time && null == sdto.time)
					{
						return 1;
					}
					else 
					{
						return 0;
					}
				}
				else
				{
					return iRet;
				}
			}
			else if(null != this.date && null == sdto.date)
			{
				return 1;
			}
			else 
			{
				return 0;
			}
		}
	}
	public class TransactionRecord implements Comparable
	{
		public String time; // e.g. 13:25:20
		public double price; 
		public double volume; 
		@Override
		public int compareTo(Object o) {
			TransactionRecord sdto = (TransactionRecord)o;
		    return this.time.compareTo(sdto.time);
		}
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
	public int getKLine(String stockID, String beginDate, String endDate, List<KLine> container);
	public int getTransactionRecordHistory(String stockID, String date, List<TransactionRecord> container);
	public int getRealTimeInfo(List<String> stockIDs, List<RealTimeInfoLite> container);
	public int getDividendPayout(String stockID, List<DividendPayout> container);
}