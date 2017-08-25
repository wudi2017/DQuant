package pers.di.quantengine;

import java.util.ArrayList;
import java.util.List;

import pers.di.dataengine.webdata.CommonDef.KLine;
import pers.di.dataengine.webdata.CommonDef.StockBaseInfo;
import pers.di.dataengine.webdata.CommonDef.TimePrice;


/*
 * 数据访问器
 * 细节：内部只有时间数据，当需要访问时进行调用
 */
public class DataAccessor {
	public DataAccessor(String date, String time)
	{
	}
	
	/*
	 * -----------------------------------------------------------------------
	 * All stock
	 */
	public static class DAStockIDs
	{
		public DAStockIDs(int error, List<String> origin)
		{
			m_error = error;
			m_resultList = origin;
		}
		public int error() 
		{ 
			return m_error; 
		}
		public int size() 
		{ 
			return m_resultList.size();
		}
		public String get(int i)
		{
			return m_resultList.get(i);
		}
		private int m_error;
		private List<String> m_resultList;
	}
	public DAStockIDs getAllStockIDs()
	{
		return null;
	}
	
	/*
	 * -----------------------------------------------------------------------
	 * Stock BaseInfo
	 */
	public static class DAStockBaseInfo
	{
		public DAStockBaseInfo(int error, StockBaseInfo origin)
		{
			m_error = error;
			m_stockBaseInfo = origin;
		}
		public int error() 
		{ 
			return m_error; 
		}
		public StockBaseInfo get()
		{
			return m_stockBaseInfo;
		}
		private int m_error;
		private StockBaseInfo m_stockBaseInfo;
	}
	public DAStockBaseInfo getStockBaseInfo(String id)
	{
		return null;
	}
	
	/*
	 * -----------------------------------------------------------------------
	 * Day KLines 
	 */
	public static class DAKLines
	{
		public DAKLines(int error, List<KLine> origin, int iBegin, int iEnd)
		{
			m_error = error;
			m_resultList = origin;
			m_iBegin = iBegin;
			m_iEnd = iEnd;
		}
		public int error()
		{
			return m_error;
		}
		public int size()
		{
			return m_iEnd-m_iBegin+1;
		}
		public KLine get(int i)
		{
			return m_resultList.get(m_iBegin+i);
		}
		private int m_error;
		private List<KLine> m_resultList;
		private int m_iBegin;
		private int m_iEnd;
	}
	public DAKLines getDayKLines(String stockID)
	{
		return null;
	}
	
	/*
	 * -----------------------------------------------------------------------
	 * Min TimePrices
	 */
	public static class DATimePrices
	{
		public DATimePrices(int error, List<TimePrice> origin, int iBegin, int iEnd)
		{
			m_error = error;
			m_resultList = origin;
			m_iBegin = iBegin;
			m_iEnd = iEnd;
		}
		public int error()
		{
			return m_error;
		}
		public int size()
		{
			return m_iEnd-m_iBegin+1;
		}
		public TimePrice get(int i)
		{
			return m_resultList.get(m_iBegin+i);
		}
		private int m_error;
		private List<TimePrice> m_resultList;
		private int m_iBegin;
		private int m_iEnd;
	}
	public DATimePrices getMinTimePrices(String id, String date)
	{
		return null;
	}
}
