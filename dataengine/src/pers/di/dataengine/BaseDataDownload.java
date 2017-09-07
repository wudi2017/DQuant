package pers.di.dataengine;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import pers.di.common.CPath;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.BaseDataDownload.ResultUpdateStock;
import pers.di.dataengine.BaseDataStorage.ResultAllStockFullDataTimestamps;
import pers.di.dataengine.webdata.DataWebStockAllList;
import pers.di.dataengine.webdata.DataWebStockBaseInfo;
import pers.di.dataengine.webdata.DataWebStockBaseInfo.ResultStockBaseInfo;
import pers.di.dataengine.webdata.DataWebStockDayDetail;
import pers.di.dataengine.webdata.DataWebStockDayK;
import pers.di.dataengine.webdata.DataWebStockDividendPayout;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo;
import pers.di.dataengine.common.*;

public class BaseDataDownload {
	
	public BaseDataDownload (BaseDataStorage cBaseDataStorage) 
	{
		m_baseDataStorage = cBaseDataStorage;
	}
	
	public int downloadAllStockFullData(String dateStr)
	{
		ResultAllStockFullDataTimestamps cResultUpdatedStocksDate = m_baseDataStorage.getAllStockFullDataTimestamps();
		if(0 == cResultUpdatedStocksDate.error)
		{
			if(cResultUpdatedStocksDate.date.compareTo(dateStr) >= 0)
			{
				s_fmt.format("update success! (current is newest, local: %s)\n", cResultUpdatedStocksDate.date);
				return 0;
			}
		}
		
		// 更新指数k
		String ShangZhiId = "999999";
		String ShangZhiName = "上阵指数";
		
		ResultUpdateStock cResultUpdateStockShangZhi = this.downloadStockFullData(ShangZhiId);
		String newestDate = "";
		if(0 == cResultUpdateStockShangZhi.error)
		{
			List<KLine> ctnKLine = new ArrayList<KLine>();
			int error = m_baseDataStorage.getKLine(ShangZhiId, ctnKLine);
			
			if(0 == error && ctnKLine.size() > 0)
			{
				newestDate = ctnKLine.get(ctnKLine.size()-1).date;
			}
			
			s_fmt.format("update success: %s (%s) item:%d date:%s\n", ShangZhiId, ShangZhiName, cResultUpdateStockShangZhi.updateCnt, newestDate);
		}
		else
		{
			s_fmt.format("update ERROR: %s error(%d)\n", ShangZhiId, cResultUpdateStockShangZhi.error);
		}
		
		
		// 更新所有k
		List<StockItem> stockAllList = new ArrayList<StockItem>();
		int errAllStockList = DataWebStockAllList.getAllStockList(stockAllList);
		if(0 == errAllStockList)
		{
			for(int i = 0; i < stockAllList.size(); i++)  
	        {  
				StockItem cStockItem = stockAllList.get(i);
				
				String stockID = cStockItem.id;
				
				ResultUpdateStock cResultUpdateStock = this.downloadStockFullData(stockID);
	           
				if(0 == cResultUpdateStock.error)
				{
					List<KLine> ctnKLine = new ArrayList<KLine>();
					int errKLine = m_baseDataStorage.getKLine(stockID, ctnKLine);
		    		if(0 == errKLine && ctnKLine.size() > 0)
		    		{
		    			String stockNewestDate = ctnKLine.get(ctnKLine.size()-1).date;
		    			s_fmt.format("update success: %s (%s) item:%d date:%s\n", cStockItem.id, cStockItem.name, cResultUpdateStock.updateCnt, stockNewestDate);
		    		}
		            else
		            {
		            	s_fmt.format("update ERROR: %s (%s) error(%d)\n", cStockItem.id, cStockItem.name, cResultUpdateStock.error);
		            }
				}
				else
				{
					s_fmt.format("update ERROR: %s error(%d)\n", cStockItem.id, cResultUpdateStock.error);
				}   
				
	        } 
			System.out.println("update finish, count:" + stockAllList.size()); 
		}
		else
		{
			System.out.println("ERROR:" + errAllStockList);
		}
		
		if(newestDate.length() == "0000-00-00".length())
		{
			m_baseDataStorage.saveAllStockFullDataTimestamps(newestDate);
		}
		else
		{
			System.out.println("ERROR:" + "updateStocksFinish failed!");
		}
		
		return 0;
	}
	

