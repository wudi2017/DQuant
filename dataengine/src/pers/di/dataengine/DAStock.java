package pers.di.dataengine;

import pers.di.common.CObjectObserver;
import pers.di.dataapi.common.StockInfo;
import pers.di.dataapi.StockDataApi;

public class DAStock {

	public DAStock(DAPool pool, String stockID)
	{
		m_pool = pool;
		m_stockID = stockID;
		obsStockInfo = new CObjectObserver<StockInfo>();
		int errStockInfo = StockDataApi.instance().buildStockInfoObserver(stockID, obsStockInfo);
		if(0 != errStockInfo)
		{
			obsStockInfo.build(new StockInfo());
		}
	}
	
	public String ID()
	{
		return m_stockID;
	}
	
	public String name()
	{
		return obsStockInfo.get().name;
	}
	
	public float PE()
	{
		return obsStockInfo.get().peRatio;
	}
	
	/*
	 * 获取日K线
	 * 注意：
	 *     只能获取到数据更新后的历史日K，当时实时日K线无法获取
	 */
	public DAKLines dayKLines()
	{
		return new DAKLines(m_pool, m_stockID);
	}
	
	/*
	 * 获取某日分时线
	 */
	public DATimePrices timePrices(String date)
	{
		return new DATimePrices(m_pool, m_stockID, date);
	}
	
	/*
	 * 当前日
	 */
	public DATimePrices timePrices()
	{
		return timePrices(m_pool.date());
	}
	
	private DAPool m_pool;
	private String m_stockID;
	private CObjectObserver<StockInfo> obsStockInfo;
}
