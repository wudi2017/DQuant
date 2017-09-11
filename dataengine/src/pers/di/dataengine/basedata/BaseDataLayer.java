package pers.di.dataengine.basedata;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import pers.di.common.*;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo;
import pers.di.dataengine.common.*;

/*
 * 股票基础数据层
 */
public class BaseDataLayer { 
	public BaseDataLayer (String workDir) 
	{
		m_cBaseDataStorage = new BaseDataStorage(workDir);
		m_cBaseDataDownload = new BaseDataDownload(m_cBaseDataStorage);
	}
	
	/*
	 * 更新所有股票数据到指定日期
	 * 参数
	 *     dateStr： 指定日期 
	 */
	public int updateLocalAllStocKLine(String dateStr)
	{
		return m_cBaseDataDownload.downloadAllStockFullData(dateStr);
	}
	// 获取本地总体数据更新时间
	public int getAllStockFullDataTimestamps(CObjectContainer<String> container)
	{
		return m_cBaseDataStorage.getAllStockFullDataTimestamps(container);
	}
	
	/*
	 * 更新某一只股票
	 */
	public int updateLocaStocKLine(String stockId, CObjectContainer<Integer> container)
	{
		return m_cBaseDataDownload.downloadStockFullData(stockId, container);
	}
	
	
	/*
	 * 获取本地股票列表
	 */
	public int getLocalAllStock(ArrayList<StockItem> container)
	{
		return m_cBaseDataStorage.getLocalAllStock(container);
	}
	
	/*
	 * 获取某只股票基础信息
	 */
	public int getStockInfo(String id, StockInfo container) 
	{
		return m_cBaseDataStorage.getStockInfo(id, container);
	}
	
	/*
	 * 获取某只股票日K
	 */
	public int getDayKLines(String id, List<KLine> container)
	{
		return m_cBaseDataStorage.getKLine(id, container);
	}
	
	/*
	 * 获取前复权日k
	 */
	public int getDayKLinesForwardAdjusted(String id, List<KLine> container)
	{
		int error = 0;
		
		int errKline = m_cBaseDataStorage.getKLine(id, container);
		List<DividendPayout> ctnDividendPayout = new ArrayList<DividendPayout>();
		int errDividendPayout = m_cBaseDataStorage.getDividendPayout(id, ctnDividendPayout);
		if(0 != errKline || 0 != errDividendPayout) 
		{
			error = -10;
			container.clear();
			return error;
		}
		
		for(int i = 0; i < ctnDividendPayout.size() ; i++)  
		{
			DividendPayout cDividendPayout = ctnDividendPayout.get(i);  
//			System.out.println(cDividendPayout.date);
//			System.out.println(cDividendPayout.songGu);
//			System.out.println(cDividendPayout.zhuanGu);
//			System.out.println(cDividendPayout.paiXi);
			
			float unitMoreGuRatio = (cDividendPayout.songGu + cDividendPayout.zhuanGu + 10)/10;
			float unitPaiXi = cDividendPayout.paiXi/10;
			
			for(int j = container.size() -1; j >= 0 ; j--)
			{
				KLine cKLine = container.get(j); 
				
				if(cKLine.date.compareTo(cDividendPayout.date) < 0) // 股票日期小于分红派息日期时，进行重新计算
				{
					cKLine.open = (cKLine.open - unitPaiXi)/unitMoreGuRatio;
					//cKLine.open = (int)(cKLine.open*1000)/(float)1000.0;
					
//					System.out.println("date " + cKLine.date + " " + cKLine.open );
					
					cKLine.close = (cKLine.close - unitPaiXi)/unitMoreGuRatio;
					//cKLine.close = (int)(cKLine.close*1000)/(float)1000.0;
					
					cKLine.low = (cKLine.low - unitPaiXi)/unitMoreGuRatio;
					//cKLine.low = (int)(cKLine.low*1000)/(float)1000.0;
					
					cKLine.high = (cKLine.high - unitPaiXi)/unitMoreGuRatio;
					//cKLine.high = (int)(cKLine.high*1000)/(float)1000.0;	
				}
			}
		}
		return error;
	}
	
