package pers.di.dataengine;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

import pers.di.common.CPath;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.DataDownload.ResultUpdateStock;
import pers.di.dataengine.DataStorage.ResultAllStockFullDataTimestamps;
import pers.di.dataengine.webdata.DataWebStockAllList;
import pers.di.dataengine.webdata.DataWebStockDayDetail;
import pers.di.dataengine.webdata.DataWebStockDayDetail.ResultDayDetail;
import pers.di.dataengine.webdata.DataWebStockDayK;
import pers.di.dataengine.webdata.DataWebStockDayK.ResultKData;
import pers.di.dataengine.webdata.DataWebStockDividendPayout;
import pers.di.dataengine.webdata.DataWebStockDividendPayout.ResultDividendPayout;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo.ResultRealTimeInfo;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo.ResultRealTimeInfoMore;
import pers.di.dataengine.webdata.DataWebCommonDef.*;
import pers.di.dataengine.webdata.DataWebStockAllList.ResultAllStockList;

public class DataDownload {
	
	public DataDownload (DataStorage cDataStorage) 
	{
		m_dataStorage = cDataStorage;
	}
	
	public int downloadAllStockFullData(String dateStr)
	{
		ResultAllStockFullDataTimestamps cResultUpdatedStocksDate = m_dataStorage.getAllStockFullDataTimestamps();
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
			ResultKData cResultKData = m_dataStorage.getKData(ShangZhiId);
			
			if(0 == cResultKData.error && cResultKData.resultList.size() > 0)
			{
				newestDate = cResultKData.resultList.get(cResultKData.resultList.size()-1).date;
			}
			
			s_fmt.format("update success: %s (%s) item:%d date:%s\n", ShangZhiId, ShangZhiName, cResultUpdateStockShangZhi.updateCnt, newestDate);
		}
		else
		{
			s_fmt.format("update ERROR: %s error(%d)\n", ShangZhiId, cResultUpdateStockShangZhi.error);
		}
		
		
		// 更新所有k
		ResultAllStockList cResultAllStockList = DataWebStockAllList.getAllStockList();
		if(0 == cResultAllStockList.error)
		{
			for(int i = 0; i < cResultAllStockList.resultList.size(); i++)  
	        {  
				StockSimpleItem cStockSimpleItem = cResultAllStockList.resultList.get(i);
				
				String stockID = cStockSimpleItem.id;
				
				ResultUpdateStock cResultUpdateStock = this.downloadStockFullData(stockID);
	           
				if(0 == cResultUpdateStock.error)
				{
					ResultKData cResultKDataQFQ = m_dataStorage.getKData(stockID);
		    		if(0 == cResultKDataQFQ.error && cResultKDataQFQ.resultList.size() > 0)
		    		{
		    			String stockNewestDate = cResultKDataQFQ.resultList.get(cResultKDataQFQ.resultList.size()-1).date;
		    			s_fmt.format("update success: %s (%s) item:%d date:%s\n", cStockSimpleItem.id, cStockSimpleItem.name, cResultUpdateStock.updateCnt, stockNewestDate);
		    		}
		            else
		            {
		            	s_fmt.format("update ERROR: %s (%s) error(%d)\n", cStockSimpleItem.id, cStockSimpleItem.name, cResultUpdateStock.error);
		            }
				}
				else
				{
					s_fmt.format("update ERROR: %s error(%d)\n", cStockSimpleItem.id, cResultUpdateStock.error);
				}   
				
	        } 
			System.out.println("update finish, count:" + cResultAllStockList.resultList.size()); 
		}
		else
		{
			System.out.println("ERROR:" + cResultAllStockList.error);
		}
		
		if(newestDate.length() == "0000-00-00".length())
		{
			m_dataStorage.saveAllStockFullDataTimestamps(newestDate);
		}
		else
		{
			System.out.println("ERROR:" + "updateStocksFinish failed!");
		}
		
