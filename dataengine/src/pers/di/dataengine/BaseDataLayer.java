package pers.di.dataengine;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import pers.di.dataengine.BaseDataDownload.ResultUpdateStock;
import pers.di.dataengine.BaseDataStorage.ResultAllStockFullDataTimestamps;
import pers.di.dataengine.BaseDataStorage.ResultStockBaseData;
import pers.di.dataengine.webdata.CommonDef.*;
import pers.di.dataengine.webdata.DataWebStockAllList.ResultAllStockList;
import pers.di.dataengine.webdata.DataWebStockDayDetail.ResultDayDetail;
import pers.di.dataengine.webdata.DataWebStockDayK.ResultKData;
import pers.di.dataengine.webdata.DataWebStockDividendPayout.ResultDividendPayout;

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
	public int updateLocalAllStockData(String dateStr)
	{
		return m_cBaseDataDownload.downloadAllStockFullData(dateStr);
	}
	// 获取本地总体数据更新时间
	public ResultAllStockFullDataTimestamps getAllStockFullDataTimestamps()
	{
		return m_cBaseDataStorage.getAllStockFullDataTimestamps();
	}
	
	/*
	 * 更新某一只股票
	 */
	public ResultUpdateStock updateLocaStockData(String stockId)
	{
		return m_cBaseDataDownload.downloadStockFullData(stockId);
	}
	
	
	/*
	 * 获取本地股票列表
	 */
	public ResultAllStockList getLocalAllStock()
	{
		return m_cBaseDataStorage.getLocalAllStock();
	}
	
	/*
	 * 获取某只股票基础信息
	 */
	public ResultStockBaseData getBaseInfo(String id) 
	{
		return m_cBaseDataStorage.getBaseInfo(id);
	}
	
	/*
	 * 获取某只股票日K
	 */
	public ResultKData getDayKData(String id)
	{
		return m_cBaseDataStorage.getKData(id);
	}
	
	/*
	 * 获取前复权日k
	 */
	public ResultKData getDayKDataForwardAdjusted(String id)
	{
		ResultKData cResultKData = m_cBaseDataStorage.getKData(id);
		ResultDividendPayout cResultDividendPayout = m_cBaseDataStorage.getDividendPayout(id);
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
	 * 获取后复权日k
	 */
	public ResultKData getDayKDataBackwardAdjusted(String id)
	{
		ResultKData cResultKData = m_cBaseDataStorage.getKData(id);
		ResultDividendPayout cResultDividendPayout = m_cBaseDataStorage.getDividendPayout(id);
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
	
	
	public static class ResultMinKDataOneDay
	{
		public ResultMinKDataOneDay()
		{
			error = 0;
			KDataList = new ArrayList<KData>();
		}
		public int error;
		public List<KData> KDataList;
	}
	/*
	 * 获取5分钟级别K
	 */
	public ResultMinKDataOneDay get5MinKDataOneDay(String id, String date)
	{
		ResultMinKDataOneDay cResultMinKDataOneDay = new ResultMinKDataOneDay();
		
		ResultDayDetail cResultDayDetail = m_cBaseDataStorage.getDayDetail(id, date);
		
		// 如果本地不存在，下载后获取
		if(0 != cResultDayDetail.error)
		{
			m_cBaseDataDownload.downloadStockDetail(id, date);
			cResultDayDetail = m_cBaseDataStorage.getDayDetail(id, date);
		}
				
		if(0 == cResultDayDetail.error && cResultDayDetail.resultList.size() != 0)
		{
			int iSec093000 = 9*3600 + 30*60 + 0;
			int iSec130000 = 13*3600 + 0*60 + 0;
            int i5Min = 5*60;
            int iSecBegin = 0;
            int iSecEnd = 0;
            int iStdSecEnd = 0;
            float preClosePrice = cResultDayDetail.resultList.get(0).price;
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
    			List<TData> tmpList = new ArrayList<TData>();
    			for(int j = 0; j < cResultDayDetail.resultList.size(); j++)  
    	        {  
    				TData cTData = cResultDayDetail.resultList.get(j);  
//    	            System.out.println(cTData.time + "," 
//    	            		+ cTData.price + "," + cTData.volume);  
    	            int iSec = Integer.parseInt(cTData.time.split(":")[0])*3600
    	            		+ Integer.parseInt(cTData.time.split(":")[1])*60
    	            		+ Integer.parseInt(cTData.time.split(":")[2]);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cTData);
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
    				TData cTData = tmpList.get(k);  
    				if(0 == k) {
    					K5MinOpen = cTData.price;
    					K5MinClose = cTData.price;
    					K5MinLow = cTData.price;
    					K5MinHigh = cTData.price;
    				}
    				if(tmpList.size()-1 == k) K5MinClose = cTData.price;
    				if(cTData.price > K5MinHigh) K5MinHigh = cTData.price;
    				if(cTData.price < K5MinLow) K5MinLow = cTData.price;
    				K5MinVolume = K5MinVolume + cTData.volume;
    				//System.out.println(cTData.time);
    			}
    			KData cKData = new KData();
    			cKData.time = StdEndTimeStr;
    			cKData.open = K5MinOpen;
    			cKData.close = K5MinClose;
    			cKData.low = K5MinLow;
    			cKData.high = K5MinHigh;
    			cKData.volume = K5MinVolume;
    			tmpList.clear();
    			cResultMinKDataOneDay.KDataList.add(cKData);
    			//System.out.println("cKData.datetime:" + cKData.datetime);
    			preClosePrice = cKData.close;
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
    			List<TData> tmpList = new ArrayList<TData>();
    			for(int j = 0; j < cResultDayDetail.resultList.size(); j++)  
    	        {  
    				TData cTData = cResultDayDetail.resultList.get(j);  
//    	            System.out.println(cTData.time + "," 
//    	            		+ cTData.price + "," + cTData.volume);  
    	            int iSec = Integer.parseInt(cTData.time.split(":")[0])*3600
    	            		+ Integer.parseInt(cTData.time.split(":")[1])*60
    	            		+ Integer.parseInt(cTData.time.split(":")[2]);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cTData);
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
    				TData cTData = tmpList.get(k);  
    				if(0 == k) {
    					K5MinOpen = cTData.price;
    					K5MinClose = cTData.price;
    					K5MinLow = cTData.price;
    					K5MinHigh = cTData.price;
    				}
    				if(tmpList.size()-1 == k) K5MinClose = cTData.price;
    				if(cTData.price > K5MinHigh) K5MinHigh = cTData.price;
    				if(cTData.price < K5MinLow) K5MinLow = cTData.price;
    				K5MinVolume = K5MinVolume + cTData.volume;
    				//System.out.println(cTData.time);
    			}
    			KData cKData = new KData();
    			cKData.time = StdEndTimeStr;
    			cKData.open = K5MinOpen;
    			cKData.close = K5MinClose;
    			cKData.low = K5MinLow;
    			cKData.high = K5MinHigh;
    			cKData.volume = K5MinVolume;
    			tmpList.clear();
    			cResultMinKDataOneDay.KDataList.add(cKData);
    			//System.out.println("cKData.datetime:" + cKData.datetime);
    			preClosePrice = cKData.close;
            }
		}
		else
		{
			System.out.println("[ERROR] get5MinKDataOneDay: " + id + " # " + date);
			cResultMinKDataOneDay.error = -10;
			return cResultMinKDataOneDay;
		}
		return cResultMinKDataOneDay;
	}
	
	/*
	 * 获取1分钟级别K
	 */
	public ResultMinKDataOneDay get1MinKDataOneDay(String id, String date)
	{
		ResultMinKDataOneDay cResultMinKDataOneDay = new ResultMinKDataOneDay();
		
		ResultDayDetail cResultDayDetail = m_cBaseDataStorage.getDayDetail(id, date);
		
		// 如果本地不存在，下载后获取
		if(0 != cResultDayDetail.error)
		{
			m_cBaseDataDownload.downloadStockDetail(id, date);
			cResultDayDetail = m_cBaseDataStorage.getDayDetail(id, date);
		}
		
		if(0 == cResultDayDetail.error && cResultDayDetail.resultList.size() != 0)
		{
			int iSec092500 = 9*3600 + 25*60 + 0;
			int iSec093000 = 9*3600 + 30*60 + 0;
			int iSec130000 = 13*3600 + 0*60 + 0;
            int i1Min = 1*60;
            int iSecBegin = 0;
            int iSecEnd = 0;
            int iStdSecEnd = 0;
            float preClosePrice = cResultDayDetail.resultList.get(0).price;
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
    			List<TData> tmpList = new ArrayList<TData>();
    			for(int j = 0; j < cResultDayDetail.resultList.size(); j++)  
    	        {  
    				TData cTData = cResultDayDetail.resultList.get(j);  
//    	            System.out.println(cTData.time + "," 
//    	            		+ cTData.price + "," + cTData.volume);  
    	            int iSec = Integer.parseInt(cTData.time.split(":")[0])*3600
    	            		+ Integer.parseInt(cTData.time.split(":")[1])*60
    	            		+ Integer.parseInt(cTData.time.split(":")[2]);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cTData);
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
    				TData cTData = tmpList.get(k);  
    				if(0 == k) {
    					K1MinOpen = cTData.price;
    					K1MinClose = cTData.price;
    					K1MinLow = cTData.price;
    					K1MinHigh = cTData.price;
    				}
    				if(tmpList.size()-1 == k) K1MinClose = cTData.price;
    				if(cTData.price > K1MinHigh) K1MinHigh = cTData.price;
    				if(cTData.price < K1MinLow) K1MinLow = cTData.price;
    				K1MinVolume = K1MinVolume + cTData.volume;
    				//System.out.println(cTData.time);
    			}
    			KData cKData = new KData();
    			cKData.time = StdEndTimeStr;
    			cKData.open = K1MinOpen;
    			cKData.close = K1MinClose;
    			cKData.low = K1MinLow;
    			cKData.high = K1MinHigh;
    			cKData.volume = K1MinVolume;
    			tmpList.clear();
    			cResultMinKDataOneDay.KDataList.add(cKData);
    			//System.out.println("cExKData.datetime:" + cExKData.datetime);
    			preClosePrice = cKData.close;
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
    			List<TData> tmpList = new ArrayList<TData>();
    			for(int j = 0; j < cResultDayDetail.resultList.size(); j++)  
    	        {  
    				TData cTData = cResultDayDetail.resultList.get(j);  
//    	            System.out.println(cTData.time + "," 
//    	            		+ cTData.price + "," + cTData.volume);  
    	            int iSec = Integer.parseInt(cTData.time.split(":")[0])*3600
    	            		+ Integer.parseInt(cTData.time.split(":")[1])*60
    	            		+ Integer.parseInt(cTData.time.split(":")[2]);
    	            if(iSec >= iSecBegin && iSec < iSecEnd)
    	            {
    	            	tmpList.add(cTData);
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
    				TData cTData = tmpList.get(k);  
    				if(0 == k) {
    					K1MinOpen = cTData.price;
    					K1MinClose = cTData.price;
    					K1MinLow = cTData.price;
    					K1MinHigh = cTData.price;
    				}
    				if(tmpList.size()-1 == k) K1MinClose = cTData.price;
    				if(cTData.price > K1MinHigh) K1MinHigh = cTData.price;
    				if(cTData.price < K1MinLow) K1MinLow = cTData.price;
    				K1MinVolume = K1MinVolume + cTData.volume;
    				//System.out.println(cTData.time);
    			}
    			KData cKData = new KData();
    			cKData.time = StdEndTimeStr;
    			cKData.open = K1MinOpen;
    			cKData.close = K1MinClose;
    			cKData.low = K1MinLow;
    			cKData.high = K1MinHigh;
    			cKData.volume = K1MinVolume;
    			tmpList.clear();
    			cResultMinKDataOneDay.KDataList.add(cKData);
    			//System.out.println("cExKData.datetime:" + cExKData.datetime);
    			preClosePrice = cKData.close;
            }
		}
		else
		{
			System.out.println("[ERROR] get1MinKDataOneDay: " + id + " # " + date);
			cResultMinKDataOneDay.error = -10;
			return cResultMinKDataOneDay;
		}
		return cResultMinKDataOneDay;
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
	public int checkStockData(String stockID)
	{
		// 检查基本信息
		ResultStockBaseData cResultStockBaseData = m_cBaseDataStorage.getBaseInfo(stockID);
		if(0 != cResultStockBaseData.error 
				|| cResultStockBaseData.stockBaseInfo.name.length() <= 0)
		{
			return -1;
		}
		
		// 检查前复权日K
		ResultKData cResultKData = this.getDayKDataForwardAdjusted(stockID);
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
	
	private BaseDataDownload m_cBaseDataDownload;
	private BaseDataStorage m_cBaseDataStorage;
	
	public static Random random = new Random();
	public static Formatter fmt = new Formatter(System.out);
}
