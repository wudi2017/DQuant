package pers.di.dataengine.webdata;

public class CommonDef {
	
	/*
	 * 股票简项
	 * id-名称
	 */
	public static class StockItem
	{
		public StockItem(){}
		public StockItem(String in_id, String in_name)
		{
			id = in_id;
			name = in_name;
		}
		public StockItem(StockItem cStockItem)
		{
			name = cStockItem.name;
			id = cStockItem.id;
		}
		public String name;
		public String id;
	}
	
	/*
	 * 日内交易明细
	 * 时间-价格-成交量
	 */
	public static class TranDetail implements Comparable
	{
		public String time;
		public float price;
		public float volume; 
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			TranDetail sdto = (TranDetail)o;
		    return this.time.compareTo(sdto.time);
		}
	}
	
	public static class StockTime {
		
		public StockTime()
		{
		}
		
		public void CopyFrom(StockTime fromObj)
		{
			this.time = fromObj.time;
			this.price = fromObj.price;
		}
		
		public String time;
		
		public Float price;
	}
	
	/*
	 * 日K数据
	 * 时间 date time 代表时间段结束时间点
	 * 日期-开盘价-收盘价-最低价-最高价-成交量
	 */
	public static class KLine implements Comparable
	{
		// 2015-09-18 or null
		public String date;
		// 13:25:20 or null
		public String time;
		public float open;
		public float close;
		public float low;
		public float high;
		public float volume;
		
		public float midle()
		{
			return (open + close) / 2;
		}
		
		public void CopyFrom(KLine c)
		{
			date = c.date;
			time = c.time;
			open = c.open;
			close = c.close;
			high = c.high;
			low = c.low;
			volume = c.volume;
		}
		
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
						return -1;
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
				return -1;
			}
		}
	}
	
	/*
	 * 分红派息因子
	 * 日期-送股-转送-派息
	 */
	public static class DividendPayout implements Comparable
	{
		public String date;
		public float songGu;
		public float zhuanGu;
		public float paiXi;
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			DividendPayout sdto = (DividendPayout)o;
		    return this.date.compareTo(sdto.date);
		}
	}
	
	/*
	 * 股票实时信息
	 * 名字-日期-时间-当前价
	 */
	public static class RealTimeInfo implements Comparable
	{
		public String name;
		public String date;
		public String time;
		public float curPrice;
		
		@Override
		public int compareTo(Object o) {
			// TODO Auto-generated method stub
			RealTimeInfo sdto = (RealTimeInfo)o;
			int iCheck1 = this.date.compareTo(sdto.date);
			if(0 == iCheck1)
			{
				int iCheck2 = this.time.compareTo(sdto.time);
				return iCheck2;
			}
			else
			{
				return iCheck1;
			}
		}
	}

	/*
	 * 股票基本信息
	 * 名字-当前价-总市值-流通市值-市盈率等（将来扩展为行业等）
	 */
	public static class StockBaseInfo
	{
		public String name;
		public String date;
		public String time;
		public float allMarketValue; // 亿
		public float circulatedMarketValue; // 亿
		public float peRatio;
		public StockBaseInfo()
		{
			name = "";
			date = "0000-00-00";
			time = "00:00:00";
			allMarketValue = 0.0f;
			circulatedMarketValue = 0.0f;
			peRatio = 0.0f;
		}
		public void CopyFrom(StockBaseInfo cCopyFromObj)
		{
			name = cCopyFromObj.name;
			date = cCopyFromObj.date;
			time = cCopyFromObj.time;
			allMarketValue = cCopyFromObj.allMarketValue;
			circulatedMarketValue = cCopyFromObj.circulatedMarketValue;
			peRatio = cCopyFromObj.peRatio;
		}
	}
}
