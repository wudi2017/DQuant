package pers.di.dataengine_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CImageCurve;
import pers.di.common.CLog;
import pers.di.common.CUtilsDateTime;
import pers.di.common.CImageCurve.CurvePoint;
import pers.di.dataengine.BaseDataLayer;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.StockDataEngine.DEStockIDs;
import pers.di.dataengine.StockDataEngine.DETimePrices;
import pers.di.dataengine.StockDataEngine.DEKLines;
import pers.di.dataengine.StockDataEngine.DEStockBaseInfo;
import pers.di.dataengine.webdata.CommonDef.*;

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
		DEStockIDs cDEStockIDs = StockDataEngine.instance().getAllStockIDs();
		CLog.output("TEST", "stock count: %d\n", cDEStockIDs.size());
		for(int i=0; i<cDEStockIDs.size(); i++)
		{
			String stockID = cDEStockIDs.get(i);
			CLog.output("TEST", "stockID: %s\n", stockID);
		}
	}
	
	private static void test_getStockBaseInfo()
	{
		String stockID = "600000";
		DEStockBaseInfo cDEStockBaseInfo = StockDataEngine.instance().getStockBaseInfo(stockID);
		if(0 == cDEStockBaseInfo.error())
		{
			CLog.output("TEST", "cStockInfo [%s][%s]\n",
					stockID, cDEStockBaseInfo.get().name);
		}
	}
	
	private static void test_getDayKLines()
	{
		String stockID = "600000";
		DEKLines cDEKLines = StockDataEngine.instance().getDayKLines(stockID, "2014-05-23", "2014-08-15");
		CLog.output("TEST", "KLine count: %d\n", cDEKLines.size());
		for(int i=0; i<cDEKLines.size(); i++)
		{
			KLine cKLine = cDEKLines.get(i);
			CLog.output("TEST", "date: %s close: %f\n", cKLine.date, cKLine.close);
		}
	}
	
	private static void test_getMinTimePrices()
	{
		CImageCurve cCImageCurve = new CImageCurve(1600,900,"test_getDayDetail.jpg");
		List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
		
		String stockID = "600004";
		DETimePrices cDETimePrices = StockDataEngine.instance().getMinTimePrices(stockID, "2016-01-27", "09:31:00", "14:00:00");
		CLog.output("TEST", "KLine count: %d\n", cDETimePrices.size());
		for(int i=0; i<cDETimePrices.size(); i++)
		{
			TimePrice cTimePrice = cDETimePrices.get(i);
			CLog.output("TEST", "date: %s close: %f\n", cTimePrice.time, cTimePrice.price);
			PoiList.add(new CurvePoint(i,cTimePrice.price));
		}
		
		cCImageCurve.writeLogicCurve(PoiList, 1);
		cCImageCurve.GenerateImage();
		
	}
	
	public static void main(String[] args) {
		//test_updateAllLocalStocks();
		//test_updateLocalStocks();
		//test_getAllStockIDs();
		//test_getStockBaseInfo();
		//test_getDayKLines();
		test_getMinTimePrices();
	}
}