		return 0;
	}
	

	/*
	 * downloadStockData
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
		ResultKData cResultKDataLocal = m_dataStorage.getKData(id);
		ResultDividendPayout cResultDividendPayout = m_dataStorage.getDividendPayout(id);
		if(0 == cResultKDataLocal.error 
			&& 0 == cResultDividendPayout.error 
			&& cResultKDataLocal.resultList.size() != 0 
			/*&& retListLocalDividend.size() != 0 */)
		// 本地有日K数据
		{
			// 获取本地数据最后日期
			KData cKDataLast = cResultKDataLocal.resultList.get(cResultKDataLocal.resultList.size()-1);
			String localDataLastDate = cKDataLast.date; 
			//System.out.println("localDataLastDate:" + localDataLastDate);
			
			// 如果当前日期大于本地最后数据日期，需要继续检测
			if(curValiddateStr.compareTo(localDataLastDate) > 0)
			{
				// 获取当前更多实时信息
				ResultRealTimeInfoMore cResultRealTimeInfoMore = DataWebStockRealTimeInfo.getRealTimeInfoMore(id);
				if(0 == cResultRealTimeInfoMore.error)
				{
					// 保存股票基本信息
					StockBaseInfo cStockBaseData = new StockBaseInfo();
					cStockBaseData.name = cResultRealTimeInfoMore.realTimeInfoMore.name;
					cStockBaseData.price = cResultRealTimeInfoMore.realTimeInfoMore.curPrice;
					cStockBaseData.allMarketValue = cResultRealTimeInfoMore.realTimeInfoMore.allMarketValue;
					cStockBaseData.circulatedMarketValue = cResultRealTimeInfoMore.realTimeInfoMore.circulatedMarketValue;
					cStockBaseData.peRatio = cResultRealTimeInfoMore.realTimeInfoMore.peRatio;
					m_dataStorage.saveBaseInfo(id, cStockBaseData);
					
					// 当前时间在收盘之前，网络数据有效日期为前一天（非周六周日）
					String webValidLastDate = cResultRealTimeInfoMore.realTimeInfoMore.date;
					if(cResultRealTimeInfoMore.realTimeInfoMore.time.compareTo("15:00:00") < 0)
					{
						int year = Integer.parseInt(cResultRealTimeInfoMore.realTimeInfoMore.date.split("-")[0]);
						int month = Integer.parseInt(cResultRealTimeInfoMore.realTimeInfoMore.date.split("-")[1]);
						int day = Integer.parseInt(cResultRealTimeInfoMore.realTimeInfoMore.date.split("-")[2]);
						int hour = Integer.parseInt(cResultRealTimeInfoMore.realTimeInfoMore.time.split(":")[0]);
						int min = Integer.parseInt(cResultRealTimeInfoMore.realTimeInfoMore.time.split(":")[1]);
						int sec = Integer.parseInt(cResultRealTimeInfoMore.realTimeInfoMore.time.split(":")[2]);
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
					if(webValidLastDate.compareTo(cKDataLast.date) > 0)
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
						ResultKData cResultKDataMore = DataWebStockDayK.getKData(id, fromDateStr, toDateStr);
						if(0 == cResultKDataMore.error)
						// 新增日K数据获取成功
						{
							// 向本地数据列表中追加新的更新数据
							for(int i = 0; i < cResultKDataMore.resultList.size(); i++)  
					        {  
								KData cKData = cResultKDataMore.resultList.get(i);  
								cResultKDataLocal.resultList.add(cKData);
					        } 
							// 追加后的本地列表日K数据保存至本地
							int retsetKData = m_dataStorage.saveKData(id, cResultKDataLocal.resultList);
							if(0 == retsetKData)
							// 保存成功
							{
								// 更新复权因子数据
								if(0 == this.downloadStockDividendPayout(id))
								{
									// 追加成功
									cResultUpdateStock.error = 0;
									cResultUpdateStock.updateCnt = cResultKDataMore.resultList.size();
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
			// 下载日K，分红派息，基本信息
			int retdownloadStockDayk =  this.downloadStockDayk(id);
			int retdownloadStockDividendPayout =  this.downloadStockDividendPayout(id);
			int retdownloadBaseInfo =  this.downloadBaseInfo(id);
			if(0 == retdownloadStockDayk 
					&& 0 == retdownloadStockDividendPayout 
					&& 0 == retdownloadBaseInfo)
			// 下载日K，分红派息，基本信息 成功
			{
				ResultKData cResultKDataLocalNew = m_dataStorage.getKData(id);
				if(cResultKDataLocalNew.error == 0)
				{
					//最新数据下载成功返回天数
					cResultUpdateStock.error = 0;
					cResultUpdateStock.updateCnt = cResultKDataLocalNew.resultList.size();
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
	}
	
	public int downloadStockDayk(String id)
	{
		String curDate = CUtilsDateTime.GetCurDateStr();
		String paramToDate = curDate.replace("-", "");
		ResultKData cResultKData = DataWebStockDayK.getKData(id, "20080101", paramToDate);
		if(0 == cResultKData.error)
		{
			try
			{
				m_dataStorage.saveKData(id, cResultKData.resultList);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage()); 
				return -1;
			}
		}
		else
		{
			System.out.println("ERROR:" + cResultKData.error);
		}
		return 0;
	}
	public int downloadBaseInfo(String id)
	{
		try
		{
			ResultRealTimeInfoMore cResultRealTimeInfoMore = DataWebStockRealTimeInfo.getRealTimeInfoMore(id);
			if(0 == cResultRealTimeInfoMore.error)
			{
				StockBaseInfo cStockBaseInfo = new StockBaseInfo();
				cStockBaseInfo.name = cResultRealTimeInfoMore.realTimeInfoMore.name;
				cStockBaseInfo.price = cResultRealTimeInfoMore.realTimeInfoMore.curPrice;
				cStockBaseInfo.allMarketValue = cResultRealTimeInfoMore.realTimeInfoMore.allMarketValue;
				cStockBaseInfo.circulatedMarketValue = cResultRealTimeInfoMore.realTimeInfoMore.circulatedMarketValue;
				cStockBaseInfo.peRatio = cResultRealTimeInfoMore.realTimeInfoMore.peRatio;
				m_dataStorage.saveBaseInfo(id, cStockBaseInfo);
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
		ResultDividendPayout cResultDividendPayout = DataWebStockDividendPayout.getDividendPayout(id);
		if(0 == cResultDividendPayout.error)
		{
			try
			{
				m_dataStorage.saveDividendPayout(id, cResultDividendPayout.resultList);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage()); 
				return -1;
			}
		}
		else
		{
			System.out.println("ERROR:" + cResultDividendPayout.error);
			return -10;
		}
		return 0;
	}
	
	/*
	 * 下载某只股票日内交易数据到本地
	 * 返回0为成功
	 */
	public int downloadStockDetail(String id, String date) {
		s_fmt.format("@downloadStockDataDetail stockID(%s) date(%s)\n",id,date);
		
		ResultDayDetail cResultDayDetail = DataWebStockDayDetail.getDayDetail(id, date);
		if(0 == cResultDayDetail.error)
		{
			try
			{
				m_dataStorage.saveDayDetail(id, date, cResultDayDetail.resultList);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage()); 
				return -1;
			}
		}
		else
		{
			System.out.println("ERROR:" + cResultDayDetail.error);
			return -30;
		}
		return 0;
	}
	
	private String s_updateFinish = "updateFinish.txt";
	
	private DataStorage m_dataStorage;
	
	private Formatter s_fmt = new Formatter(System.out);
}