	/*
	 * downloadStocKLine
	 * include: dayK,DividendPayout,BaseInfo
	 * not include: detail
	 */
	public static class ResultUpdateStock
	{
		public ResultUpdateStock()
		{
			error = 0;
			updateCnt = 0;
		}
		public int error;
		public int updateCnt;
	}
	public ResultUpdateStock downloadStockFullData(String id)
	{
		ResultUpdateStock cResultUpdateStock = new ResultUpdateStock();
		
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
				StockBaseInfo ctnStockBaseInfo = new StockBaseInfo();
				int errStockBaseInfo = DataWebStockBaseInfo.getStockBaseInfo(id, ctnStockBaseInfo);
				if(0 == errStockBaseInfo)
				{
					// 保存股票基本信息
					StockBaseInfo cStockBaseData = new StockBaseInfo();
					cStockBaseData.name = ctnStockBaseInfo.name;
					cStockBaseData.allMarketValue = ctnStockBaseInfo.allMarketValue;
					cStockBaseData.circulatedMarketValue = ctnStockBaseInfo.circulatedMarketValue;
					cStockBaseData.peRatio = ctnStockBaseInfo.peRatio;
					m_baseDataStorage.saveBaseInfo(id, cStockBaseData);
					
					// 当前时间在收盘之前，网络数据有效日期为前一天（非周六周日）
					String webValidLastDate = ctnStockBaseInfo.date;
					if(ctnStockBaseInfo.time.compareTo("15:00:00") < 0)
					{
						int year = Integer.parseInt(ctnStockBaseInfo.date.split("-")[0]);
						int month = Integer.parseInt(ctnStockBaseInfo.date.split("-")[1]);
						int day = Integer.parseInt(ctnStockBaseInfo.date.split("-")[2]);
						int hour = Integer.parseInt(ctnStockBaseInfo.time.split(":")[0]);
						int min = Integer.parseInt(ctnStockBaseInfo.time.split(":")[1]);
						int sec = Integer.parseInt(ctnStockBaseInfo.time.split(":")[2]);
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
									cResultUpdateStock.error = 0;
									cResultUpdateStock.updateCnt = ctnKLineWeb.size();
									return cResultUpdateStock;
								}
								else
								{
									// 更新复权因子失败
									cResultUpdateStock.error = -80;
									return cResultUpdateStock;
								}
							}
							else
							{
								//保存本地数据失败
								cResultUpdateStock.error = -50;
								return cResultUpdateStock;
							}
						}
						else
						{
							// 网络获取追加数据失败
							cResultUpdateStock.error = -40;
							return cResultUpdateStock;
						}
						
					}
					else
					{
						// 已经和网络最新有效日线一样
						cResultUpdateStock.error = 0;
						return cResultUpdateStock;
					}
				}
				else
				{
					// 获取网络最新有效交易日期失败
					cResultUpdateStock.error = -20;
					return cResultUpdateStock;
				}
			}
			else
			{
				// 本地数据已经是最新
				cResultUpdateStock.error = 0;
				return cResultUpdateStock;
			}
		}
		else
		// 本地没有数据，需要试图重新下载
		{	
			StockBaseInfo ctnStockBaseInfo = new StockBaseInfo();
			int errStockBaseInfo = DataWebStockBaseInfo.getStockBaseInfo(id, ctnStockBaseInfo);
			if(0 == errStockBaseInfo)
			{
				// 下载日K，分红派息，基本信息
				int retdownloadStockDayk =  this.downloadStockDayk(id);
				int retdownloadStockDividendPayout =  this.downloadStockDividendPayout(id);
				int retdownloadBaseInfo =  this.downloadBaseInfo(id);
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
						cResultUpdateStock.error = 0;
						cResultUpdateStock.updateCnt = ctnKLineLocalNew.size();
						return cResultUpdateStock;
					}
					else
					{
						cResultUpdateStock.error = -23;
						return cResultUpdateStock;
					}
				}
				else
				// 下载日K，分红派息，基本信息 失败
				{
					cResultUpdateStock.error = -10;
					return cResultUpdateStock;
				}
			}
			else
			{
				// 获取网络最新有效交易日期失败
				cResultUpdateStock.error = -20;
				return cResultUpdateStock;
			}
		}
	}
	
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
	public int downloadBaseInfo(String id)
	{
		try
		{
			StockBaseInfo ctnStockBaseInfo = new StockBaseInfo();
			int errStockBaseInfo = DataWebStockBaseInfo.getStockBaseInfo(id, ctnStockBaseInfo);
			if(0 == errStockBaseInfo)
			{
				m_baseDataStorage.saveBaseInfo(id, ctnStockBaseInfo);
			}
			else
			{
				return -20;
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return -1;
		}
		return 0;
	}
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
