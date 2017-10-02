package pers.di.quantplatform.dataaccessor;

import pers.di.common.CObjectObserver;
import pers.di.dataapi.*;
import pers.di.dataapi.common.*;
import pers.di.dataengine.StockDataEngine;

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