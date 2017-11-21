package pers.di.dataapi;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import pers.di.common.*;
import pers.di.dataapi.*;
import pers.di.dataapi.common.*;
import pers.di.dataapi.webapi.*;

public class BaseDataDownload {
	
	public BaseDataDownload (BaseDataStorage cBaseDataStorage) 
	{
		m_baseDataStorage = cBaseDataStorage;
	}
	
	/*
	 * 下载所有股票数据到本地
	 * 
	 * 返回值：
	 *     返回0为成功，其他值为失败
	 * 参数：
	 *     dateStr 下载到的最后日期
	 */
	public int downloadAllStockFullData(String dateStr)
	{
		long lTCBegin = CUtilsDateTime.GetCurrentTimeMillis();
		
		CObjectContainer<String> ctnAllStockFullDataTimestamps = new CObjectContainer<String>();
		int errAllStockFullDataTimestamps = m_baseDataStorage.getAllStockFullDataTimestamps(ctnAllStockFullDataTimestamps);
		if(0 == errAllStockFullDataTimestamps)
		{
			if(ctnAllStockFullDataTimestamps.get().compareTo(dateStr) >= 0)
			{
				s_fmt.format("[%s] update success! (current is newest, local: %s)\n", 
						CUtilsDateTime.GetCurDateTimeStr(), ctnAllStockFullDataTimestamps.get());
				return 0;
			}
		}
		
		// 更新指数k
		String ShangZhiId = "999999";
		String ShangZhiName = "上阵指数";
		
		CObjectContainer<Integer> ctnCountSZ = new CObjectContainer<Integer>();
		int errDownloadSZ = this.downloadStockFullData(ShangZhiId, ctnCountSZ);
		String newestDate = "";
		if(0 == errDownloadSZ)
		{
			List<KLine> ctnKLine = new ArrayList<KLine>();
			int error = m_baseDataStorage.getKLine(ShangZhiId, ctnKLine);
			
			if(0 == error && ctnKLine.size() > 0)
			{
				newestDate = ctnKLine.get(ctnKLine.size()-1).date;
			}
			
			s_fmt.format("[%s] update success: %s (%s) item:%d date:%s\n", 
					CUtilsDateTime.GetCurDateTimeStr(), ShangZhiId, ShangZhiName, ctnCountSZ.get(), newestDate);
		}
		else
		{
			s_fmt.format("[%s] update ERROR: %s error(%d)\n", 
					CUtilsDateTime.GetCurDateTimeStr(), ShangZhiId, errDownloadSZ);
		}
		
		
		// 更新所有k
		List<StockItem> stockAllList = new ArrayList<StockItem>();
		int errAllStockList = DataWebStockAllList.getAllStockList(stockAllList);
		if(0 == errAllStockList)
		{
			int iAllStockListSize = stockAllList.size();
			for(int i = 0; i < iAllStockListSize; i++)  
	        {  
				StockItem cStockItem = stockAllList.get(i);
				
				String stockID = cStockItem.id;
				
				CObjectContainer<Integer> ctnCount = new CObjectContainer<Integer>();
				int errDownloaddStockFullData = this.downloadStockFullData(stockID, ctnCount);
	           
				double fCostTime = (CUtilsDateTime.GetCurrentTimeMillis() - lTCBegin)/1000.0f;
				
				if(0 == errDownloaddStockFullData)
				{
					List<KLine> ctnKLine = new ArrayList<KLine>();
					int errKLine = m_baseDataStorage.getKLine(stockID, ctnKLine);
		    		if(0 == errKLine && ctnKLine.size() > 0)
		    		{
		    			String stockNewestDate = ctnKLine.get(ctnKLine.size()-1).date;
		    			s_fmt.format("[%s] update success %d/%d %.3fs: %s (%s) item:%d date:%s\n", 
		    					CUtilsDateTime.GetCurDateTimeStr(), i, iAllStockListSize, fCostTime, 
		    					cStockItem.id, cStockItem.name, ctnCount.get(), stockNewestDate);
		    		}
		            else
		            {
		            	s_fmt.format("[%s] update ERROR %d/%d %.3fs: %s (%s) error(%d)\n", 
		            			CUtilsDateTime.GetCurDateTimeStr(), i, iAllStockListSize, fCostTime, 
		            			cStockItem.id, cStockItem.name, errDownloaddStockFullData);
		            }
				}
				else
				{
					s_fmt.format("[%s] update ERROR %d/%d %.3fs: %s error(%d)\n", 
							CUtilsDateTime.GetCurDateTimeStr(), i, iAllStockListSize, fCostTime, 
							cStockItem.id, errDownloaddStockFullData);
				}   
				
	        } 
			double fCostTimeAll = (CUtilsDateTime.GetCurrentTimeMillis() - lTCBegin)/1000.0f;
			s_fmt.format("[%s] update success all %.3fs, count: %d\n", 
					CUtilsDateTime.GetCurDateTimeStr(), fCostTimeAll, stockAllList.size()); 
		}
		else
		{
			System.out.println("ERROR:" + errAllStockList + "\n");
		}
		
		if(newestDate.length() == "0000-00-00".length())
		{
			m_baseDataStorage.saveAllStockFullDataTimestamps(newestDate);
		}
		else
		{
			System.out.println("ERROR:" + "updateStocksFinish failed!\n");
		}
		
		return 0;
	}
	

