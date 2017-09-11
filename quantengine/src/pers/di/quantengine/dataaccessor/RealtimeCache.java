package pers.di.quantengine.dataaccessor;

import java.util.*;

import pers.di.common.*;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.common.*;

public class RealtimeCache {
	
	public RealtimeCache()
	{
		m_curDate = "0000-00-00";
		m_cacheStockTimeMap = 
				new HashMap<String, List<TimePrice>>();
	}
	
	public void build(String date, String time)
	{
		
	}
	
	public void subscribe(String stockID)
	{
		
	}
	
	public void unSubscribeAll()
	{
		
	}

	public boolean clear()
	{
		m_curDate = "0000-00-00";
		m_cacheStockTimeMap.clear();
		return true;
	}
	
	public int buildMinTimePriceListObserver(String id, String date,
			CListObserver<TimePrice> observer)
	{
		// 构建缓存
		if(date.compareTo(m_curDate) != 0)
		{
			clear();
		}
		if(!m_cacheStockTimeMap.containsKey(id))
		{
			m_cacheStockTimeMap.put(id, new ArrayList<TimePrice>());
		}
		List<TimePrice> cTimePriceList = m_cacheStockTimeMap.get(id);
		
		// 判断是否要添加新实时数据
		boolean bAddNewVal = false;
		TimePrice latastTimePrice = null;
		if(cTimePriceList.size() > 0)
		{
			latastTimePrice = cTimePriceList.get(cTimePriceList.size()-1);
		}
		if(null != latastTimePrice)
		{
			String curRealTimeHM = CUtilsDateTime.GetCurTimeStrHM();
			String latestTime = latastTimePrice.time;
			if(latestTime.startsWith(curRealTimeHM))
			{
				// 当前时间，与最后时间没有分钟变化
				bAddNewVal = false;
			}
			else
			{
				// 当前时间，与最后时间有分钟变化
				bAddNewVal = true;
			}
		}
		else
		{
			// 缓存中没数据
			bAddNewVal = true;

		}
		
		// 添加实时数据
		RealTimeInfo ctnRealTimeInfo = new RealTimeInfo();
		int error = StockDataEngine.instance().loadRealTimeInfo(id, ctnRealTimeInfo);
		if(0 == error)
		{
			TimePrice cTimePrice = new TimePrice();
			cTimePrice.time = ctnRealTimeInfo.time;
			cTimePrice.price = ctnRealTimeInfo.curPrice;
			cTimePriceList.add(cTimePrice);
		}
		
		// 更新日期
		m_curDate = date;
		
		// build观察器
		observer.build(cTimePriceList);
	
		return 0;
	}
	
	private String m_curDate;
	private Map<String, List<TimePrice>> m_cacheStockTimeMap;
}
