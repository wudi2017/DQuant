package pers.di.localstock.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.List;

import pers.di.common.*;
import pers.di.localstock.common.*;

public class BaseDataStorage {
	
	/*
	 * 鍩虹鏁版嵁瀛樺偍绫�??
	 * 
	 * 鍙傛暟锛�??
	 *     dataDir 瀛樺偍鏁版嵁璺�??
	 */
	public BaseDataStorage(String dataDir) 
	{
		s_workDir = dataDir;
		CFileSystem.createDir(s_workDir);
	}
	
	public boolean resetDataRoot(String dateRoot)
	{
		s_workDir = dateRoot;
		CFileSystem.createDir(dateRoot);
		return true;
	}
	
	public String dataRoot()
	{
		return s_workDir;
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
		
		String tempString = null;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
			int line = 1;
            while ((tempString = reader.readLine()) != null) {
            	
//                System.out.println("line " + line + ": " + tempString);
//                if(tempString.contains("2017-01-05"))
//                {
//                	System.out.println("line " + line + ": " + tempString);
//                }
                
            	KLine cKLine = new KLine();
            	String[] cols = tempString.split(",");
            	if(cols.length != 6 || cols[cols.length-1].length() <= 0) {
            		System.out.println("line " + line + ": " + tempString);
            	}
            	
            	cKLine.date = cols[0];
	        	cKLine.open = Double.parseDouble(cols[1]);
	        	cKLine.close = Double.parseDouble(cols[2]);
	        	cKLine.low = Double.parseDouble(cols[3]);
	        	cKLine.high = Double.parseDouble(cols[4]);
	        	cKLine.volume = Double.parseDouble(cols[5]);
	        	container.add(cKLine);
	        	
                line++;
            }
            reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("ErrorInfo: BaseDataStorage.getKLine "+ "stockID:" + id + " ParseStr:" + tempString); 
			System.out.println(e.getMessage()); 
			error = -1;
			return error;
		}
		return error;
	}
	public int saveKLine(String id, List<KLine> container)
	{
		String stocKLineDir = s_workDir + "/" + id;
		if(!CFileSystem.createDir(stocKLineDir)) return -10;
		
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
			e.printStackTrace();
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
                cDividendPayout.songGu = Double.parseDouble(cols[1]);
                cDividendPayout.zhuanGu = Double.parseDouble(cols[2]);
                cDividendPayout.paiXi = Double.parseDouble(cols[3]);
                container.add(cDividendPayout);
                
                line++;
            }
            reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage()); 
			error = -1;
			return error;
		}
		return error;
	}
	public int saveDividendPayout(String id, List<DividendPayout> container)
	{
		String stocKLineDir = s_workDir + "/" + id;
		if(!CFileSystem.createDir(stocKLineDir)) return -10;
		
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
			e.printStackTrace();
			System.out.println(e.getMessage()); 
			return -1;
		}
		return 0;
	}
	
	public int getDayDetail(String id, String date, List<TransactionRecord> container)
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

            	TransactionRecord cTradeDetail = new TransactionRecord();
	        	cTradeDetail.time = cols[0];
	        	cTradeDetail.price = Double.parseDouble(cols[1]);
	        	cTradeDetail.volume = Double.parseDouble(cols[2]);
	        	
	        	container.add(cTradeDetail);
	        	
                line++;
            }
            reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage()); 
			error = -1;
			return error;
		}
		return error;
	}
	public int saveDayDetail(String id, String date, List<TransactionRecord> container)
	{
		String stocKLineDir = s_workDir + "/" + id;
		if(!CFileSystem.createDir(stocKLineDir)) return -10;
		
		String stocKLineDetailFileName = s_workDir + "/" + id + "/" + date + ".txt";
		try
		{
			File cfile =new File(stocKLineDetailFileName);
			FileOutputStream cOutputStream = new FileOutputStream(cfile);
			for(int i = 0; i < container.size(); i++)  
	        {  
				TransactionRecord cTradeDetail = container.get(i);  
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
			e.printStackTrace();
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
            	container.allMarketValue = Double.parseDouble(cols[3]);
            	container.circulatedMarketValue = Double.parseDouble(cols[4]);
            	container.peRatio = Double.parseDouble(cols[5]);

                break;
            }
            reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage()); 
			error = -1;
			return error;
		}
		return error;
	}
	public int saveStockInfo(String id, StockInfo container) 
	{
		String stocKLineDir = s_workDir + "/" + id;
		if(!CFileSystem.createDir(stocKLineDir)) return -10;
		
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
			System.out.println(e.getMessage()); 
			return false;
		}
		return true;
	}
	
	/*
	 * get local stocks form data dir that download
	 * transfer all stock folders
	 */
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
	/*
	 * get stocklist form file, that export from tongdaxin client
	 */
	public int getStockListFromLocalFile(List<StockItem> container) {
		int error = 0;
		String stocklistfilename = s_workDir + "/" + s_stocklist;
		File stockListfile = new File(stocklistfilename);
		if(!stockListfile.exists())
		{
			CLog.output("DATAAPI", "getStockListFromLocalFile failed, not found: %s\n", stocklistfilename);
			error = -1;
			return error;
		}

		String tempString = "";
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(stockListfile));
			int line = 0;
			String[] heads = null;
			int iStockID=-1, iStockName=-1;
            while ((tempString = reader.readLine()) != null) {
            	
                if(0 == line) {
                	heads = tempString.split(",|\t");
                	if (heads.length < 2) {
                		throw new Exception("parse error!");
                	}
                	for(int i=0;i<heads.length;i++) {
                		if(heads[i].equals("代码")) {
                			iStockID = i;
                		}
                		if(heads[i].equals("名称")) {
                			iStockName = i;
                		}
                	}
                	if(-1 == iStockID || -1 == iStockName) {
                		throw new Exception("parse error!");
                	}
                }
                
                if(0 < line) {
	            	String[] cols = tempString.split(",|\t");
	            	if (cols.length != heads.length) {
	            		continue;
	            	}
	            	
					StockItem cStockItem = new StockItem();
					cStockItem.id = cols[iStockID];
					cStockItem.name = cols[iStockName];
					container.add(cStockItem);
                }

                line++;
            }
            
            reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("ErrorInfo: BaseDataStorage.getStockListFromLocalFile ParseStr " + tempString); 
			System.out.println(e.getMessage()); 
			error = -1;
			return error;
		}
		
		return error;
	}
	
	private String s_workDir = "data";
	private String s_stocklist = "stock_list.txt";
	private String s_updateFinish = "updateFinish.txt";
	
	private String s_daykFile = "dayk.txt";
	private String s_DividendPayoutFile = "dividendPayout.txt";
	private String s_BaseInfoFile = "baseInfo.txt";
	
	private Formatter s_fmt = new Formatter(System.out);
}