	/*
	 * 获取后复权日k
	 */
	public int getDayKLinesBackwardAdjusted(String id, List<KLine> container)
	{
		int error = 0;
		
		int errKline =  m_cBaseDataStorage.getKLine(id, container);
		List<DividendPayout> ctnDividendPayout = new ArrayList<DividendPayout>();
		int errDividendPayout = m_cBaseDataStorage.getDividendPayout(id, ctnDividendPayout);
		if(0 != errKline || 0 != errDividendPayout) 
		{
			error = -10;
			container.clear();
			return error;
		}
		
		
		for(int i = ctnDividendPayout.size() -1; i >=0  ; i--)  
		{
			DividendPayout cDividendPayout = ctnDividendPayout.get(i);  
//			System.out.println(cDividendPayout.date);
//			System.out.println(cDividendPayout.songGu);
//			System.out.println(cDividendPayout.zhuanGu);
//			System.out.println(cDividendPayout.paiXi);
			
			float unitMoreGuRatio = (cDividendPayout.songGu + cDividendPayout.zhuanGu + 10)/10;
			float unitPaiXi = cDividendPayout.paiXi/10;
			
			for(int j = 0; j< container.size(); j++)
			{
				KLine cKLine = container.get(j); 
				
				if(cKLine.date.compareTo(cDividendPayout.date) >= 0) // 股票日期 大于等于分红派息日期时，进行重新计算
				{
					cKLine.open = cKLine.open * unitMoreGuRatio + unitPaiXi;
					//cKLine.open = (int)(cKLine.open*1000)/(float)1000.0;
					
//					System.out.println("date " + cKLine.date + " " + cKLine.open );
					
					cKLine.close = cKLine.close * unitMoreGuRatio + unitPaiXi;
					//cKLine.close = (int)(cKLine.close*1000)/(float)1000.0;
					
					cKLine.low = cKLine.low * unitMoreGuRatio + unitPaiXi;
					//cKLine.low = (int)(cKLine.low*1000)/(float)1000.0;
					
					cKLine.high = cKLine.high * unitMoreGuRatio + unitPaiXi;
					//cKLine.high = (int)(cKLine.high*1000)/(float)1000.0;	
				}
			}
		}
		
		return error;
	}
	