	/*
	 * 下载所单只股票数据到本地
	 * 
	 * 返回值：
	 *     返回0为成功，其他值为失败
	 * 参数：
	 *     container 接收容器，获得更新日K线天数
	 */
	public int downloadStockFullData(String id, CObjectContainer<Integer> container)
	{
		int error = 0;
		
		// 获取当前有效日期，交易日（非周六周日）
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		String CurrentDate = df.format(new Date());
		int curyear = Integer.parseInt(CurrentDate.split("-")[0]);
		int curmonth = Integer.parseInt(CurrentDate.split("-")[1]);
		int curday = Integer.parseInt(CurrentDate.split("-")[2]);
		Calendar xcal = Calendar.getInstance();
		xcal.set(curyear, curmonth-1, curday);
		int cw = xcal.get(Calendar.DAY_OF_WEEK);
		while(cw == 1 || cw == 7)
		{
			xcal.add(Calendar.DATE, -1);
			cw = xcal.get(Calendar.DAY_OF_WEEK);
		}
		Date curValiddate = xcal.getTime();
		String curValiddateStr = df.format(curValiddate);
		// System.out.println("CurrentValidDate:" + curValiddateStr);
		
		// 获取本地日k数据与分红派息数据
		List<KLine> ctnKLineLocal = new ArrayList<KLine>();
		int errKline = m_baseDataStorage.getKLine(id, ctnKLineLocal);
		List<DividendPayout> ctnDividendPayout = new ArrayList<DividendPayout>();
		int errDividendPayout = m_baseDataStorage.getDividendPayout(id, ctnDividendPayout);
		if(0 == errKline
			&& 0 == errDividendPayout 
			&& ctnKLineLocal.size() != 0 
			/*&& retListLocalDividend.size() != 0 */)
		// 本地有日K数据
		{
			// 获取本地数据最后日期
			KLine cKLineLast = ctnKLineLocal.get(ctnKLineLocal.size()-1);
			String localDataLastDate = cKLineLast.date; 
			//System.out.println("localDataLastDate:" + localDataLastDate);
			
			// 如果当前日期大于本地最后数据日期，需要继续检测
			if(curValiddateStr.compareTo(localDataLastDate) > 0)
			{
				// 获取当前BaseInfo信息
				StockInfo ctnStockInfo = new StockInfo();
				int errStockInfo = DataWebStockInfo.getStockInfo(id, ctnStockInfo);
				if(0 == errStockInfo)
				{
					// 保存股票基本信息
					StockInfo cStockBaseData = new StockInfo();
					cStockBaseData.name = ctnStockInfo.name;
					cStockBaseData.allMarketValue = ctnStockInfo.allMarketValue;
					cStockBaseData.circulatedMarketValue = ctnStockInfo.circulatedMarketValue;
					cStockBaseData.peRatio = ctnStockInfo.peRatio;
					m_baseDataStorage.saveStockInfo(id, cStockBaseData);
					
					// 当前时间在收盘之前，网络数据有效日期为前一天（非周六周日）
					String webValidLastDate = ctnStockInfo.date;
					if(ctnStockInfo.time.compareTo("15:00:00") < 0)
					{
						int year = Integer.parseInt(ctnStockInfo.date.split("-")[0]);
						int month = Integer.parseInt(ctnStockInfo.date.split("-")[1]);
						int day = Integer.parseInt(ctnStockInfo.date.split("-")[2]);
						int hour = Integer.parseInt(ctnStockInfo.time.split(":")[0]);
						int min = Integer.parseInt(ctnStockInfo.time.split(":")[1]);
						int sec = Integer.parseInt(ctnStockInfo.time.split(":")[2]);
						Calendar cal0 = Calendar.getInstance();
						cal0.set(year, month-1, day, hour, min, sec);
						// 获取上一个非周末的日期
						cal0.add(Calendar.DATE, -1);
						int webwk = cal0.get(Calendar.DAY_OF_WEEK);
						while(webwk == 1 || webwk == 7)
						{
							cal0.add(Calendar.DATE, -1);
							webwk = cal0.get(Calendar.DAY_OF_WEEK);
						}
						
						Date vdate = cal0.getTime();
						webValidLastDate = df.format(vdate);
					}
					// System.out.println("webValidLastDate:" + webValidLastDate);
					
					// 网络数据有效日期比本地数据新，需要追加更新
					if(webValidLastDate.compareTo(cKLineLast.date) > 0)
					{
						// 计算从网络需要获取哪一段时间数据
						int year = Integer.parseInt(localDataLastDate.split("-")[0]);
						int month = Integer.parseInt(localDataLastDate.split("-")[1]);
						int day = Integer.parseInt(localDataLastDate.split("-")[2]);
						Calendar cal1 = Calendar.getInstance();
						cal1.set(year, month-1, day);
						cal1.add(Calendar.DATE, 1);
						Date fromDate = cal1.getTime();
						String fromDateStr = df.format(fromDate).replace("-", "");
						String toDateStr = webValidLastDate.replace("-", "");
						//System.out.println("fromDateStr:" + fromDateStr);
						//System.out.println("toDateStr:" + toDateStr);
						
						// 获取网络日K数据
						List<KLine> ctnKLineWeb = new ArrayList<KLine>();
						int errKLineWeb = DataWebStockDayK.getKLine(id, fromDateStr, toDateStr, ctnKLineWeb);
						if(0 == errKLineWeb)
						// 新增日K数据获取成功
						{
							// 向本地数据列表中追加新的更新数据
							for(int i = 0; i < ctnKLineWeb.size(); i++)  
					        {  
								KLine cKLine = ctnKLineWeb.get(i);  
								ctnKLineLocal.add(cKLine);
					        } 
							// 追加后的本地列表日K数据保存至本地
							int retsetKLine = m_baseDataStorage.saveKLine(id, ctnKLineLocal);
							if(0 == retsetKLine)
							// 保存成功
							{
								// 更新复权因子数据
								if(0 == this.downloadStockDividendPayout(id))
								{
									// 追加成功
									error = 0;
									container.set(ctnKLineWeb.size());
									return error;
								}
								else
								{
									// 更新复权因子失败
									error = -80;
									return error;
								}
							}
							else
							{
								//保存本地数据失败
								error = -50;
								return error;
							}
						}
						else
						{
							// 网络获取追加数据失败
							error = -40;
							return error;
						}
						
					}
					else
					{
						// 已经和网络最新有效日线一样
						error = 0;
						container.set(0);
						return error;
					}
				}
				else
				{
					// 获取网络最新有效交易日期失败
					error = -20;
					return error;
				}
			}
			else
			{
				// 本地数据已经是最新
				error = 0;
				return error;
			}
		}
		else
		// 本地没有数据，需要试图重新下载
		{	
			StockInfo ctnStockInfo = new StockInfo();
			int errStockInfo = DataWebStockInfo.getStockInfo(id, ctnStockInfo);
			if(0 == errStockInfo)
			{
				// 下载日K，分红派息，基本信息
				int retdownloadStockDayk =  this.downloadStockDayk(id);
				int retdownloadStockDividendPayout =  this.downloadStockDividendPayout(id);
				int retdownloadBaseInfo =  this.downloadStockInfo(id);
				if(0 == retdownloadStockDayk 
						&& 0 == retdownloadStockDividendPayout 
						&& 0 == retdownloadBaseInfo)
				// 下载日K，分红派息，基本信息 成功
				{
					List<KLine> ctnKLineLocalNew = new ArrayList<KLine>();
					int errKLineLocalNew = m_baseDataStorage.getKLine(id, ctnKLineLocalNew);
					if(errKLineLocalNew == 0)
					{
						//最新数据下载成功返回天数
						error = 0;
						container.set(ctnKLineLocalNew.size());
						return error;
					}
					else
					{
						error = -23;
						return error;
					}
				}
				else
				// 下载日K，分红派息，基本信息 失败
				{
					error = -10;
					return error;
				}
			}
			else
			{
				// 获取网络最新有效交易日期失败
				error = -20;
				return error;
			}
		}
	}
	
