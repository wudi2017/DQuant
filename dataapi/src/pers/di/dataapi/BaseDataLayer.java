package pers.di.dataapi;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import pers.di.common.*;
import pers.di.dataapi.webapi.*;
import pers.di.dataapi.common.*;

/*
 * 鑲＄エ鍩虹鏁版嵁灞�
 */
public class BaseDataLayer { 
	public BaseDataLayer (String workDir) 
	{
		m_cBaseDataStorage = new BaseDataStorage(workDir);
		m_cBaseDataDownload = new BaseDataDownload(m_cBaseDataStorage);
		m_cDataWebStockRealTimeInfo = new DataWebStockRealTimeInfo();
	}
	
	public boolean resetDataRoot(String dateRoot)
	{
		return m_cBaseDataStorage.resetDataRoot(dateRoot);
	}
	
	public String dataRoot()
	{
		return m_cBaseDataStorage.dataRoot();
	}
	
	/*
	 * 鏇存柊鎵�鏈夎偂绁ㄦ暟鎹埌鎸囧畾鏃ユ湡
	 * 鍙傛暟
	 *     dateStr锛� 鎸囧畾鏃ユ湡 
	 */
	public int updateLocalAllStocKLine(String dateStr)
	{
		return m_cBaseDataDownload.downloadAllStockFullData(dateStr);
	}
	// 鑾峰彇鏈湴鎬讳綋鏁版嵁鏇存柊鏃堕棿
	public int getAllStockFullDataTimestamps(CObjectContainer<String> container)
	{
		return m_cBaseDataStorage.getAllStockFullDataTimestamps(container);
	}
	
	/*
	 * 鏇存柊鏌愪竴鍙偂绁�
	 */
	public int updateLocaStocKLine(String stockId, CObjectContainer<Integer> container)
	{
		return m_cBaseDataDownload.downloadStockFullData(stockId, container);
	}
	
	
	/*
	 * 鑾峰彇鏈湴鑲＄エ鍒楄〃
	 */
	public int getLocalAllStock(ArrayList<StockItem> container)
	{
		return m_cBaseDataStorage.getLocalAllStock(container);
	}
	
	/*
	 * 鑾峰彇鏌愬彧鑲＄エ鍩虹淇℃伅
	 */
	public int getStockInfo(String id, StockInfo container) 
	{
		return m_cBaseDataStorage.getStockInfo(id, container);
	}
	
	/*
	 * 鑾峰彇鏌愬彧鑲＄エ鏃
	 */
	public int getDayKLines(String id, List<KLine> container)
	{
		return m_cBaseDataStorage.getKLine(id, container);
	}
	
