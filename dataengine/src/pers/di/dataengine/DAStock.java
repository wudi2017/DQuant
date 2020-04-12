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
	
	public String date() {
		if (m_pool.time().compareTo("09:28:00") < 0 || 
				m_pool.time().compareTo("15:10:00") > 0) {
			// not in transact time, return last date of dayk.
			DAKLines cDAKLines = this.dayKLines();
			int iSize = cDAKLines.size();
			String dateStr = cDAKLines.get(iSize-1).date;
			return dateStr;
		} else {
			/* in transact time, first try return pool date if timeprices exit,
			 * else return last date of dayk.
			 */
			DATimePrices cDATimePrices = timePrices();
			if (cDATimePrices.size() > 0) {
				return m_pool.date();
			} else {
				DAKLines cDAKLines = this.dayKLines();
				int iSize = cDAKLines.size();
				String dateStr = cDAKLines.get(iSize-1).date;
				return dateStr;
			}
		}
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