	/*
	 * 下载日K
	 */
	public int downloadStockDayk(String id)
	{
		String curDate = CUtilsDateTime.GetCurDateStr();
		String paramToDate = curDate.replace("-", "");
		List<KLine> ctnKLine = new ArrayList<KLine>();
		int error = DataWebStockDayK.getKLine(id, "20080101", paramToDate, ctnKLine);
		if(0 == error)
		{
			try
			{
				m_baseDataStorage.saveKLine(id, ctnKLine);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println(e.getMessage()); 
				return -1;
			}
		}
		else
		{
			System.out.println("ERROR:" + error);
		}
		return 0;
	}
	
	/*
	 * 下载股票信息
	 */
	public int downloadStockInfo(String id)
	{
		try
		{
			StockInfo ctnStockInfo = new StockInfo();
			int errStockInfo = DataWebStockInfo.getStockInfo(id, ctnStockInfo);
			if(0 == errStockInfo)
			{
				m_baseDataStorage.saveStockInfo(id, ctnStockInfo);
			}
			else
			{
				return -20;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println(e.getMessage()); 
			return -1;
		}
		return 0;
	}
	
	/*
	 * 下载分红派息
	 */
	public int downloadStockDividendPayout(String id)
	{
		List<DividendPayout> ctnDividendPayout = new ArrayList<DividendPayout>();
		int errDividendPayout = DataWebStockDividendPayout.getDividendPayout(id, ctnDividendPayout);
		if(0 == errDividendPayout)
		{
			try
			{
				m_baseDataStorage.saveDividendPayout(id, ctnDividendPayout);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println(e.getMessage()); 
				return -1;
			}
		}
		else
		{
			System.out.println("ERROR:" + errDividendPayout);
			return -10;
		}
		return 0;
	}
	
	/*
	 * 下载某只股票日内交易数据到本地
	 * 返回0为成功
	 */
	public int downloadStockDetail(String id, String date) {
		s_fmt.format("@downloadStocKLineDetail stockID(%s) date(%s)\n",id,date);
		
		List<TradeDetail> ctnTradeDetail = new ArrayList<TradeDetail>();
		int error = DataWebStockDayDetail.getDayDetail(id, date, ctnTradeDetail);
		if(0 == error)
		{
			try
			{
				m_baseDataStorage.saveDayDetail(id, date, ctnTradeDetail);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println(e.getMessage()); 
				return -1;
			}
		}
		else
		{
			System.out.println("ERROR:" + error);
			return -30;
		}
		return 0;
	}
	
	private BaseDataStorage m_baseDataStorage;
	
	private Formatter s_fmt = new Formatter(System.out);
}
