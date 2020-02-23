package pers.di.dataengine;

import pers.di.common.CObjectObserver;
import pers.di.localstock.StockDataApi;
import pers.di.localstock.common.StockInfo;

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
	
	public double price()
	{
		double curPrice = 0.0f;
		DATimePrices cDATimePrices = timePrices();
		if(cDATimePrices.size()!=0)
		{
			curPrice = cDATimePrices.get(cDATimePrices.size()-1).price;
		}
		else
		{
			DAKLines cDAKLines = dayKLines();
			curPrice = cDAKLines.get(cDAKLines.size()-1).close;
		}
		return curPrice;
	}
	
	public double PE()
	{
		return obsStockInfo.get().peRatio;
	}
	
	public double circulatedMarketValue()
	{
		return obsStockInfo.get().circulatedMarketValue;
	}
	
	public double allMarketValue()
	{
		return obsStockInfo.get().allMarketValue;
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
