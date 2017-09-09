package pers.di.dataengine.basedata;

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
import pers.di.common.*;
import pers.di.common.CNewTypeDefine.*;

public class BaseDataStorage {
	
	/*
	 * 基础数据存储类
	 * 
	 * 参数：
	 *     dataDir 存储数据路径
	 */
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
	public int saveKLine(String id, List<KLine> container)
	{
		String stocKLineDir = s_workDir + "/" + id;
		if(!CPath.createDir(stocKLineDir)) return -10;
		
		String stockDayKFileName = s_workDir + "/" + id + "/" + s_daykFile;
		File cfile=new File(stockDayKFileName);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			for(int i = 0; i < container.size(); i++)  
	        {  
				KLine cKLine = container.get(i);  
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
	public int saveDividendPayout(String id, List<DividendPayout> container)
	{
		String stocKLineDir = s_workDir + "/" + id;
		if(!CPath.createDir(stocKLineDir)) return -10;
		
		String stockDividendPayoutFileName = s_workDir + "/" + id + "/" + s_DividendPayoutFile;
		File cfile =new File(stockDividendPayoutFileName);
		try
		{
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			for(int i = 0; i < container.size(); i++)  
	        {  
				DividendPayout cDividendPayout = container.get(i);
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
	public int saveDayDetail(String id, String date, List<TradeDetail> container)
	{
		String stocKLineDir = s_workDir + "/" + id;
		if(!CPath.createDir(stocKLineDir)) return -10;
		
		String stocKLineDetailFileName = s_workDir + "/" + id + "/" + date + ".txt";
		try
		{
			File cfile =new File(stocKLineDetailFileName);
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			for(int i = 0; i < container.size(); i++)  
	        {  
				TradeDetail cTradeDetail = container.get(i);  
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
	
	public int getStockInfo(String id, StockInfo container) 
	{
		int error = 0;
		String stockBaseInfoFileName = s_workDir + "/" + id + "/" + s_BaseInfoFile;
		File cfile=new File(stockBaseInfoFileName);
		if(!cfile.exists()) 
		{
			error = -10;
			return error;
		}
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
			String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                //System.out.println("line " + line + ": " + tempString);
            	String[] cols = tempString.split(",");
            	
            	container.name = cols[0];
            	container.date = cols[1];
            	container.time = cols[2];
            	container.allMarketValue = Float.parseFloat(cols[3]);
            	container.circulatedMarketValue = Float.parseFloat(cols[4]);
            	container.peRatio = Float.parseFloat(cols[5]);

                break;
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
	public int saveStockInfo(String id, StockInfo container) 
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
					container.name,container.date,container.time,
					container.allMarketValue, container.circulatedMarketValue, container.peRatio);
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
	
	public int getAllStockFullDataTimestamps(CObjectContainer<String> container)
	{
		int error = 0;
		
		String allStockFullDataTimestampsFile = s_workDir + "/" + s_updateFinish;

		File cfile=new File(allStockFullDataTimestampsFile);
		if(!cfile.exists()) 
		{
			error = -10;
			return error;
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
            	container.set(lineTxt);
            }
            read.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			error = -1;
		}
		return error;
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
