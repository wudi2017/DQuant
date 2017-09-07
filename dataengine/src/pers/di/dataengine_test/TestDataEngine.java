package pers.di.dataengine_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CImageCurve;
import pers.di.common.CLog;
import pers.di.common.CUtilsDateTime;
import pers.di.common.CImageCurve.CurvePoint;
import pers.di.dataengine.basedata.*;
import pers.di.dataengine.*;
import pers.di.dataengine.common.*;

public class TestDataEngine {
	
	private static void test_updateAllLocalStocks()
	{
		StockDataEngine.instance().updateAllLocalStocks("2017-08-15");
	}
	
	private static void test_updateLocalStocks()
	{
		StockDataEngine.instance().updateLocalStocks("300163", "2017-08-16");
	}
	
	private static void test_getAllStockIDs()
	{
		DEStockIDListObserver observer = new DEStockIDListObserver();
		int error = StockDataEngine.instance().buildAllStockIDObserver(observer);
		CLog.output("TEST", "stock count: %d\n", observer.size());
		for(int i=0; i<observer.size(); i++)
		{
			String stockID = observer.get(i);
			CLog.output("TEST", "stockID: %s\n", stockID);
		}
	}
	
	private static void test_getStockInfo()
	{
		String stockID = "600000";
		DEStockInfoObserver observer = new DEStockInfoObserver();
		int error = StockDataEngine.instance().buildStockInfoObserver(stockID, observer);
		if(0 == error)
		{
			CLog.output("TEST", "cStockInfo [%s][%s]\n",
					stockID, observer.name());
		}
	}
	
	private static void test_getDayKLines()
	{
		String stockID = "600000";
		DEKLineListObserver obsKLineList = new DEKLineListObserver();
		int error = StockDataEngine.instance().buildDayKLineListObserver(stockID, "2014-05-23", "2014-08-15", obsKLineList);
		CLog.output("TEST", "KLine count: %d\n", obsKLineList.size());
		for(int i=0; i<obsKLineList.size(); i++)
		{
			KLine cKLine = obsKLineList.get(i);
			CLog.output("TEST", "date: %s close: %f\n", cKLine.date, cKLine.close);
		}
	}
	
	private static void test_getMinTimePrices()
	{
		CImageCurve cCImageCurve = new CImageCurve(1600,900,"test_getDayDetail.jpg");
		List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
		
		String stockID = "600004";
		DETimePriceListObserver obsTimePriceList = new DETimePriceListObserver();
		int errObsTimePriceList = StockDataEngine.instance().buildMinTimePriceListObserver(stockID, "2016-01-27", 
				"09:00:00", "15:00:00", obsTimePriceList);
		CLog.output("TEST", "KLine count: %d\n", obsTimePriceList.size());
		for(int i=0; i<obsTimePriceList.size(); i++)
		{
			TimePrice cTimePrice = obsTimePriceList.get(i);
			CLog.output("TEST", "time: %s close: %f\n", cTimePrice.time, cTimePrice.price);
			PoiList.add(new CurvePoint(i,cTimePrice.price));
		}
		
		cCImageCurve.writeLogicCurve(PoiList, 1);
		cCImageCurve.GenerateImage();
	}
	
	private static void test_getRealTimePrice()
	{
		TimePrice ctnTimePrice = new TimePrice();
		int error = StockDataEngine.instance().loadRealTimePrice("600000", ctnTimePrice);
		CLog.output("TEST", "err:%d time:%s price:%f\n", error, ctnTimePrice.time, ctnTimePrice.price);
		
	}
	
	public static void main(String[] args) {
		//test_updateAllLocalStocks();
		//test_updateLocalStocks();
		//test_getAllStockIDs();
		//test_getStockInfo();
		//test_getDayKLines();
		test_getMinTimePrices();
		test_getRealTimePrice();
	}
}
