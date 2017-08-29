package pers.di.quantengine;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CLog;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.StockDataEngine.*;
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
		m_date = date;
		m_time = time;
	}
	
	/*
	 * -----------------------------------------------------------------------
	 * All stock
	 */
	public static class DAStockIDs
	{
		public DAStockIDs(DEStockIDs origin)
		{
			m_DEStockIDs = origin;
		}
		public int error() 
		{ 
			return m_DEStockIDs.error(); 
		}
		public int size() 
		{ 
			return m_DEStockIDs.size();
		}
		public String get(int i)
		{
			return m_DEStockIDs.get(i);
		}
		private DEStockIDs m_DEStockIDs;
	}
	public DAStockIDs getAllStockIDs()
	{
		DEStockIDs cDEStockIDs = StockDataEngine.instance().getAllStockIDs();
		return new DAStockIDs(cDEStockIDs);
	}
	
	/*
	 * -----------------------------------------------------------------------
	 * Stock BaseInfo
	 */
	public static class DAStockBaseInfo
	{
		public DAStockBaseInfo(DEStockBaseInfo origin)
		{
			m_DEStockBaseInfo = origin;
		}
		public int error() 
		{ 
			return m_DEStockBaseInfo.error(); 
		}
		public String name() 
		{ 
			return m_DEStockBaseInfo.get().name; 
		}
		public String date() 
		{ 
			return m_DEStockBaseInfo.get().date; 
		}
		public String time() 
		{ 
			return m_DEStockBaseInfo.get().time; 
		}
		public float allMarketValue() 
		{ 
			return m_DEStockBaseInfo.get().allMarketValue; 
		}
		public float circulatedMarketValue() 
		{ 
			return m_DEStockBaseInfo.get().circulatedMarketValue; 
		}
		public float peRatio() 
		{ 
			return m_DEStockBaseInfo.get().peRatio; 
		}
		private DEStockBaseInfo m_DEStockBaseInfo;
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
		public DAKLines(DEKLines cDEKLines)
		{
			m_resultDEKLines = cDEKLines;
		}
		public int error()
		{
			return m_resultDEKLines.error();
		}
		public int size()
		{
			return m_resultDEKLines.size();
		}
		public KLine get(int i)
		{
			return m_resultDEKLines.get(i);
		}
		private DEKLines m_resultDEKLines;
	}
	public DAKLines getDayKLines(String stockID)
	{
		DEKLines cDEKLines = StockDataEngine.instance().getDayKLines(stockID, "2008-01-01", m_date);
		return new DAKLines(cDEKLines);
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
	
	/**
	 * ------------------------------------------------------
	 */
	private String m_date;
	private String m_time;
	
}
