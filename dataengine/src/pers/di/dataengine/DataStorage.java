package pers.di.dataengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Formatter;
import java.util.List;

import pers.di.dataengine.webdata.DataWebStockDayDetail;
import pers.di.dataengine.webdata.DataWebStockDividendPayout;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo;
import pers.di.dataengine.webdata.DataWebCommonDef.*;
import pers.di.dataengine.webdata.DataWebStockDayDetail.ResultDayDetail;
import pers.di.common.CPath;
import pers.di.dataengine.webdata.DataWebStockDayK.ResultDayKData;
import pers.di.dataengine.webdata.DataWebStockDividendPayout.ResultDividendPayout;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo.ResultRealTimeInfo;

public class DataStorage {
	
	public DataStorage(String dataDir) 
	{
		s_workDir = dataDir;
		CPath.createDir(s_workDir);
	}

	public ResultDayKData getDayKData(String id)
	{
		ResultDayKData cResultDayKData = new ResultDayKData();
		
		String stockDayKFileName = s_workDir + "/" + id + "/" + s_daykFile;
		File cfile=new File(stockDayKFileName);

		if(!cfile.exists())
		{
			cResultDayKData.error = -10;
			return cResultDayKData;
		}
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
			int line = 1;
			String tempString = null;
            while ((tempString = reader.readLine()) != null) {
            	
//                System.out.println("line " + line + ": " + tempString);
//                if(tempString.contains("2017-01-05"))
//                {
//                	System.out.println("line " + line + ": " + tempString);
//                }
                
            	DayKData cDayKData = new DayKData();
            	String[] cols = tempString.split(",");
            	
            	cDayKData.date = cols[0];
	        	cDayKData.open = Float.parseFloat(cols[1]);
	        	cDayKData.close = Float.parseFloat(cols[2]);
	        	cDayKData.low = Float.parseFloat(cols[3]);
	        	cDayKData.high = Float.parseFloat(cols[4]);
	        	cDayKData.volume = Float.parseFloat(cols[5]);
	        	cResultDayKData.resultList.add(cDayKData);
	        	
                line++;
            }
            reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			cResultDayKData.error = -1;
			return cResultDayKData;
		}
		return cResultDayKData;
	}
	public int saveDayKData(String id, List<DayKData> in_list)
	{
		String stockDataDir = s_workDir + "/" + id;
		if(!CPath.createDir(stockDataDir)) return -10;
		
		String stockDayKFileName = s_workDir + "/" + id + "/" + s_daykFile;
		File cfile=new File(stockDayKFileName);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			for(int i = 0; i < in_list.size(); i++)  
	        {  
				DayKData cDayKData = in_list.get(i);  
//		            System.out.println(cDayKData.date + "," 
//		            		+ cDayKData.open + "," + cDayKData.close);  
	            cOutputStream.write((cDayKData.date + ",").getBytes());
	            cOutputStream.write((cDayKData.open + ",").getBytes());
	            cOutputStream.write((cDayKData.close + ",").getBytes());
	            cOutputStream.write((cDayKData.low + ",").getBytes());
	            cOutputStream.write((cDayKData.high + ",").getBytes());
	            cOutputStream.write((cDayKData.volume + "\n").getBytes());
	        } 
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return -1;
		}
		return 0;
	}
	
	public ResultDividendPayout getDividendPayout(String id)
	{
		ResultDividendPayout cResultDividendPayout = new ResultDividendPayout();
		
		String stockDividendPayoutFileName = s_workDir + "/" + id + "/" + s_DividendPayoutFile;
		File cfile=new File(stockDividendPayoutFileName);
//		if(!cfile.exists())
//		{
//			int iDownLoad = downloadStockDividendPayout(id);
//			if(0 != iDownLoad)
//			{
//				cResultDividendPayout.error = -21;
//				return cResultDividendPayout;
//			}
//		}
		if(!cfile.exists()) 
		{
			cResultDividendPayout.error = -10;
			return cResultDividendPayout;
		}
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
			int line = 1;
			String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                //System.out.println("line " + line + ": " + tempString);
            	String[] cols = tempString.split(",");
            	
            	DividendPayout cDividendPayout = new DividendPayout();
            	cDividendPayout.date = cols[0];
                cDividendPayout.songGu = Float.parseFloat(cols[1]);
                cDividendPayout.zhuanGu = Float.parseFloat(cols[2]);
                cDividendPayout.paiXi = Float.parseFloat(cols[3]);
                cResultDividendPayout.resultList.add(cDividendPayout);
                
                line++;
            }
            reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			cResultDividendPayout.error = -1;
			return cResultDividendPayout;
		}
		return cResultDividendPayout;
	}
	public int saveDividendPayout(String id, List<DividendPayout> in_list)
	{
		String stockDataDir = s_workDir + "/" + id;
		if(!CPath.createDir(stockDataDir)) return -10;
		
		String stockDividendPayoutFileName = s_workDir + "/" + id + "/" + s_DividendPayoutFile;
		File cfile =new File(stockDividendPayoutFileName);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			for(int i = 0; i < in_list.size(); i++)  
	        {  
				DividendPayout cDividendPayout = in_list.get(i);
				// System.out.println(cDividendPayout.date); 
				cOutputStream.write((cDividendPayout.date + ",").getBytes());
				cOutputStream.write((cDividendPayout.songGu + ",").getBytes());
				cOutputStream.write((cDividendPayout.zhuanGu + ",").getBytes());
				cOutputStream.write((cDividendPayout.paiXi + "\n").getBytes());
	        } 
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return -1;
		}
		return 0;
	}
	
	public ResultDayDetail getDayDetail(String id, String date)
	{
		ResultDayDetail cResultDayDetail = new ResultDayDetail();
				
		String stockDataDetailFileName = s_workDir + "/" + id + "/" + date + ".txt";
		File cfile=new File(stockDataDetailFileName);

		if(!cfile.exists()) 
		{
			cResultDayDetail.error = -10;
			return cResultDayDetail;
		}
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
			int line = 1;
			String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                // System.out.println("line " + line + ": " + tempString);
            	DayKData cDayKData = new DayKData();
            	String[] cols = tempString.split(",");

            	DayDetailItem cDayDetailItem = new DayDetailItem();
	        	cDayDetailItem.time = cols[0];
	        	cDayDetailItem.price = Float.parseFloat(cols[1]);
	        	cDayDetailItem.volume = Float.parseFloat(cols[2]);
	        	
	        	cResultDayDetail.resultList.add(cDayDetailItem);
	        	
                line++;
            }
            reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			cResultDayDetail.error = -1;
			return cResultDayDetail;
		}
		return cResultDayDetail;
	}
	public int saveDayDetail(String id, String date, List<DayDetailItem> in_list)
	{
		String stockDataDir = s_workDir + "/" + id;
		if(!CPath.createDir(stockDataDir)) return -10;
		
		String stockDataDetailFileName = s_workDir + "/" + id + "/" + date + ".txt";
		try
		{
			File cfile =new File(stockDataDetailFileName);
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			for(int i = 0; i < in_list.size(); i++)  
	        {  
				DayDetailItem cDayDetailItem = in_list.get(i);  
//			            System.out.println(cDayDetailItem.time + "," 
//			            		+ cDayDetailItem.price + "," + cDayDetailItem.volume);  
				cOutputStream.write((cDayDetailItem.time + ",").getBytes());
				cOutputStream.write((cDayDetailItem.price + ",").getBytes());
				cOutputStream.write((cDayDetailItem.volume + "\n").getBytes());
	        } 
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return -1;
		}

		return 0;
	}
	
	public static class ResultStockBaseData
	{
		public ResultStockBaseData()
		{
			error = 0;
			stockBaseInfo = new StockBaseInfo();
		}
		public int error;
		public StockBaseInfo stockBaseInfo;
	}
	public ResultStockBaseData getBaseInfo(String id) 
	{
		ResultStockBaseData cResultStockBaseData = new ResultStockBaseData();
		
		String stockBaseInfoFileName = s_workDir + "/" + id + "/" + s_BaseInfoFile;
		File cfile=new File(stockBaseInfoFileName);
		if(!cfile.exists()) 
		{
			cResultStockBaseData.error = -10;
			return cResultStockBaseData;
		}
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
			String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                //System.out.println("line " + line + ": " + tempString);
            	String[] cols = tempString.split(",");
            	
            	cResultStockBaseData.stockBaseInfo.name = cols[0];
            	cResultStockBaseData.stockBaseInfo.price = Float.parseFloat(cols[1]);
            	cResultStockBaseData.stockBaseInfo.allMarketValue = Float.parseFloat(cols[2]);
            	cResultStockBaseData.stockBaseInfo.circulatedMarketValue = Float.parseFloat(cols[3]);
            	cResultStockBaseData.stockBaseInfo.peRatio = Float.parseFloat(cols[4]);

                break;
            }
            reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			cResultStockBaseData.error = -1;
			return cResultStockBaseData;
		}
		return cResultStockBaseData;
	}
	public int saveBaseInfo(String id, StockBaseInfo baseData) 
	{
		String stockDataDir = s_workDir + "/" + id;
		if(!CPath.createDir(stockDataDir)) return -10;
		
		String stockBaseInfoFileName = stockDataDir + "/" + s_BaseInfoFile;
		
		File cfile =new File(stockBaseInfoFileName);
		// System.out.println("saveStockBaseData:" + id);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			String s = String.format("%s,%.3f,%.3f,%.3f,%.3f", 
					baseData.name, baseData.price, 
					baseData.allMarketValue, baseData.circulatedMarketValue, baseData.peRatio);
			cOutputStream.write(s.getBytes());
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return -1;
		}
		return 0;
	}
	
	private String s_workDir = "data";
	private String s_updateFinish = "updateFinish.txt";
	
	private String s_daykFile = "dayk.txt";
	private String s_DividendPayoutFile = "dividendPayout.txt";
	private String s_BaseInfoFile = "baseInfo.txt";
	
	private Formatter s_fmt = new Formatter(System.out);
}
