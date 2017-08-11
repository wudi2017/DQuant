package pers.di.dataengine;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import pers.di.dataengine.DataDownload.ResultUpdateStock;
import pers.di.dataengine.DataStorage.ResultStockBaseData;
import pers.di.dataengine.webdata.DataWebCommonDef.KData;
import pers.di.dataengine.webdata.DataWebCommonDef.DividendPayout;
import pers.di.dataengine.webdata.DataWebCommonDef.StockSimpleItem;
import pers.di.dataengine.webdata.DataWebStockAllList;
import pers.di.dataengine.webdata.DataWebStockAllList.ResultAllStockList;
import pers.di.dataengine.webdata.DataWebStockDayK.ResultKData;
import pers.di.dataengine.webdata.DataWebStockDividendPayout.ResultDividendPayout;

public class DataEngine {
	private static DataEngine s_instance = new DataEngine();  
	private DataEngine () 
	{
		
	}
	public static DataEngine instance() {  
		return s_instance;  
	}  
	
	public int initialize(String workDir)
	{
		m_cDataStorage = new DataStorage(workDir);
		m_cDataDownload = new DataDownload(m_cDataStorage);
		return 0;
	}
	
	public int updateLocalAllStockData(String dateStr)
	{
		return m_cDataDownload.downloadAllStockFullData(dateStr);
	}
	
	/*
	 * 前复权日k
	 */
	public ResultKData getKDataForwardAdjusted(String id)
	{
		ResultKData cResultKData = m_cDataStorage.getKData(id);
		ResultDividendPayout cResultDividendPayout = m_cDataStorage.getDividendPayout(id);
		if(0 != cResultKData.error || 0 != cResultDividendPayout.error) 
		{
			cResultKData.error = -10;
			cResultKData.resultList.clear();
			return cResultKData;
		}
		
		for(int i = 0; i < cResultDividendPayout.resultList.size() ; i++)  
		{
			DividendPayout cDividendPayout = cResultDividendPayout.resultList.get(i);  
//			System.out.println(cDividendPayout.date);
//			System.out.println(cDividendPayout.songGu);
//			System.out.println(cDividendPayout.zhuanGu);
//			System.out.println(cDividendPayout.paiXi);
			
			float unitMoreGuRatio = (cDividendPayout.songGu + cDividendPayout.zhuanGu + 10)/10;
			float unitPaiXi = cDividendPayout.paiXi/10;
			
			for(int j = cResultKData.resultList.size() -1; j >= 0 ; j--)
			{
				KData cKData = cResultKData.resultList.get(j); 
				
				if(cKData.date.compareTo(cDividendPayout.date) < 0) // 股票日期小于分红派息日期时，进行重新计算
				{
					cKData.open = (cKData.open - unitPaiXi)/unitMoreGuRatio;
					//cKData.open = (int)(cKData.open*1000)/(float)1000.0;
					
//					System.out.println("date " + cKData.date + " " + cKData.open );
					
					cKData.close = (cKData.close - unitPaiXi)/unitMoreGuRatio;
					//cKData.close = (int)(cKData.close*1000)/(float)1000.0;
					
					cKData.low = (cKData.low - unitPaiXi)/unitMoreGuRatio;
					//cKData.low = (int)(cKData.low*1000)/(float)1000.0;
					
					cKData.high = (cKData.high - unitPaiXi)/unitMoreGuRatio;
					//cKData.high = (int)(cKData.high*1000)/(float)1000.0;	
				}
			}
		}
		return cResultKData;
	}
	
	/*
	 * 后复权日k
	 */
	public ResultKData getKDataBackwardAdjusted(String id)
	{
		ResultKData cResultKData = m_cDataStorage.getKData(id);
		ResultDividendPayout cResultDividendPayout = m_cDataStorage.getDividendPayout(id);
		if(0 != cResultKData.error || 0 != cResultDividendPayout.error) 
		{
			cResultKData.error = -10;
			cResultKData.resultList.clear();
			return cResultKData;
		}
		
		
		for(int i = cResultDividendPayout.resultList.size() -1; i >=0  ; i--)  
		{
			DividendPayout cDividendPayout = cResultDividendPayout.resultList.get(i);  
//			System.out.println(cDividendPayout.date);
//			System.out.println(cDividendPayout.songGu);
//			System.out.println(cDividendPayout.zhuanGu);
//			System.out.println(cDividendPayout.paiXi);
			
			float unitMoreGuRatio = (cDividendPayout.songGu + cDividendPayout.zhuanGu + 10)/10;
			float unitPaiXi = cDividendPayout.paiXi/10;
			
			for(int j = 0; j< cResultKData.resultList.size(); j++)
			{
				KData cKData = cResultKData.resultList.get(j); 
				
				if(cKData.date.compareTo(cDividendPayout.date) >= 0) // 股票日期 大于等于分红派息日期时，进行重新计算
				{
					cKData.open = cKData.open * unitMoreGuRatio + unitPaiXi;
					//cKData.open = (int)(cKData.open*1000)/(float)1000.0;
					
//					System.out.println("date " + cKData.date + " " + cKData.open );
					
					cKData.close = cKData.close * unitMoreGuRatio + unitPaiXi;
					//cKData.close = (int)(cKData.close*1000)/(float)1000.0;
					
					cKData.low = cKData.low * unitMoreGuRatio + unitPaiXi;
					//cKData.low = (int)(cKData.low*1000)/(float)1000.0;
					
					cKData.high = cKData.high * unitMoreGuRatio + unitPaiXi;
					//cKData.high = (int)(cKData.high*1000)/(float)1000.0;	
				}
			}
		}
		
		return cResultKData;
	}
	
