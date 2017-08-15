package pers.di.dataengine_test;

import java.util.ArrayList;
import java.util.List;

import pers.di.common.CImageCurve;
import pers.di.common.CLog;
import pers.di.common.CUtilsDateTime;
import pers.di.common.CImageCurve.CurvePoint;
import pers.di.dataengine.BaseDataLayer;
import pers.di.dataengine.StockDataEngine;
import pers.di.dataengine.StockDataEngine.ResultAllStockID;
import pers.di.dataengine.StockDataEngine.ResultDayDetail;
import pers.di.dataengine.StockDataEngine.ResultDayKLine;
import pers.di.dataengine.StockDataEngine.ResultLatestStockBaseInfo;
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
	
	private static void test_getAllStockID()
	{
		ResultAllStockID cResultAllStockID = StockDataEngine.instance().getAllStockID();
		List<String> stockIDList = cResultAllStockID.resultList;
		CLog.output("TEST", "stock count: %d\n", stockIDList.size());
		for(int i=0; i<stockIDList.size(); i++)
		{
			String stockID = stockIDList.get(i);
			CLog.output("TEST", "stockID: %s\n", stockID);
		}
	}
	
	private static void test_getLatestStockBaseInfo()
	{
		String stockID = "600000";
		ResultLatestStockBaseInfo cResultLatestStockBaseInfo = StockDataEngine.instance().getLatestStockBaseInfo(stockID);
		if(0 == cResultLatestStockBaseInfo.error)
		{
			CLog.output("TEST", "cStockInfo [%s][%s]\n",
					stockID, cResultLatestStockBaseInfo.stockBaseInfo.name);
		}
	}
	
	private static void test_getDayKLine()
	{
		String stockID = "600000";
		ResultDayKLine cResultDayKLine = StockDataEngine.instance().getDayKLine(stockID, "2014-05-23", "2014-08-15");
		CLog.output("TEST", "KLine count: %d\n", cResultDayKLine.resultList.size());
		for(int i=0; i<cResultDayKLine.resultList.size(); i++)
		{
			KLine cKLine = cResultDayKLine.resultList.get(i);
			CLog.output("TEST", "date: %s close: %f\n", cKLine.date, cKLine.close);
		}
	}
	
	private static void test_getDayDetail()
	{
		CImageCurve cCImageCurve = new CImageCurve(1600,900,"test_getDayDetail.jpg");
		List<CurvePoint> PoiList = new ArrayList<CurvePoint>();
		
		String stockID = "600004";
		ResultDayDetail cResultDayDetail = StockDataEngine.instance().getDayDetail(stockID, "2016-01-27", "09:00:00", "15:00:00");
		CLog.output("TEST", "KLine count: %d\n", cResultDayDetail.resultList.size());
		for(int i=0; i<cResultDayDetail.resultList.size(); i++)
		{
			StockTime cStockTime = cResultDayDetail.resultList.get(i);
			CLog.output("TEST", "date: %s close: %f\n", cStockTime.time, cStockTime.price);
			PoiList.add(new CurvePoint(i,cStockTime.price));
		}
		
		cCImageCurve.writeLogicCurve(PoiList, 1);
		cCImageCurve.GenerateImage();
		
	}
	
	public static void main(String[] args) {
		//test_updateAllLocalStocks();
		//test_updateLocalStocks();
		//test_getAllStockID();
		//test_getLatestStockBaseInfo();
		//test_getDayKLine();
		//test_getDayDetail();
	}
}