	/*
	 * 鑾峰彇鍓嶅鏉冩棩k
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
			
			double unitMoreGuRatio = (cDividendPayout.songGu + cDividendPayout.zhuanGu + 10)/10;
			double unitPaiXi = cDividendPayout.paiXi/10;
			
			for(int j = container.size() -1; j >= 0 ; j--)
			{
				KLine cKLine = container.get(j); 
				
				if(cKLine.date.compareTo(cDividendPayout.date) < 0) // 鑲＄エ鏃ユ湡灏忎簬鍒嗙孩娲炬伅鏃ユ湡鏃讹紝杩涜閲嶆柊璁＄畻
				{
					cKLine.open = (cKLine.open - unitPaiXi)/unitMoreGuRatio;
					//cKLine.open = (int)(cKLine.open*1000)/(double)1000.0;
					
//					System.out.println("date " + cKLine.date + " " + cKLine.open );
					
					cKLine.close = (cKLine.close - unitPaiXi)/unitMoreGuRatio;
					//cKLine.close = (int)(cKLine.close*1000)/(double)1000.0;
					
					cKLine.low = (cKLine.low - unitPaiXi)/unitMoreGuRatio;
					//cKLine.low = (int)(cKLine.low*1000)/(double)1000.0;
					
					cKLine.high = (cKLine.high - unitPaiXi)/unitMoreGuRatio;
					//cKLine.high = (int)(cKLine.high*1000)/(double)1000.0;	
				}
			}
		}
		return error;
	}
	
	/*
	 * 鑾峰彇鍚庡鏉冩棩k
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
			
			double unitMoreGuRatio = (cDividendPayout.songGu + cDividendPayout.zhuanGu + 10)/10;
			double unitPaiXi = cDividendPayout.paiXi/10;
			
			for(int j = 0; j< container.size(); j++)
			{
				KLine cKLine = container.get(j); 
				
				if(cKLine.date.compareTo(cDividendPayout.date) >= 0) // 鑲＄エ鏃ユ湡 澶т簬绛変簬鍒嗙孩娲炬伅鏃ユ湡鏃讹紝杩涜閲嶆柊璁＄畻
				{
					cKLine.open = cKLine.open * unitMoreGuRatio + unitPaiXi;
					//cKLine.open = (int)(cKLine.open*1000)/(double)1000.0;
					
//					System.out.println("date " + cKLine.date + " " + cKLine.open );
					
					cKLine.close = cKLine.close * unitMoreGuRatio + unitPaiXi;
					//cKLine.close = (int)(cKLine.close*1000)/(double)1000.0;
					
					cKLine.low = cKLine.low * unitMoreGuRatio + unitPaiXi;
					//cKLine.low = (int)(cKLine.low*1000)/(double)1000.0;
					
					cKLine.high = cKLine.high * unitMoreGuRatio + unitPaiXi;
					//cKLine.high = (int)(cKLine.high*1000)/(double)1000.0;	
				}
			}
		}
		
		return error;
	}
	
	/*
	 * 鑾峰彇5鍒嗛挓绾у埆K
	 */
	public int get5MinKLineOneDay(String id, String date, List<KLine> container)
	{
		int error = 0;
		
		List<TransactionRecord> ctnTransactionRecord = new ArrayList<TransactionRecord>();
		int errTransactionRecord = m_cBaseDataStorage.getDayDetail(id, date, ctnTransactionRecord);
		
		// 濡傛灉鏈湴涓嶅瓨鍦紝涓嬭浇鍚庤幏鍙�
		if(0 != errTransactionRecord)
		{
			m_cBaseDataDownload.downloadStockDetail(id, date);
			errTransactionRecord = m_cBaseDataStorage.getDayDetail(id, date, ctnTransactionRecord);
		}
				
		if(0 == errTransactionRecord && ctnTransactionRecord.size() != 0)
		{
			int iSec093000 = 9*3600 + 30*60 + 0;
			int iSec130000 = 13*3600 + 0*60 + 0;
            int i5Min = 5*60;
            int iSecBegin = 0;
            int iSecEnd = 0;
            int iStdSecEnd = 0;
            double preClosePrice = ctnTransactionRecord.get(0).price;
            // add 涓婂崍
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
    			List<TransactionRecord> tmpList = new ArrayList<TransactionRecord>();
    			for(int j = 0; j < ctnTransactionRecord.size(); j++)  
    	        {  
    				TransactionRecord cTransactionRecord = ctnTransactionRecord.get(j);  
//    	            System.out.println(cTransactionRecord.time + "," 
//    	            		+ cTransactionRecord.price + "," + cTransactionRecord.volume);  
    	            int iSec = Integer.parseInt(cTransactionRecord.time.split(":")[0])*3600
    	            		+ Integer.parseInt(cTransactionRecord.time.split(":")[1])*60
    	            		+ Integer.parseInt(cTransactionRecord.time.split(":")[2]);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cTransactionRecord);
    	            }
    	        } 
    			// 璁＄畻5mink鍚庢坊鍔犲埌鎬昏〃
    			//System.out.println("==================================================");
    			String StdEndTimeStr = String.format("%02d:%02d:%02d", 
    					iStdSecEnd/3600, (iStdSecEnd%3600)/60, (iStdSecEnd%3600)%60);
    			double K5MinOpen = preClosePrice;
    			double K5MinClose = preClosePrice;
    			double K5MinLow = preClosePrice;
    			double K5MinHigh = preClosePrice;
    			double K5MinVolume = preClosePrice;
    			for(int k = 0; k < tmpList.size(); k++) 
    			{
    				TransactionRecord cTransactionRecord = tmpList.get(k);  
    				if(0 == k) {
    					K5MinOpen = cTransactionRecord.price;
    					K5MinClose = cTransactionRecord.price;
    					K5MinLow = cTransactionRecord.price;
    					K5MinHigh = cTransactionRecord.price;
    				}
    				if(tmpList.size()-1 == k) K5MinClose = cTransactionRecord.price;
    				if(cTransactionRecord.price > K5MinHigh) K5MinHigh = cTransactionRecord.price;
    				if(cTransactionRecord.price < K5MinLow) K5MinLow = cTransactionRecord.price;
    				K5MinVolume = K5MinVolume + cTransactionRecord.volume;
    				//System.out.println(cTransactionRecord.time);
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
            // add 涓嬪崍
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
    			List<TransactionRecord> tmpList = new ArrayList<TransactionRecord>();
    			for(int j = 0; j < ctnTransactionRecord.size(); j++)  
    	        {  
    				TransactionRecord cTransactionRecord = ctnTransactionRecord.get(j);  
//    	            System.out.println(cTransactionRecord.time + "," 
//    	            		+ cTransactionRecord.price + "," + cTransactionRecord.volume);  
    	            int iSec = Integer.parseInt(cTransactionRecord.time.split(":")[0])*3600
    	            		+ Integer.parseInt(cTransactionRecord.time.split(":")[1])*60
    	            		+ Integer.parseInt(cTransactionRecord.time.split(":")[2]);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cTransactionRecord);
    	            }
    	        } 
    			// 璁＄畻5mink鍚庢坊鍔犲埌鎬昏〃
    			//System.out.println("==================================================");
    			String StdEndTimeStr = String.format("%02d:%02d:%02d", 
    					iStdSecEnd/3600, (iStdSecEnd%3600)/60, (iStdSecEnd%3600)%60);
    			double K5MinOpen = preClosePrice;
    			double K5MinClose = preClosePrice;
    			double K5MinLow = preClosePrice;
    			double K5MinHigh = preClosePrice;
    			double K5MinVolume = preClosePrice;
    			for(int k = 0; k < tmpList.size(); k++) 
    			{
    				TransactionRecord cTransactionRecord = tmpList.get(k);  
    				if(0 == k) {
    					K5MinOpen = cTransactionRecord.price;
    					K5MinClose = cTransactionRecord.price;
    					K5MinLow = cTransactionRecord.price;
    					K5MinHigh = cTransactionRecord.price;
    				}
    				if(tmpList.size()-1 == k) K5MinClose = cTransactionRecord.price;
    				if(cTransactionRecord.price > K5MinHigh) K5MinHigh = cTransactionRecord.price;
    				if(cTransactionRecord.price < K5MinLow) K5MinLow = cTransactionRecord.price;
    				K5MinVolume = K5MinVolume + cTransactionRecord.volume;
    				//System.out.println(cTransactionRecord.time);
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
	 * 鑾峰彇1鍒嗛挓绾у埆K
	 */
	public int get1MinKLineOneDay(String id, String date, List<KLine> container)
	{
		int error = 0;
		
		List<TransactionRecord> ctnTransactionRecord = new ArrayList<TransactionRecord>();
		int errTransactionRecord = m_cBaseDataStorage.getDayDetail(id, date, ctnTransactionRecord);
		
		// 濡傛灉鏈湴涓嶅瓨鍦紝涓嬭浇鍚庤幏鍙�
		if(0 != errTransactionRecord)
		{
			m_cBaseDataDownload.downloadStockDetail(id, date);
			errTransactionRecord = m_cBaseDataStorage.getDayDetail(id, date, ctnTransactionRecord);
		}
		
		if(0 == errTransactionRecord && ctnTransactionRecord.size() != 0)
		{
			int iSec092500 = 9*3600 + 25*60 + 0;
			int iSec093000 = 9*3600 + 30*60 + 0;
			int iSec130000 = 13*3600 + 0*60 + 0;
            int i1Min = 1*60;
            int iSecBegin = 0;
            int iSecEnd = 0;
            int iStdSecEnd = 0;
            double preClosePrice = ctnTransactionRecord.get(0).price;
            // add 涓婂崍
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
    			List<TransactionRecord> tmpList = new ArrayList<TransactionRecord>();
    			for(int j = 0; j < ctnTransactionRecord.size(); j++)  
    	        {  
    				TransactionRecord cTransactionRecord = ctnTransactionRecord.get(j);  
//    	            System.out.println(cTransactionRecord.time + "," 
//    	            		+ cTransactionRecord.price + "," + cTransactionRecord.volume);  
    	            int iSec = CUtilsDateTime.GetSecondFromTimeStr(cTransactionRecord.time);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cTransactionRecord);
    	            }
    	        } 
    			// 璁＄畻5mink鍚庢坊鍔犲埌鎬昏〃
    			//System.out.println("==================================================");
    			String StdEndTimeStr = String.format("%02d:%02d:%02d", 
    					iStdSecEnd/3600, (iStdSecEnd%3600)/60, (iStdSecEnd%3600)%60);
    			
    			double K1MinOpen = preClosePrice;
    			double K1MinClose = preClosePrice;
    			double K1MinLow = preClosePrice;
    			double K1MinHigh = preClosePrice;
    			double K1MinVolume = 0.0f;
    			for(int k = 0; k < tmpList.size(); k++) 
    			{
    				TransactionRecord cTransactionRecord = tmpList.get(k);  
    				if(0 == k) {
    					K1MinOpen = cTransactionRecord.price;
    					K1MinClose = cTransactionRecord.price;
    					K1MinLow = cTransactionRecord.price;
    					K1MinHigh = cTransactionRecord.price;
    				}
    				if(tmpList.size()-1 == k) K1MinClose = cTransactionRecord.price;
    				if(cTransactionRecord.price > K1MinHigh) K1MinHigh = cTransactionRecord.price;
    				if(cTransactionRecord.price < K1MinLow) K1MinLow = cTransactionRecord.price;
    				K1MinVolume = K1MinVolume + cTransactionRecord.volume;
    				//System.out.println(cTransactionRecord.time);
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
            // add 涓嬪崍
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
    			List<TransactionRecord> tmpList = new ArrayList<TransactionRecord>();
    			for(int j = 0; j < ctnTransactionRecord.size(); j++)  
    	        {  
    				TransactionRecord cTransactionRecord = ctnTransactionRecord.get(j);  
//    	            System.out.println(cTransactionRecord.time + "," 
//    	            		+ cTransactionRecord.price + "," + cTransactionRecord.volume);  
    	            int iSec = CUtilsDateTime.GetSecondFromTimeStr(cTransactionRecord.time);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cTransactionRecord);
    	            }
    	        } 
    			// 璁＄畻5mink鍚庢坊鍔犲埌鎬昏〃
    			//System.out.println("==================================================");
    			String StdEndTimeStr = String.format("%02d:%02d:%02d", 
    					iStdSecEnd/3600, (iStdSecEnd%3600)/60, (iStdSecEnd%3600)%60);
    			double K1MinOpen = preClosePrice;
    			double K1MinClose = preClosePrice;
    			double K1MinLow = preClosePrice;
    			double K1MinHigh = preClosePrice;
    			double K1MinVolume = 0.0f;
    			for(int k = 0; k < tmpList.size(); k++) 
    			{
    				TransactionRecord cTransactionRecord = tmpList.get(k);  
    				if(0 == k) {
    					K1MinOpen = cTransactionRecord.price;
    					K1MinClose = cTransactionRecord.price;
    					K1MinLow = cTransactionRecord.price;
    					K1MinHigh = cTransactionRecord.price;
    				}
    				if(tmpList.size()-1 == k) K1MinClose = cTransactionRecord.price;
    				if(cTransactionRecord.price > K1MinHigh) K1MinHigh = cTransactionRecord.price;
    				if(cTransactionRecord.price < K1MinLow) K1MinLow = cTransactionRecord.price;
    				K1MinVolume = K1MinVolume + cTransactionRecord.volume;
    				//System.out.println(cTransactionRecord.time);
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
	
	public int getRealTimeInfo(List<String> stockIDs, List<RealTimeInfoLite> container)
	{
		return m_cDataWebStockRealTimeInfo.getRealTimeInfo(stockIDs, container);
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
	 * 鏍￠獙鑲＄エ鏁版嵁,妫�鏌ヨ偂绁ㄦ暟鎹敊璇�
	 * 鎴愬姛杩斿洖0
	 */
	public int checkStocKLine(String stockID)
	{
		// 妫�鏌ュ熀鏈俊鎭�
		StockInfo ctnStockInfo = new StockInfo();
		int errStockInfo = m_cBaseDataStorage.getStockInfo(stockID, ctnStockInfo);
		if(0 != errStockInfo 
				|| ctnStockInfo.name.length() <= 0)
		{
			return -1;
		}
		
		// 妫�鏌ュ墠澶嶆潈鏃
		List<KLine> ctnKLine = new ArrayList<KLine>();
		int errKLine = this.getDayKLinesForwardAdjusted(stockID, ctnKLine);
		if(0 != errKLine 
				|| ctnKLine.size() <= 0)
		{
			return -2;
		}
		
		// 妫�鏌ュ墠澶嶆潈鏃娑ㄨ穼骞呭害, 杩戣嫢骞插ぉ娌℃湁闂灏辩畻娌℃湁闂
		int iBeginCheck = ctnKLine.size() - 500;
		if(iBeginCheck<=0) iBeginCheck = 0;
		for(int i=iBeginCheck; i < ctnKLine.size()-1; i++)  
        {  
			KLine cKLine = ctnKLine.get(i);  
			KLine cKLineNext = ctnKLine.get(i+1);  
            double close = cKLine.close;
            double nextHigh = cKLineNext.high;
            double nextLow = cKLineNext.low;
            double nextClose = cKLineNext.close;
            double fHighper = Math.abs((nextHigh-close)/close);
            double fLowper = Math.abs((nextLow-close)/close);
            double fCloseper = Math.abs((nextClose-close)/close);
            if(fCloseper > 0.12) // 鏀剁洏娑ㄨ穼骞呭害寮傚父
        	{
            	// 鏁版嵁鏈変腑闂翠涪澶卞ぉ鐨勬儏鍐碉紝鎺掗櫎杩欑閿欒
            	// 鑾峰彇褰撳墠鏈夋晥鏃ユ湡锛屼笅涓�涓氦鏄撴棩锛堥潪鍛ㄥ叚鍛ㄦ棩锛�
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
        			// 姝ょ鎯呭喌鍏佽閿欒锛屼腑闂寸己澶变簡鍑犲ぉ鏁版嵁
//        			System.out.println("Warnning: Check getKLineQianFuQuan NG(miss data)! id:" + stockID
//                			+ " date:" + cKLine.date);
        		}
        		else
        		{
        			// 涓棿鏈己澶辨暟鎹紝浣嗗嚭鐜颁簡鍋忓樊杩囧ぇ鍟婏紝灞炰簬閿欒
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
	private DataWebStockRealTimeInfo m_cDataWebStockRealTimeInfo;
	
	public static Random random = new Random();
	public static Formatter fmt = new Formatter(System.out);
}
