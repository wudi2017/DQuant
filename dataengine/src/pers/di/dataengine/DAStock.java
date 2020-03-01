package pers.di.dataengine;

import pers.di.common.CObjectObserver;
import pers.di.localstock.LocalStock;
import pers.di.localstock.common.StockInfo;

public class DAStock {

	public DAStock(DAPool pool, String stockID)
	{
		m_pool = pool;
		m_stockID = stockID;
		obsStockInfo = new CObjectObserver<StockInfo>();
		int errStockInfo = LocalStock.instance().buildStockInfoObserver(stockID, obsStockInfo);
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
	
	/*
	 * current price
	 * if has no newest timePrices, it will return yesterday close price
	 * if has timePrices. it will return newest timePrices price
	 */
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
	 * ��ȡ��K��
	 * ע�⣺
	 *     ֻ�ܻ�ȡ�����ݸ��º����ʷ��K����ʱʵʱ��K���޷���ȡ
	 */
	public DAKLines dayKLines()
	{
		return new DAKLines(m_pool, m_stockID);
	}
	
	/*
	 * ��ȡĳ�շ�ʱ��
	 */
	public DATimePrices timePrices(String date)
	{
		return new DATimePrices(m_pool, m_stockID, date);
	}
	
	/*
	 * ��ǰ��
	 */
	public DATimePrices timePrices()
	{
		return timePrices(m_pool.date());
	}
	
	private DAPool m_pool;
	private String m_stockID;
	private CObjectObserver<StockInfo> obsStockInfo;
}