	/*
	 * 获取5分钟级别K
	 */
	public int get5MinKLineOneDay(String id, String date, List<KLine> container)
	{
		int error = 0;
		
		List<TradeDetail> ctnTradeDetail = new ArrayList<TradeDetail>();
		int errTradeDetail = m_cBaseDataStorage.getDayDetail(id, date, ctnTradeDetail);
		
		// 如果本地不存在，下载后获取
		if(0 != errTradeDetail)
		{
			m_cBaseDataDownload.downloadStockDetail(id, date);
			errTradeDetail = m_cBaseDataStorage.getDayDetail(id, date, ctnTradeDetail);
		}
				
		if(0 == errTradeDetail && ctnTradeDetail.size() != 0)
		{
			int iSec093000 = 9*3600 + 30*60 + 0;
			int iSec130000 = 13*3600 + 0*60 + 0;
            int i5Min = 5*60;
            int iSecBegin = 0;
            int iSecEnd = 0;
            int iStdSecEnd = 0;
            float preClosePrice = ctnTradeDetail.get(0).price;
            // add 上午
            for(int i = 0; i < 24; i++)
            {
            	if(0 == i)
            	{
                    iSecBegin = iSec093000 + i5Min*i - i5Min*2;
                    iSecEnd = iSec093000 + i5Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i < 23)
            	{
                    iSecBegin = iSec093000 + i5Min*i;
                    iSecEnd = iSec093000 + i5Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i == 23)
            	{
            		iSecBegin = iSec093000 + i5Min*i;
                    iSecEnd = iSec093000 + i5Min*(i+1) + i5Min*2;
                    iStdSecEnd = iSec093000 + i5Min*(i+1);
            	}
            	//System.out.println("iSecBegin:" + iSecBegin + " -- iSecEnd:" + iSecEnd );
    			List<TradeDetail> tmpList = new ArrayList<TradeDetail>();
    			for(int j = 0; j < ctnTradeDetail.size(); j++)  
    	        {  
    				TradeDetail cTradeDetail = ctnTradeDetail.get(j);  
//    	            System.out.println(cTradeDetail.time + "," 
//    	            		+ cTradeDetail.price + "," + cTradeDetail.volume);  
    	            int iSec = Integer.parseInt(cTradeDetail.time.split(":")[0])*3600
    	            		+ Integer.parseInt(cTradeDetail.time.split(":")[1])*60
    	            		+ Integer.parseInt(cTradeDetail.time.split(":")[2]);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cTradeDetail);
    	            }
    	        } 
    			// 计算5mink后添加到总表
    			//System.out.println("==================================================");
    			String StdEndTimeStr = String.format("%02d:%02d:%02d", 
    					iStdSecEnd/3600, (iStdSecEnd%3600)/60, (iStdSecEnd%3600)%60);
    			float K5MinOpen = preClosePrice;
    			float K5MinClose = preClosePrice;
    			float K5MinLow = preClosePrice;
    			float K5MinHigh = preClosePrice;
    			float K5MinVolume = preClosePrice;
    			for(int k = 0; k < tmpList.size(); k++) 
    			{
    				TradeDetail cTradeDetail = tmpList.get(k);  
    				if(0 == k) {
    					K5MinOpen = cTradeDetail.price;
    					K5MinClose = cTradeDetail.price;
    					K5MinLow = cTradeDetail.price;
    					K5MinHigh = cTradeDetail.price;
    				}
    				if(tmpList.size()-1 == k) K5MinClose = cTradeDetail.price;
    				if(cTradeDetail.price > K5MinHigh) K5MinHigh = cTradeDetail.price;
    				if(cTradeDetail.price < K5MinLow) K5MinLow = cTradeDetail.price;
    				K5MinVolume = K5MinVolume + cTradeDetail.volume;
    				//System.out.println(cTradeDetail.time);
    			}
    			KLine cKLine = new KLine();
    			cKLine.time = StdEndTimeStr;
    			cKLine.open = K5MinOpen;
    			cKLine.close = K5MinClose;
    			cKLine.low = K5MinLow;
    			cKLine.high = K5MinHigh;
    			cKLine.volume = K5MinVolume;
    			tmpList.clear();
    			container.add(cKLine);
    			//System.out.println("cKLine.datetime:" + cKLine.datetime);
    			preClosePrice = cKLine.close;
            }
            // add 下午
            for(int i = 0; i < 24; i++)
            {
            	if(0 == i)
            	{
                    iSecBegin = iSec130000 + i5Min*i - i5Min*2;
                    iSecEnd = iSec130000 + i5Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i < 23)
            	{
                    iSecBegin = iSec130000 + i5Min*i;
                    iSecEnd = iSec130000 + i5Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i == 23)
            	{
            		iSecBegin = iSec130000 + i5Min*i;
                    iSecEnd = iSec130000 + i5Min*(i+1) + i5Min*2;
                    iStdSecEnd = iSec130000 + i5Min*(i+1);
            	}
            	//System.out.println("iSecBegin:" + iSecBegin + " -- iSecEnd:" + iSecEnd );
    			List<TradeDetail> tmpList = new ArrayList<TradeDetail>();
    			for(int j = 0; j < ctnTradeDetail.size(); j++)  
    	        {  
    				TradeDetail cTradeDetail = ctnTradeDetail.get(j);  
//    	            System.out.println(cTradeDetail.time + "," 
//    	            		+ cTradeDetail.price + "," + cTradeDetail.volume);  
    	            int iSec = Integer.parseInt(cTradeDetail.time.split(":")[0])*3600
    	            		+ Integer.parseInt(cTradeDetail.time.split(":")[1])*60
    	            		+ Integer.parseInt(cTradeDetail.time.split(":")[2]);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cTradeDetail);
    	            }
    	        } 
    			// 计算5mink后添加到总表
    			//System.out.println("==================================================");
    			String StdEndTimeStr = String.format("%02d:%02d:%02d", 
    					iStdSecEnd/3600, (iStdSecEnd%3600)/60, (iStdSecEnd%3600)%60);
    			float K5MinOpen = preClosePrice;
    			float K5MinClose = preClosePrice;
    			float K5MinLow = preClosePrice;
    			float K5MinHigh = preClosePrice;
    			float K5MinVolume = preClosePrice;
    			for(int k = 0; k < tmpList.size(); k++) 
    			{
    				TradeDetail cTradeDetail = tmpList.get(k);  
    				if(0 == k) {
    					K5MinOpen = cTradeDetail.price;
    					K5MinClose = cTradeDetail.price;
    					K5MinLow = cTradeDetail.price;
    					K5MinHigh = cTradeDetail.price;
    				}
    				if(tmpList.size()-1 == k) K5MinClose = cTradeDetail.price;
    				if(cTradeDetail.price > K5MinHigh) K5MinHigh = cTradeDetail.price;
    				if(cTradeDetail.price < K5MinLow) K5MinLow = cTradeDetail.price;
    				K5MinVolume = K5MinVolume + cTradeDetail.volume;
    				//System.out.println(cTradeDetail.time);
    			}
    			KLine cKLine = new KLine();
    			cKLine.time = StdEndTimeStr;
    			cKLine.open = K5MinOpen;
    			cKLine.close = K5MinClose;
    			cKLine.low = K5MinLow;
    			cKLine.high = K5MinHigh;
    			cKLine.volume = K5MinVolume;
    			tmpList.clear();
    			container.add(cKLine);
    			//System.out.println("cKLine.datetime:" + cKLine.datetime);
    			preClosePrice = cKLine.close;
            }
		}
		else
		{
			System.out.println("[ERROR] get5MinKLineOneDay: " + id + " # " + date);
			error = -10;
			return error;
		}
		return error;
	}
	
	/*
	 * 获取1分钟级别K
	 */
	public int get1MinKLineOneDay(String id, String date, List<KLine> container)
	{
		int error = 0;
		
		List<TradeDetail> ctnTradeDetail = new ArrayList<TradeDetail>();
		int errTradeDetail = m_cBaseDataStorage.getDayDetail(id, date, ctnTradeDetail);
		
		// 如果本地不存在，下载后获取
		if(0 != errTradeDetail)
		{
			m_cBaseDataDownload.downloadStockDetail(id, date);
			errTradeDetail = m_cBaseDataStorage.getDayDetail(id, date, ctnTradeDetail);
		}
		
		if(0 == errTradeDetail && ctnTradeDetail.size() != 0)
		{
			int iSec092500 = 9*3600 + 25*60 + 0;
			int iSec093000 = 9*3600 + 30*60 + 0;
			int iSec130000 = 13*3600 + 0*60 + 0;
            int i1Min = 1*60;
            int iSecBegin = 0;
            int iSecEnd = 0;
            int iStdSecEnd = 0;
            float preClosePrice = ctnTradeDetail.get(0).price;
            // add 上午
            for(int i = 0; i < 120; i++)
            {
            	if(0 == i)
            	{
                    iSecBegin = iSec092500 + i1Min*i - i1Min*2;
                    iSecEnd = iSec093000 + i1Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i < 119)
            	{
                    iSecBegin = iSec093000 + i1Min*i;
                    iSecEnd = iSec093000 + i1Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i == 119)
            	{
            		iSecBegin = iSec093000 + i1Min*i;
                    iSecEnd = iSec093000 + i1Min*(i+1) + i1Min*2;
                    iStdSecEnd = iSec093000 + i1Min*(i+1);
            	}
            	//System.out.println("iSecBegin:" + iSecBegin + " -- iSecEnd:" + iSecEnd );
    			List<TradeDetail> tmpList = new ArrayList<TradeDetail>();
    			for(int j = 0; j < ctnTradeDetail.size(); j++)  
    	        {  
    				TradeDetail cTradeDetail = ctnTradeDetail.get(j);  
//    	            System.out.println(cTradeDetail.time + "," 
//    	            		+ cTradeDetail.price + "," + cTradeDetail.volume);  
    	            int iSec = CUtilsDateTime.GetSecond(cTradeDetail.time);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cTradeDetail);
    	            }
    	        } 
    			// 计算5mink后添加到总表
    			//System.out.println("==================================================");
    			String StdEndTimeStr = String.format("%02d:%02d:%02d", 
    					iStdSecEnd/3600, (iStdSecEnd%3600)/60, (iStdSecEnd%3600)%60);
    			
    			float K1MinOpen = preClosePrice;
    			float K1MinClose = preClosePrice;
    			float K1MinLow = preClosePrice;
    			float K1MinHigh = preClosePrice;
    			float K1MinVolume = 0.0f;
    			for(int k = 0; k < tmpList.size(); k++) 
    			{
    				TradeDetail cTradeDetail = tmpList.get(k);  
    				if(0 == k) {
    					K1MinOpen = cTradeDetail.price;
    					K1MinClose = cTradeDetail.price;
    					K1MinLow = cTradeDetail.price;
    					K1MinHigh = cTradeDetail.price;
    				}
    				if(tmpList.size()-1 == k) K1MinClose = cTradeDetail.price;
    				if(cTradeDetail.price > K1MinHigh) K1MinHigh = cTradeDetail.price;
    				if(cTradeDetail.price < K1MinLow) K1MinLow = cTradeDetail.price;
    				K1MinVolume = K1MinVolume + cTradeDetail.volume;
    				//System.out.println(cTradeDetail.time);
    			}
    			KLine cKLine = new KLine();
    			cKLine.time = StdEndTimeStr;
    			cKLine.open = K1MinOpen;
    			cKLine.close = K1MinClose;
    			cKLine.low = K1MinLow;
    			cKLine.high = K1MinHigh;
    			cKLine.volume = K1MinVolume;
    			tmpList.clear();
    			container.add(cKLine);
    			//System.out.println("cExKLine.datetime:" + cExKLine.datetime);
    			preClosePrice = cKLine.close;
            }
            // add 下午
            for(int i = 0; i < 120; i++)
            {
            	if(0 == i)
            	{
                    iSecBegin = iSec130000 + i1Min*i - i1Min*2;
                    iSecEnd = iSec130000 + i1Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i < 119)
            	{
                    iSecBegin = iSec130000 + i1Min*i;
                    iSecEnd = iSec130000 + i1Min*(i+1);
                    iStdSecEnd = iSecEnd;
            	}
            	else if(i == 119)
            	{
            		iSecBegin = iSec130000 + i1Min*i;
                    iSecEnd = iSec130000 + i1Min*(i+1) + i1Min*2;
                    iStdSecEnd = iSec130000 + i1Min*(i+1);
            	}
            	//System.out.println("iSecBegin:" + iSecBegin + " -- iSecEnd:" + iSecEnd );
    			List<TradeDetail> tmpList = new ArrayList<TradeDetail>();
    			for(int j = 0; j < ctnTradeDetail.size(); j++)  
    	        {  
    				TradeDetail cTradeDetail = ctnTradeDetail.get(j);  
//    	            System.out.println(cTradeDetail.time + "," 
//    	            		+ cTradeDetail.price + "," + cTradeDetail.volume);  
    	            int iSec = CUtilsDateTime.GetSecond(cTradeDetail.time);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cTradeDetail);
    	            }
    	        } 
    			// 计算5mink后添加到总表
    			//System.out.println("==================================================");
    			String StdEndTimeStr = String.format("%02d:%02d:%02d", 
    					iStdSecEnd/3600, (iStdSecEnd%3600)/60, (iStdSecEnd%3600)%60);
    			float K1MinOpen = preClosePrice;
    			float K1MinClose = preClosePrice;
    			float K1MinLow = preClosePrice;
    			float K1MinHigh = preClosePrice;
    			float K1MinVolume = 0.0f;
    			for(int k = 0; k < tmpList.size(); k++) 
    			{
    				TradeDetail cTradeDetail = tmpList.get(k);  
    				if(0 == k) {
    					K1MinOpen = cTradeDetail.price;
    					K1MinClose = cTradeDetail.price;
    					K1MinLow = cTradeDetail.price;
    					K1MinHigh = cTradeDetail.price;
    				}
    				if(tmpList.size()-1 == k) K1MinClose = cTradeDetail.price;
    				if(cTradeDetail.price > K1MinHigh) K1MinHigh = cTradeDetail.price;
    				if(cTradeDetail.price < K1MinLow) K1MinLow = cTradeDetail.price;
    				K1MinVolume = K1MinVolume + cTradeDetail.volume;
    				//System.out.println(cTradeDetail.time);
    			}
    			KLine cKLine = new KLine();
    			cKLine.time = StdEndTimeStr;
    			cKLine.open = K1MinOpen;
    			cKLine.close = K1MinClose;
    			cKLine.low = K1MinLow;
    			cKLine.high = K1MinHigh;
    			cKLine.volume = K1MinVolume;
    			tmpList.clear();
    			container.add(cKLine);
    			//System.out.println("cExKLine.datetime:" + cExKLine.datetime);
    			preClosePrice = cKLine.close;
            }
		}
		else
		{
			System.out.println("[ERROR] get1MinKLineOneDay: " + id + " # " + date);
			error = -10;
			return error;
		}
		return error;
	}
	
	public int getRealTimeInfo(String stockID, RealTimeInfo container)
	{
		return DataWebStockRealTimeInfo.getRealTimeInfo(stockID, container);
	}
	
	
	public List<StockItem> getRandomStockItem(int count)
	{
		List<StockItem> retList = new ArrayList<StockItem>();
		if(0 != count)
		{
			List<StockItem> retListAll = new ArrayList<StockItem>();
			
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
						StockItem cStockItem = new StockItem();
						cStockItem.id = dirName;
						retListAll.add(cStockItem);
					}
					
				}
			}
			
			if(retListAll.size()!=0)
			{
				for(int i = 0; i < count; i++)  
		        {  
					StockItem cStockItem = popRandomStock(retListAll);
					retList.add(cStockItem);
		        } 
			}
		}
		return retList;
	}
	private StockItem popRandomStock(List<StockItem> in_list)
	{
		if(in_list.size() == 0) return null;
		
		int randomInt = Math.abs(random.nextInt());
		int randomIndex = randomInt % in_list.size();
		StockItem cStockItem = new  StockItem(in_list.get(randomIndex));
		in_list.remove(randomIndex);
		return cStockItem;
	}
	
	/*
	 * 校验股票数据,检查股票数据错误
	 * 成功返回0
	 */
	public int checkStocKLine(String stockID)
	{
		// 检查基本信息
		StockInfo ctnStockInfo = new StockInfo();
		int errStockInfo = m_cBaseDataStorage.getStockInfo(stockID, ctnStockInfo);
		if(0 != errStockInfo 
				|| ctnStockInfo.name.length() <= 0)
		{
			return -1;
		}
		
		// 检查前复权日K
		List<KLine> ctnKLine = new ArrayList<KLine>();
		int errKLine = this.getDayKLinesForwardAdjusted(stockID, ctnKLine);
		if(0 != errKLine 
				|| ctnKLine.size() <= 0)
		{
			return -2;
		}
		
		// 检查前复权日K涨跌幅度, 近若干天没有问题就算没有问题
		int iBeginCheck = ctnKLine.size() - 500;
		if(iBeginCheck<=0) iBeginCheck = 0;
		for(int i=iBeginCheck; i < ctnKLine.size()-1; i++)  
        {  
			KLine cKLine = ctnKLine.get(i);  
			KLine cKLineNext = ctnKLine.get(i+1);  
            float close = cKLine.close;
            float nextHigh = cKLineNext.high;
            float nextLow = cKLineNext.low;
            float nextClose = cKLineNext.close;
            float fHighper = Math.abs((nextHigh-close)/close);
            float fLowper = Math.abs((nextLow-close)/close);
            float fCloseper = Math.abs((nextClose-close)/close);
            if(fCloseper > 0.12) // 收盘涨跌幅度异常
        	{
            	// 数据有中间丢失天的情况，排除这种错误
            	// 获取当前有效日期，下一个交易日（非周六周日）
            	String CurrentDate = cKLine.date;
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
        		
        		if(cKLineNext.date.compareTo(curValiddateStr) > 0)
        		{
        			// 此种情况允许错误，中间缺失了几天数据
//        			System.out.println("Warnning: Check getKLineQianFuQuan NG(miss data)! id:" + stockID
//                			+ " date:" + cKLine.date);
        		}
        		else
        		{
        			// 中间未缺失数据，但出现了偏差过大啊，属于错误
                	System.out.println("Warnning: Check getKLineQianFuQuan error! id:" + stockID
                			+ " date:" + cKLine.date);
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
	
	private BaseDataDownload m_cBaseDataDownload;
	private BaseDataStorage m_cBaseDataStorage;
	
	public static Random random = new Random();
	public static Formatter fmt = new Formatter(System.out);
}
