package pers.di.dataengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.List;

import pers.di.dataengine.webdata.DataWebStockDayDetail;
import pers.di.dataengine.webdata.DataWebStockDividendPayout;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo;
import pers.di.dataengine.common.*;
import pers.di.common.CPath;

public class BaseDataStorage {
	
	public BaseDataStorage(String dataDir) 
	{
		s_workDir = dataDir;
		CPath.createDir(s_workDir);
	}

	public int getKLine(String id, List<KLine> container)
	{
		int error = 0;
		
		String stockDayKFileName = s_workDir + "/" + id + "/" + s_daykFile;
		File cfile=new File(stockDayKFileName);

		if(!cfile.exists())
		{
			error = -10;
			return error;
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
                
            	KLine cKLine = new KLine();
            	String[] cols = tempString.split(",");
            	
            	cKLine.date = cols[0];
	        	cKLine.open = Float.parseFloat(cols[1]);
	        	cKLine.close = Float.parseFloat(cols[2]);
	        	cKLine.low = Float.parseFloat(cols[3]);
	        	cKLine.high = Float.parseFloat(cols[4]);
	        	cKLine.volume = Float.parseFloat(cols[5]);
	        	container.add(cKLine);
	        	
                line++;
            }
            reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			error = -1;
			return error;
		}
		return error;
	}
	public int saveKLine(String id, List<KLine> in_list)
	{
		String stocKLineDir = s_workDir + "/" + id;
		if(!CPath.createDir(stocKLineDir)) return -10;
		
		String stockDayKFileName = s_workDir + "/" + id + "/" + s_daykFile;
		File cfile=new File(stockDayKFileName);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			for(int i = 0; i < in_list.size(); i++)  
	        {  
				KLine cKLine = in_list.get(i);  
//		            System.out.println(cKLine.date + "," 
//		            		+ cKLine.open + "," + cKLine.close);  
	            cOutputStream.write((cKLine.date + ",").getBytes());
	            cOutputStream.write((cKLine.open + ",").getBytes());
	            cOutputStream.write((cKLine.close + ",").getBytes());
	            cOutputStream.write((cKLine.low + ",").getBytes());
	            cOutputStream.write((cKLine.high + ",").getBytes());
	            cOutputStream.write((cKLine.volume + "\n").getBytes());
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
	
	public int getDividendPayout(String id, List<DividendPayout> container)
	{
		int error = 0;
		
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
			error = -10;
			return error;
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
                container.add(cDividendPayout);
                
                line++;
            }
            reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			error = -1;
			return error;
		}
		return error;
	}
	public int saveDividendPayout(String id, List<DividendPayout> in_list)
	{
		String stocKLineDir = s_workDir + "/" + id;
		if(!CPath.createDir(stocKLineDir)) return -10;
		
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
	
	public int getDayDetail(String id, String date, List<TradeDetail> container)
	{
		int error = 0;
		
		String stocKLineDetailFileName = s_workDir + "/" + id + "/" + date + ".txt";
		File cfile=new File(stocKLineDetailFileName);

		if(!cfile.exists()) 
		{
			error = -10;
			return error;
		}
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
			int line = 1;
			String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                // System.out.println("line " + line + ": " + tempString);
            	KLine cKLine = new KLine();
            	String[] cols = tempString.split(",");

            	TradeDetail cTradeDetail = new TradeDetail();
	        	cTradeDetail.time = cols[0];
	        	cTradeDetail.price = Float.parseFloat(cols[1]);
	        	cTradeDetail.volume = Float.parseFloat(cols[2]);
	        	
	        	container.add(cTradeDetail);
	        	
                line++;
            }
            reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			error = -1;
			return error;
		}
		return error;
	}
	public int saveDayDetail(String id, String date, List<TradeDetail> in_list)
	{
		String stocKLineDir = s_workDir + "/" + id;
		if(!CPath.createDir(stocKLineDir)) return -10;
		
		String stocKLineDetailFileName = s_workDir + "/" + id + "/" + date + ".txt";
		try
		{
			File cfile =new File(stocKLineDetailFileName);
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			for(int i = 0; i < in_list.size(); i++)  
	        {  
				TradeDetail cTradeDetail = in_list.get(i);  
//			            System.out.println(cTradeDetail.time + "," 
//			            		+ cTradeDetail.price + "," + cTradeDetail.volume);  
				cOutputStream.write((cTradeDetail.time + ",").getBytes());
				cOutputStream.write((cTradeDetail.price + ",").getBytes());
				cOutputStream.write((cTradeDetail.volume + "\n").getBytes());
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
			stockBaseInfo = new StockInfo();
		}
		public int error;
		public StockInfo stockBaseInfo;
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
            	cResultStockBaseData.stockBaseInfo.date = cols[1];
            	cResultStockBaseData.stockBaseInfo.time = cols[2];
            	cResultStockBaseData.stockBaseInfo.allMarketValue = Float.parseFloat(cols[3]);
            	cResultStockBaseData.stockBaseInfo.circulatedMarketValue = Float.parseFloat(cols[4]);
            	cResultStockBaseData.stockBaseInfo.peRatio = Float.parseFloat(cols[5]);

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
	public int saveBaseInfo(String id, StockInfo baseData) 
	{
		String stocKLineDir = s_workDir + "/" + id;
		if(!CPath.createDir(stocKLineDir)) return -10;
		
		String stockBaseInfoFileName = stocKLineDir + "/" + s_BaseInfoFile;
		
		File cfile =new File(stockBaseInfoFileName);
		// System.out.println("saveStockBaseData:" + id);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			String s = String.format("%s,%s,%s,%.3f,%.3f,%.3f", 
					baseData.name,baseData.date,baseData.time,
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
	
	
	public static class ResultAllStockFullDataTimestamps
	{
		public ResultAllStockFullDataTimestamps()
		{
			error = 0;
			date = "0000-00-00";
		}
		public int error;
		public String date;
	}
	public ResultAllStockFullDataTimestamps getAllStockFullDataTimestamps()
	{
		ResultAllStockFullDataTimestamps cResultUpdatedStocksDate = new ResultAllStockFullDataTimestamps();
		
		String dateStr = null;
		String allStockFullDataTimestampsFile = s_workDir + "/" + s_updateFinish;

		File cfile=new File(allStockFullDataTimestampsFile);
		if(!cfile.exists()) 
		{
			cResultUpdatedStocksDate.error = -10;
			return cResultUpdatedStocksDate;
		}
		
		try
		{
			String encoding = "utf-8";
			InputStreamReader read = new InputStreamReader(new FileInputStream(cfile),encoding);
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = bufferedReader.readLine();
            lineTxt = lineTxt.trim().replace("\n", "");
            if(lineTxt.length() == "0000-00-00".length())
            {
            	cResultUpdatedStocksDate.date = lineTxt;
            }
            read.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			cResultUpdatedStocksDate.error = -1;
		}
		return cResultUpdatedStocksDate;
	}
	public boolean saveAllStockFullDataTimestamps(String dateStr)
	{
		String allStockFullDataTimestampsFile = s_workDir + "/" + s_updateFinish;
	
		File cfile =new File(allStockFullDataTimestampsFile);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
	        cOutputStream.write(dateStr.getBytes());
			cOutputStream.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return false;
		}
		return true;
	}
	
	
	public int getLocalAllStock(List<StockItem> container)
	{
		int error = 0;
		// emu local
		File root = new File(s_workDir);
		File[] fs = root.listFiles();
		if(fs == null)
		{
			s_fmt.format("[ERROR] not found dir:data\n");
			error = -10;
			return error;
		}
		for(int i=0; i<fs.length; i++){
			if(fs[i].isDirectory()){
				String dirName = fs[i].getName();
				if(dirName.length()==6 
					&& (dirName.startsWith("6") || dirName.startsWith("3") || dirName.startsWith("0"))
						)
				{
					StockItem cStockItem = new StockItem();
					cStockItem.id = dirName;
					container.add(cStockItem);
				}
				
			}
		}
		return error;
	}
	
	private String s_workDir = "data";
	private String s_updateFinish = "updateFinish.txt";
	
	private String s_daykFile = "dayk.txt";
	private String s_DividendPayoutFile = "dividendPayout.txt";
	private String s_BaseInfoFile = "baseInfo.txt";
	
	private Formatter s_fmt = new Formatter(System.out);
}
