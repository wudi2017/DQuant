package pers.di.dataengine;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import pers.di.dataengine.DataDownload.ResultUpdateStock;
import pers.di.dataengine.DataStorage.ResultStockBaseData;
import pers.di.dataengine.webdata.DataWebCommonDef.DayKData;
import pers.di.dataengine.webdata.DataWebCommonDef.DividendPayout;
import pers.di.dataengine.webdata.DataWebCommonDef.StockSimpleItem;
import pers.di.dataengine.webdata.DataWebStockAllList;
import pers.di.dataengine.webdata.DataWebStockAllList.ResultAllStockList;
import pers.di.dataengine.webdata.DataWebStockDayK.ResultDayKData;
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
	public ResultDayKData getDayKDataForwardAdjusted(String id)
	{
		ResultDayKData cResultDayKData = m_cDataStorage.getDayKData(id);
		ResultDividendPayout cResultDividendPayout = m_cDataStorage.getDividendPayout(id);
		if(0 != cResultDayKData.error || 0 != cResultDividendPayout.error) 
		{
			cResultDayKData.error = -10;
			cResultDayKData.resultList.clear();
			return cResultDayKData;
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
			
			for(int j = cResultDayKData.resultList.size() -1; j >= 0 ; j--)
			{
				DayKData cDayKData = cResultDayKData.resultList.get(j); 
				
				if(cDayKData.date.compareTo(cDividendPayout.date) < 0) // 股票日期小于分红派息日期时，进行重新计算
				{
					cDayKData.open = (cDayKData.open - unitPaiXi)/unitMoreGuRatio;
					//cDayKData.open = (int)(cDayKData.open*1000)/(float)1000.0;
					
//					System.out.println("date " + cDayKData.date + " " + cDayKData.open );
					
					cDayKData.close = (cDayKData.close - unitPaiXi)/unitMoreGuRatio;
					//cDayKData.close = (int)(cDayKData.close*1000)/(float)1000.0;
					
					cDayKData.low = (cDayKData.low - unitPaiXi)/unitMoreGuRatio;
					//cDayKData.low = (int)(cDayKData.low*1000)/(float)1000.0;
					
					cDayKData.high = (cDayKData.high - unitPaiXi)/unitMoreGuRatio;
					//cDayKData.high = (int)(cDayKData.high*1000)/(float)1000.0;	
				}
			}
		}
		return cResultDayKData;
	}
	
	/*
	 * 后复权日k
	 */
	public ResultDayKData getDayKDataBackwardAdjusted(String id)
	{
		ResultDayKData cResultDayKData = m_cDataStorage.getDayKData(id);
		ResultDividendPayout cResultDividendPayout = m_cDataStorage.getDividendPayout(id);
		if(0 != cResultDayKData.error || 0 != cResultDividendPayout.error) 
		{
			cResultDayKData.error = -10;
			cResultDayKData.resultList.clear();
			return cResultDayKData;
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
			
			for(int j = 0; j< cResultDayKData.resultList.size(); j++)
			{
				DayKData cDayKData = cResultDayKData.resultList.get(j); 
				
				if(cDayKData.date.compareTo(cDividendPayout.date) >= 0) // 股票日期 大于等于分红派息日期时，进行重新计算
				{
					cDayKData.open = cDayKData.open * unitMoreGuRatio + unitPaiXi;
					//cDayKData.open = (int)(cDayKData.open*1000)/(float)1000.0;
					
//					System.out.println("date " + cDayKData.date + " " + cDayKData.open );
					
					cDayKData.close = cDayKData.close * unitMoreGuRatio + unitPaiXi;
					//cDayKData.close = (int)(cDayKData.close*1000)/(float)1000.0;
					
					cDayKData.low = cDayKData.low * unitMoreGuRatio + unitPaiXi;
					//cDayKData.low = (int)(cDayKData.low*1000)/(float)1000.0;
					
					cDayKData.high = cDayKData.high * unitMoreGuRatio + unitPaiXi;
					//cDayKData.high = (int)(cDayKData.high*1000)/(float)1000.0;	
				}
			}
		}
		
		return cResultDayKData;
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
		ResultDayKData cResultDayKData = this.getDayKDataForwardAdjusted(stockID);
		if(0 != cResultDayKData.error 
				|| cResultDayKData.resultList.size() <= 0)
		{
			return -2;
		}
		
		// 检查前复权日K涨跌幅度, 近若干天没有问题就算没有问题
		int iBeginCheck = cResultDayKData.resultList.size() - 500;
		if(iBeginCheck<=0) iBeginCheck = 0;
		for(int i=iBeginCheck; i < cResultDayKData.resultList.size()-1; i++)  
        {  
			DayKData cDayKData = cResultDayKData.resultList.get(i);  
			DayKData cDayKDataNext = cResultDayKData.resultList.get(i+1);  
            float close = cDayKData.close;
            float nextHigh = cDayKDataNext.high;
            float nextLow = cDayKDataNext.low;
            float nextClose = cDayKDataNext.close;
            float fHighper = Math.abs((nextHigh-close)/close);
            float fLowper = Math.abs((nextLow-close)/close);
            float fCloseper = Math.abs((nextClose-close)/close);
            if(fCloseper > 0.12) // 收盘涨跌幅度异常
        	{
            	// 数据有中间丢失天的情况，排除这种错误
            	// 获取当前有效日期，下一个交易日（非周六周日）
            	String CurrentDate = cDayKData.date;
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
        		
        		if(cDayKDataNext.date.compareTo(curValiddateStr) > 0)
        		{
        			// 此种情况允许错误，中间缺失了几天数据
//        			System.out.println("Warnning: Check getDayKDataQianFuQuan NG(miss data)! id:" + stockID
//                			+ " date:" + cDayKData.date);
        		}
        		else
        		{
        			// 中间未缺失数据，但出现了偏差过大啊，属于错误
                	System.out.println("Warnning: Check getDayKDataQianFuQuan error! id:" + stockID
                			+ " date:" + cDayKData.date);
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
