package pers.di.dataapi;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import pers.di.common.*;
import pers.di.dataapi.*;
import pers.di.dataapi.common.*;

public class BaseDataDownload {
	
	public BaseDataDownload (BaseDataStorage cBaseDataStorage) 
	{
		m_baseDataStorage = cBaseDataStorage;
	}
	
	/*
	 * �������й�Ʊ���ݵ�����
	 * 
	 * ����ֵ��
	 *     ����0Ϊ�ɹ�������ֵΪʧ��
	 * ������
	 *     dateStr ���ص����������
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
		
		// ����ָ��k
		String ShangZhiId = "999999";
		String ShangZhiName = "����ָ��";
		
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
		
		
		// ��������k
		List<StockItem> stockAllList = new ArrayList<StockItem>();
		int errAllStockList = WebStockAPILayer.getAllStockList(stockAllList);
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
	 * ��������ֻ��Ʊ���ݵ�����
	 * 
	 * ����ֵ��
	 *     ����0Ϊ�ɹ�������ֵΪʧ��
	 * ������
	 *     container ������������ø�����K������
	 */
	public int downloadStockFullData(String id, CObjectContainer<Integer> container)
	{
		int error = 0;
		
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
		List<KLine> ctnKLineLocal = new ArrayList<KLine>();
		int errKline = m_baseDataStorage.getKLine(id, ctnKLineLocal);
		List<DividendPayout> ctnDividendPayout = new ArrayList<DividendPayout>();
		int errDividendPayout = m_baseDataStorage.getDividendPayout(id, ctnDividendPayout);
		if(0 == errKline
			&& 0 == errDividendPayout 
			&& ctnKLineLocal.size() != 0 
			/*&& retListLocalDividend.size() != 0 */)
		// ��������K����
		{
			// ��ȡ���������������
			KLine cKLineLast = ctnKLineLocal.get(ctnKLineLocal.size()-1);
			String localDataLastDate = cKLineLast.date; 
			//System.out.println("localDataLastDate:" + localDataLastDate);
			
			// �����ǰ���ڴ��ڱ�������������ڣ���Ҫ�������
			if(curValiddateStr.compareTo(localDataLastDate) > 0)
			{
				// ��ȡ��ǰBaseInfo��Ϣ
				StockInfo ctnStockInfo = new StockInfo();
				int errStockInfo = WebStockAPILayer.getStockInfo(id, ctnStockInfo);
				if(0 == errStockInfo)
				{
					// ��ǰʱ��������֮ǰ������������Ч����Ϊǰһ�죨���������գ�
					String webValidLastDate = ctnStockInfo.date;
					Double webCurPrice = ctnStockInfo.curPrice;
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
//					System.out.println(
//							String.format("webValidLastDate:%s CurPrice:%.3f", webValidLastDate, webCurPrice)
//							);
					
					// ����������Ч���ڱȱ��������£���Ҫ׷�Ӹ���
					if(webValidLastDate.compareTo(cKLineLast.date) > 0)
					{
						if(webCurPrice >= 0.0f)
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
							List<KLine> ctnKLineWebNew = new ArrayList<KLine>();
							int errKLineWebNew = WebStockAPILayer.getKLine(id, fromDateStr, toDateStr, ctnKLineWebNew);
							// ��ȡ����ֺ���Ϣ����
							List<DividendPayout> ctnDividendPayoutNew = new ArrayList<DividendPayout>();
							int errDividendPayoutNew = WebStockAPILayer.getDividendPayout(id, ctnDividendPayoutNew);
							
							if(0 == errKLineWebNew && 0 == errDividendPayoutNew)
							// ������K���ݻ�ȡ�ɹ�
							{
								// �򱾵������б���׷���µĸ�������
								for(int i = 0; i < ctnKLineWebNew.size(); i++)  
						        {  
									KLine cKLine = ctnKLineWebNew.get(i);  
									ctnKLineLocal.add(cKLine);
						        } 
								
								// ׷�Ӻ�ı����б���K���ݱ���������,���ҽ��ֺ���Ϣ��������Ϣһ������
								int retsetKLine = m_baseDataStorage.saveKLine(id, ctnKLineLocal);
								int retsetDividendPayout = m_baseDataStorage.saveDividendPayout(id, ctnDividendPayoutNew);
								int retsetBaseInfo = m_baseDataStorage.saveStockInfo(id, ctnStockInfo);

								if(0 == retsetKLine
										&& 0 == retsetDividendPayout
										&& 0 == retsetBaseInfo)
								// ����ɹ�
								{
									error = 0;
									container.set(ctnKLineWebNew.size());
									return error;
								}
								else
								{
									//���汾������ʧ��
									error = -50;
									return error;
								}
							}
							else
							{
								// �����ȡ��K���ֺ���Ϣ����ʧ��
								error = -40;
								return error;
							}
						}
						else
						{
							// �������¼۸���Ч
							error = -41;
							return error;
						}
					}
					else
					{
						// �Ѿ�������������Ч����һ��
						error = 0;
						container.set(0);
						return error;
					}
				}
				else
				{
					// ��ȡ����������Ч��������ʧ��
					error = -20;
					return error;
				}
			}
			else
			{
				// ���������Ѿ�������
				error = 0;
				container.set(0);
				return error;
			}
		}
		else
		// ����û�����ݣ���Ҫ��ͼ��������
		{	
			StockInfo ctnStockInfoNew = new StockInfo();
			int errStockInfoNew = WebStockAPILayer.getStockInfo(id, ctnStockInfoNew); // ��ȡ���������Ϣ����
			if(0 == errStockInfoNew)
			{
				String curDate = CUtilsDateTime.GetCurDateStr();
				String paramToDate = curDate.replace("-", "");
				List<KLine> ctnKLineNew = new ArrayList<KLine>();
				int errGetWebKLineNew = WebStockAPILayer.getKLine(id, "19900101", paramToDate, ctnKLineNew);// ��ȡ������K����
				
				List<DividendPayout> ctnDividendPayoutNew = new ArrayList<DividendPayout>();
				int errDividendPayoutNew = WebStockAPILayer.getDividendPayout(id, ctnDividendPayoutNew);//��ȡ����ֺ���Ϣ����
				
				if(0 == errGetWebKLineNew 
						&& 0 == errDividendPayoutNew 
						&& 0 == errStockInfoNew)
				// �����ȡ��K���ֺ���Ϣ��������Ϣ �ɹ�
				{
					try
					{
						// ���浽����
						m_baseDataStorage.saveKLine(id, ctnKLineNew);
						m_baseDataStorage.saveDividendPayout(id, ctnDividendPayoutNew);
						m_baseDataStorage.saveStockInfo(id, ctnStockInfoNew);
					}
					catch(Exception e)
					{
						e.printStackTrace();
						System.out.println(e.getMessage()); 
						error = -21;
						return error;
					}
					
					List<KLine> ctnKLineLocalNew = new ArrayList<KLine>();
					int errKLineLocalNew = m_baseDataStorage.getKLine(id, ctnKLineLocalNew);
					if(errKLineLocalNew == 0)
					{
						//�����������سɹ���������
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
				// ������K���ֺ���Ϣ��������Ϣ ʧ��
				{
					error = -10;
					return error;
				}
			}
			else
			{
				// ��ȡ����������Ч��������ʧ��
				error = -20;
				return error;
			}
		}
	}
	
	/*
	 * ������K
	 */
	public int downloadStockDayk(String id)
	{
		String curDate = CUtilsDateTime.GetCurDateStr();
		String paramToDate = curDate.replace("-", "");
		List<KLine> ctnKLine = new ArrayList<KLine>();
		int error = WebStockAPILayer.getKLine(id, "20080101", paramToDate, ctnKLine);
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
	 * ���ع�Ʊ��Ϣ
	 */
	public int downloadStockInfo(String id)
	{
		try
		{
			StockInfo ctnStockInfo = new StockInfo();
			int errStockInfo = WebStockAPILayer.getStockInfo(id, ctnStockInfo);
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
	 * ���طֺ���Ϣ
	 */
	public int downloadStockDividendPayout(String id)
	{
		List<DividendPayout> ctnDividendPayout = new ArrayList<DividendPayout>();
		int errDividendPayout = WebStockAPILayer.getDividendPayout(id, ctnDividendPayout);
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
	 * ����ĳֻ��Ʊ���ڽ������ݵ�����
	 * ����0Ϊ�ɹ�
	 */
	public int downloadStockDetail(String id, String date) {
		s_fmt.format("@downloadStocKLineDetail stockID(%s) date(%s)\n",id,date);
		
		List<TransactionRecord> ctnTransactionRecord = new ArrayList<TransactionRecord>();
		int error = WebStockAPILayer.getTransactionRecordHistory(id, date, ctnTransactionRecord);
		if(0 == error)
		{
			try
			{
				m_baseDataStorage.saveDayDetail(id, date, ctnTransactionRecord);
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