	public List<StockSimpleItem> getRandomStockSimpleItem(int count)
	{
		List<StockSimpleItem> retList = new ArrayList<StockSimpleItem>();
		if(0 != count)
		{
			List<StockSimpleItem> retListAll = new ArrayList<StockSimpleItem>();
			
			// emu local
			File root = new File("data");
			File[] fs = root.listFiles();
			if(fs == null)
			{
				fmt.format("[ERROR] not found dir:data\n");
				return null;
			}
			for(int i=0; i<fs.length; i++){
				if(fs[i].isDirectory()){
					String dirName = fs[i].getName();
					if(dirName.length()==6 
						&& (dirName.startsWith("6") || dirName.startsWith("3") || dirName.startsWith("0"))
							)
					{
						StockSimpleItem cStockSimpleItem = new StockSimpleItem();
						cStockSimpleItem.id = dirName;
						retListAll.add(cStockSimpleItem);
					}
					
				}
			}
			
			if(retListAll.size()!=0)
			{
				for(int i = 0; i < count; i++)  
		        {  
					StockSimpleItem cStockSimpleItem = popRandomStock(retListAll);
					retList.add(cStockSimpleItem);
		        } 
			}
		}
		return retList;
	}
	private StockSimpleItem popRandomStock(List<StockSimpleItem> in_list)
	{
		if(in_list.size() == 0) return null;
		
		int randomInt = Math.abs(random.nextInt());
		int randomIndex = randomInt % in_list.size();
		StockSimpleItem cStockSimpleItem = new  StockSimpleItem(in_list.get(randomIndex));
		in_list.remove(randomIndex);
		return cStockSimpleItem;
	}
	
	/*
	 * 校验股票数据,检查股票数据错误
	 * 成功返回0
	 */
	public int checkStockData(String stockID)
	{
		// 检查基本信息
		ResultStockBaseData cResultStockBaseData = m_cDataStorage.getBaseInfo(stockID);
		if(0 != cResultStockBaseData.error 
				|| cResultStockBaseData.stockBaseInfo.name.length() <= 0)
		{
			return -1;
		}
		
		// 检查前复权日K
		ResultKData cResultKData = this.getKDataForwardAdjusted(stockID);
		if(0 != cResultKData.error 
				|| cResultKData.resultList.size() <= 0)
		{
			return -2;
		}
		
		// 检查前复权日K涨跌幅度, 近若干天没有问题就算没有问题
		int iBeginCheck = cResultKData.resultList.size() - 500;
		if(iBeginCheck<=0) iBeginCheck = 0;
		for(int i=iBeginCheck; i < cResultKData.resultList.size()-1; i++)  
        {  
			KData cKData = cResultKData.resultList.get(i);  
			KData cKDataNext = cResultKData.resultList.get(i+1);  
            float close = cKData.close;
            float nextHigh = cKDataNext.high;
            float nextLow = cKDataNext.low;
            float nextClose = cKDataNext.close;
            float fHighper = Math.abs((nextHigh-close)/close);
            float fLowper = Math.abs((nextLow-close)/close);
            float fCloseper = Math.abs((nextClose-close)/close);
            if(fCloseper > 0.12) // 收盘涨跌幅度异常
        	{
            	// 数据有中间丢失天的情况，排除这种错误
            	// 获取当前有效日期，下一个交易日（非周六周日）
            	String CurrentDate = cKData.date;
            	Calendar c = Calendar.getInstance();  
                Date date = null;  
                try {  
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(CurrentDate);  
                } catch (Exception e) {  
                    e.printStackTrace();  
                }  
                c.setTime(date);  
                c.add(Calendar.DATE, 1);
                int cw = c.get(Calendar.DAY_OF_WEEK);
        		while(cw == 1 || cw == 7)
        		{
        			c.add(Calendar.DATE, 1);
        			cw = c.get(Calendar.DAY_OF_WEEK);
        		}
        		Date nextValiddate = c.getTime();
        		String curValiddateStr = new SimpleDateFormat("yyyy-MM-dd").format(nextValiddate);
        		
        		if(cKDataNext.date.compareTo(curValiddateStr) > 0)
        		{
        			// 此种情况允许错误，中间缺失了几天数据
//        			System.out.println("Warnning: Check getKDataQianFuQuan NG(miss data)! id:" + stockID
//                			+ " date:" + cKData.date);
        		}
        		else
        		{
        			// 中间未缺失数据，但出现了偏差过大啊，属于错误
                	System.out.println("Warnning: Check getKDataQianFuQuan error! id:" + stockID
                			+ " date:" + cKData.date);
                	System.out.println("close:" + close);
                	System.out.println("nextHigh:" + nextHigh);
                	System.out.println("fHighper:" + fHighper);
                	System.out.println("nextLow:" + nextLow);
                	System.out.println("fLowper:" + fLowper);
                	return -3;
        		}
        	}
        } 
		return 0;
	}
	
	private DataDownload m_cDataDownload;
	private DataStorage m_cDataStorage;
	
	public static Random random = new Random();
	public static Formatter fmt = new Formatter(System.out);
}
