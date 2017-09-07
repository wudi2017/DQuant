package pers.di.dataengine;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

import pers.di.common.CPath;
import pers.di.common.CUtilsDateTime;
import pers.di.dataengine.BaseDataDownload.ResultUpdateStock;
import pers.di.dataengine.BaseDataStorage.ResultAllStockFullDataTimestamps;
import pers.di.dataengine.webdata.DataWebStockAllList;
import pers.di.dataengine.webdata.DataWebStockBaseInfo;
import pers.di.dataengine.webdata.DataWebStockBaseInfo.ResultStockBaseInfo;
import pers.di.dataengine.webdata.DataWebStockDayDetail;
import pers.di.dataengine.webdata.DataWebStockDayDetail.ResultDayDetail;
import pers.di.dataengine.webdata.DataWebStockDayK;
import pers.di.dataengine.webdata.DataWebStockDayK.ResultKLine;
import pers.di.dataengine.webdata.DataWebStockDividendPayout;
import pers.di.dataengine.webdata.DataWebStockDividendPayout.ResultDividendPayout;
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
		
		// ����ָ��k
		String ShangZhiId = "999999";
		String ShangZhiName = "����ָ��";
		
		ResultUpdateStock cResultUpdateStockShangZhi = this.downloadStockFullData(ShangZhiId);
		String newestDate = "";
		if(0 == cResultUpdateStockShangZhi.error)
		{
			ResultKLine cResultKLine = m_baseDataStorage.getKLine(ShangZhiId);
			
			if(0 == cResultKLine.error && cResultKLine.resultList.size() > 0)
			{
				newestDate = cResultKLine.resultList.get(cResultKLine.resultList.size()-1).date;
			}
			
			s_fmt.format("update success: %s (%s) item:%d date:%s\n", ShangZhiId, ShangZhiName, cResultUpdateStockShangZhi.updateCnt, newestDate);
		}
		else
		{
			s_fmt.format("update ERROR: %s error(%d)\n", ShangZhiId, cResultUpdateStockShangZhi.error);
		}
		
		
		// ��������k
		List<StockItem> stockAllList = new ArrayList<StockItem>();
		int error = DataWebStockAllList.getAllStockList(stockAllList);
		if(0 == error)
		{
			for(int i = 0; i < stockAllList.size(); i++)  
	        {  
				StockItem cStockItem = stockAllList.get(i);
				
				String stockID = cStockItem.id;
				
				ResultUpdateStock cResultUpdateStock = this.downloadStockFullData(stockID);
	           
				if(0 == cResultUpdateStock.error)
				{
					ResultKLine cResultKLineQFQ = m_baseDataStorage.getKLine(stockID);
		    		if(0 == cResultKLineQFQ.error && cResultKLineQFQ.resultList.size() > 0)
		    		{
		    			String stockNewestDate = cResultKLineQFQ.resultList.get(cResultKLineQFQ.resultList.size()-1).date;
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
			System.out.println("ERROR:" + error);
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
		
		// ��ȡ��ǰ��Ч���ڣ������գ����������գ�
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//�������ڸ�ʽ
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
		
		// ��ȡ������k������ֺ���Ϣ����
		ResultKLine cResultKLineLocal = m_baseDataStorage.getKLine(id);
		ResultDividendPayout cResultDividendPayout = m_baseDataStorage.getDividendPayout(id);
		if(0 == cResultKLineLocal.error 
			&& 0 == cResultDividendPayout.error 
			&& cResultKLineLocal.resultList.size() != 0 
			/*&& retListLocalDividend.size() != 0 */)
		// ��������K����
		{
			// ��ȡ���������������
			KLine cKLineLast = cResultKLineLocal.resultList.get(cResultKLineLocal.resultList.size()-1);
			String localDataLastDate = cKLineLast.date; 
			//System.out.println("localDataLastDate:" + localDataLastDate);
			
			// �����ǰ���ڴ��ڱ�������������ڣ���Ҫ�������
			if(curValiddateStr.compareTo(localDataLastDate) > 0)
			{
				// ��ȡ��ǰBaseInfo��Ϣ
				StockBaseInfo ctnStockBaseInfo = new StockBaseInfo();
				int errStockBaseInfo = DataWebStockBaseInfo.getStockBaseInfo(id, ctnStockBaseInfo);
				if(0 == errStockBaseInfo)
				{
					// �����Ʊ������Ϣ
					StockBaseInfo cStockBaseData = new StockBaseInfo();
					cStockBaseData.name = ctnStockBaseInfo.name;
					cStockBaseData.allMarketValue = ctnStockBaseInfo.allMarketValue;
					cStockBaseData.circulatedMarketValue = ctnStockBaseInfo.circulatedMarketValue;
					cStockBaseData.peRatio = ctnStockBaseInfo.peRatio;
					m_baseDataStorage.saveBaseInfo(id, cStockBaseData);
					
					// ��ǰʱ��������֮ǰ������������Ч����Ϊǰһ�죨���������գ�
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
						// ��ȡ��һ������ĩ������
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
					
					// ����������Ч���ڱȱ��������£���Ҫ׷�Ӹ���
					if(webValidLastDate.compareTo(cKLineLast.date) > 0)
					{
						// �����������Ҫ��ȡ��һ��ʱ������
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
						
						// ��ȡ������K����
						ResultKLine cResultKLineMore = DataWebStockDayK.getKLine(id, fromDateStr, toDateStr);
						if(0 == cResultKLineMore.error)
						// ������K���ݻ�ȡ�ɹ�
						{
							// �򱾵������б���׷���µĸ�������
							for(int i = 0; i < cResultKLineMore.resultList.size(); i++)  
					        {  
								KLine cKLine = cResultKLineMore.resultList.get(i);  
								cResultKLineLocal.resultList.add(cKLine);
					        } 
							// ׷�Ӻ�ı����б���K���ݱ���������
							int retsetKLine = m_baseDataStorage.saveKLine(id, cResultKLineLocal.resultList);
							if(0 == retsetKLine)
							// ����ɹ�
							{
								// ���¸�Ȩ��������
								if(0 == this.downloadStockDividendPayout(id))
								{
									// ׷�ӳɹ�
									cResultUpdateStock.error = 0;
									cResultUpdateStock.updateCnt = cResultKLineMore.resultList.size();
									return cResultUpdateStock;
								}
								else
								{
									// ���¸�Ȩ����ʧ��
									cResultUpdateStock.error = -80;
									return cResultUpdateStock;
								}
							}
							else
							{
								//���汾������ʧ��
								cResultUpdateStock.error = -50;
								return cResultUpdateStock;
							}
						}
						else
						{
							// �����ȡ׷������ʧ��
							cResultUpdateStock.error = -40;
							return cResultUpdateStock;
						}
						
					}
					else
					{
						// �Ѿ�������������Ч����һ��
						cResultUpdateStock.error = 0;
						return cResultUpdateStock;
					}
				}
				else
				{
					// ��ȡ����������Ч��������ʧ��
					cResultUpdateStock.error = -20;
					return cResultUpdateStock;
				}
			}
			else
			{
				// ���������Ѿ�������
				cResultUpdateStock.error = 0;
				return cResultUpdateStock;
			}
		}
		else
		// ����û�����ݣ���Ҫ��ͼ��������
		{	
			StockBaseInfo ctnStockBaseInfo = new StockBaseInfo();
			int errStockBaseInfo = DataWebStockBaseInfo.getStockBaseInfo(id, ctnStockBaseInfo);
			if(0 == errStockBaseInfo)
			{
				// ������K���ֺ���Ϣ��������Ϣ
				int retdownloadStockDayk =  this.downloadStockDayk(id);
				int retdownloadStockDividendPayout =  this.downloadStockDividendPayout(id);
				int retdownloadBaseInfo =  this.downloadBaseInfo(id);
				if(0 == retdownloadStockDayk 
						&& 0 == retdownloadStockDividendPayout 
						&& 0 == retdownloadBaseInfo)
				// ������K���ֺ���Ϣ��������Ϣ �ɹ�
				{
					ResultKLine cResultKLineLocalNew = m_baseDataStorage.getKLine(id);
					if(cResultKLineLocalNew.error == 0)
					{
						//�����������سɹ���������
						cResultUpdateStock.error = 0;
						cResultUpdateStock.updateCnt = cResultKLineLocalNew.resultList.size();
						return cResultUpdateStock;
					}
					else
					{
						cResultUpdateStock.error = -23;
						return cResultUpdateStock;
					}
				}
				else
				// ������K���ֺ���Ϣ��������Ϣ ʧ��
				{
					cResultUpdateStock.error = -10;
					return cResultUpdateStock;
				}
			}
			else
			{
				// ��ȡ����������Ч��������ʧ��
				cResultUpdateStock.error = -20;
				return cResultUpdateStock;
			}
		}
	}
	
	public int downloadStockDayk(String id)
	{
		String curDate = CUtilsDateTime.GetCurDateStr();
		String paramToDate = curDate.replace("-", "");
		ResultKLine cResultKLine = DataWebStockDayK.getKLine(id, "20080101", paramToDate);
		if(0 == cResultKLine.error)
		{
			try
			{
				m_baseDataStorage.saveKLine(id, cResultKLine.resultList);
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage()); 
				return -1;
			}
		}
		else
		{
			System.out.println("ERROR:" + cResultKLine.error);
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
		ResultDividendPayout cResultDividendPayout = DataWebStockDividendPayout.getDividendPayout(id);
		if(0 == cResultDividendPayout.error)
		{
			try
			{
				m_baseDataStorage.saveDividendPayout(id, cResultDividendPayout.resultList);
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
	 * ����ĳֻ��Ʊ���ڽ������ݵ�����
	 * ����0Ϊ�ɹ�
	 */
	public int downloadStockDetail(String id, String date) {
		s_fmt.format("@downloadStocKLineDetail stockID(%s) date(%s)\n",id,date);
		
		ResultDayDetail cResultDayDetail = DataWebStockDayDetail.getDayDetail(id, date);
		if(0 == cResultDayDetail.error)
		{
			try
			{
				m_baseDataStorage.saveDayDetail(id, date, cResultDayDetail.resultList);
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
	
	private BaseDataStorage m_baseDataStorage;
	
	private Formatter s_fmt = new Formatter(System.out);
}
