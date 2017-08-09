package pers.di.dataengine;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;

import pers.di.common.CPath;
import pers.di.dataengine.webdata.DataWebStockDayDetail;
import pers.di.dataengine.webdata.DataWebStockDayDetail.ResultDayDetail;
import pers.di.dataengine.webdata.DataWebStockDayK;
import pers.di.dataengine.webdata.DataWebStockDayK.ResultDayKData;
import pers.di.dataengine.webdata.DataWebStockDividendPayout;
import pers.di.dataengine.webdata.DataWebStockDividendPayout.ResultDividendPayout;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo.ResultRealTimeInfo;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo.ResultRealTimeInfoMore;
import pers.di.dataengine.webdata.DataWebCommonDef.*;

public class DataDownload {
	
	public DataDownload (String dataDir, DataStorage cDataStorage) 
	{
		s_workDir = dataDir;
		m_dataStorage = cDataStorage;
		CPath.createDir(s_workDir);
	}

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
	public ResultUpdateStock updateStock(String id)
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
		ResultDayKData cResultDayKDataLocal = m_dataStorage.getDayKData(id);
		ResultDividendPayout cResultDividendPayout = m_dataStorage.getDividendPayout(id);
		if(0 == cResultDayKDataLocal.error 
			&& 0 == cResultDividendPayout.error 
			&& cResultDayKDataLocal.resultList.size() != 0 
			/*&& retListLocalDividend.size() != 0 */)
		// ��������K����
		{
			// ��ȡ���������������
			DayKData cDayKDataLast = cResultDayKDataLocal.resultList.get(cResultDayKDataLocal.resultList.size()-1);
			String localDataLastDate = cDayKDataLast.date; 
			//System.out.println("localDataLastDate:" + localDataLastDate);
			
			// �����ǰ���ڴ��ڱ�������������ڣ���Ҫ�������
			if(curValiddateStr.compareTo(localDataLastDate) > 0)
			{
				// ��ȡ��ǰ����ʵʱ��Ϣ
				ResultRealTimeInfoMore cResultRealTimeInfoMore = DataWebStockRealTimeInfo.getRealTimeInfoMore(id);
				if(0 == cResultRealTimeInfoMore.error)
				{
					// �����Ʊ������Ϣ
					StockBaseInfo cStockBaseData = new StockBaseInfo();
					cStockBaseData.name = cResultRealTimeInfoMore.realTimeInfoMore.name;
					cStockBaseData.price = cResultRealTimeInfoMore.realTimeInfoMore.curPrice;
					cStockBaseData.allMarketValue = cResultRealTimeInfoMore.realTimeInfoMore.allMarketValue;
					cStockBaseData.circulatedMarketValue = cResultRealTimeInfoMore.realTimeInfoMore.circulatedMarketValue;
					cStockBaseData.peRatio = cResultRealTimeInfoMore.realTimeInfoMore.peRatio;
					m_dataStorage.saveStockBaseData(id, cStockBaseData);
					
					// ��ǰʱ��������֮ǰ������������Ч����Ϊǰһ�죨���������գ�
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
					if(webValidLastDate.compareTo(cDayKDataLast.date) > 0)
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
						ResultDayKData cResultDayKDataMore = DataWebStockDayK.getDayKData(id, fromDateStr, toDateStr);
						if(0 == cResultDayKDataMore.error)
						// ������K���ݻ�ȡ�ɹ�
						{
							// �򱾵������б���׷���µĸ�������
							for(int i = 0; i < cResultDayKDataMore.resultList.size(); i++)  
					        {  
								DayKData cDayKData = cResultDayKDataMore.resultList.get(i);  
								cResultDayKDataLocal.resultList.add(cDayKData);
					        } 
							// ׷�Ӻ�ı����б���K���ݱ���������
							int retsetDayKData = m_dataStorage.saveDayKData(id, cResultDayKDataLocal.resultList);
							if(0 == retsetDayKData)
							// ����ɹ�
							{
								// ���¸�Ȩ��������
								if(0 == this.downloadStockDividendPayout(id))
								{
									// ׷�ӳɹ�
									cResultUpdateStock.error = 0;
									cResultUpdateStock.updateCnt = cResultDayKDataMore.resultList.size();
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
			// ������K���ֺ���Ϣ��������Ϣ
			int retdownloadStockDayk =  this.downloadStockDayk(id);
			int retdownloadStockDividendPayout =  this.downloadStockDividendPayout(id);
			int retdownloadBaseInfo =  this.downloadBaseInfo(id);
			if(0 == retdownloadStockDayk 
					&& 0 == retdownloadStockDividendPayout 
					&& 0 == retdownloadBaseInfo)
			// ������K���ֺ���Ϣ��������Ϣ �ɹ�
			{
				ResultDayKData cResultDayKDataLocalNew = m_dataStorage.getDayKData(id);
				if(cResultDayKDataLocalNew.error == 0)
				{
					//�����������سɹ���������
					cResultUpdateStock.error = 0;
					cResultUpdateStock.updateCnt = cResultDayKDataLocalNew.resultList.size();
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
	}
	
	public int downloadStockDayk(String id)
	{
		String stockDataDir = s_workDir + "/" + id;
		if(!CPath.createDir(stockDataDir)) return -20;
		
		String stockDayKFileName = stockDataDir + "/" + s_daykFile;
		
		ResultRealTimeInfo cResultRealTimeInfo = DataWebStockRealTimeInfo.getRealTimeInfo(id);
		if(0 != cResultRealTimeInfo.error) return -20;
		String curAvailidDate = cResultRealTimeInfo.realTimeInfo.date;
		String curAvailidTime = cResultRealTimeInfo.realTimeInfo.time;
		
		File cfile =new File(stockDayKFileName);
		//System.out.println("updateStocData_Dayk:" + id);
		String paramToDate = curAvailidDate.replace("-", "");
		ResultDayKData cResultDayKData = DataWebStockDayK.getDayKData(id, "20080101", paramToDate);
		if(0 == cResultDayKData.error)
		{
			try
			{
				FileOutputStream cOutputStream = new FileOutputStream(cfile);
				for(int i = 0; i < cResultDayKData.resultList.size(); i++)  
		        {  
					DayKData cDayKData = cResultDayKData.resultList.get(i);  
//		            System.out.println(cDayKData.date + "," 
//		            		+ cDayKData.open + "," + cDayKData.close);  
		            cOutputStream.write((cDayKData.date + ",").getBytes());
		            cOutputStream.write((cDayKData.open + ",").getBytes());
		            cOutputStream.write((cDayKData.close + ",").getBytes());
		            cOutputStream.write((cDayKData.low + ",").getBytes());
		            cOutputStream.write((cDayKData.high + ",").getBytes());
		            cOutputStream.write((cDayKData.volume + "\n").getBytes());
		        } 
				cOutputStream.close();
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage()); 
				return -1;
			}
		}
		else
		{
			System.out.println("ERROR:" + cResultDayKData.error);
		}
		return 0;
	}
	public int downloadBaseInfo(String id)
	{
		String stockDataDir = s_workDir + "/" + id;
		if(!CPath.createDir(stockDataDir)) return -20;
		
		String stockBaseInfoFileName = stockDataDir + "/" + s_BaseInfoFile;
		File cfile =new File(stockBaseInfoFileName);
		// System.out.println("saveStockBaseData:" + id);
		try
		{
			ResultRealTimeInfoMore cResultRealTimeInfoMore = DataWebStockRealTimeInfo.getRealTimeInfoMore(id);
			
			if(0 == cResultRealTimeInfoMore.error)
			{
				FileOutputStream cOutputStream = new FileOutputStream(cfile);
				String s = String.format("%s,%.3f,%.3f,%.3f,%.3f", 
						cResultRealTimeInfoMore.realTimeInfoMore.name, 
						cResultRealTimeInfoMore.realTimeInfoMore.curPrice, 
						cResultRealTimeInfoMore.realTimeInfoMore.allMarketValue, 
						cResultRealTimeInfoMore.realTimeInfoMore.circulatedMarketValue, 
						cResultRealTimeInfoMore.realTimeInfoMore.peRatio);
				cOutputStream.write(s.getBytes());
				cOutputStream.close();
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
		String stockDataDir = s_workDir + "/" + id;
		if(!CPath.createDir(stockDataDir)) return -20;
		
		String stockDividendPayoutFileName = stockDataDir + "/" + s_DividendPayoutFile;
		
		ResultRealTimeInfo cResultRealTimeInfo = DataWebStockRealTimeInfo.getRealTimeInfo(id);
		if(0 != cResultRealTimeInfo.error) return -20;
		String curAvailidDate = cResultRealTimeInfo.realTimeInfo.date;
		String curAvailidTime = cResultRealTimeInfo.realTimeInfo.time;
		
		File cfile =new File(stockDividendPayoutFileName);
		// System.out.println("updateStocData_DividendPayout:" + id);
		ResultDividendPayout cResultDividendPayout = DataWebStockDividendPayout.getDividendPayout(id);
		if(0 == cResultDividendPayout.error)
		{
			try
			{
				FileOutputStream cOutputStream = new FileOutputStream(cfile);
				for(int i = 0; i < cResultDividendPayout.resultList.size(); i++)  
		        {  
					DividendPayout cDividendPayout = cResultDividendPayout.resultList.get(i);
					// System.out.println(cDividendPayout.date); 
					cOutputStream.write((cDividendPayout.date + ",").getBytes());
					cOutputStream.write((cDividendPayout.songGu + ",").getBytes());
					cOutputStream.write((cDividendPayout.zhuanGu + ",").getBytes());
					cOutputStream.write((cDividendPayout.paiXi + "\n").getBytes());
		        } 
				cOutputStream.close();
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
	public int downloadStockDataDetail(String id, String date) {
		s_fmt.format("@downloadStockDataDetail stockID(%s) date(%s)\n",id,date);
		
		String stockDataDir = s_workDir + "/" + id;
		if(!CPath.createDir(stockDataDir)) return -20;
		
		String stockDataDetailFileName = stockDataDir + "/" + date + ".txt";
		
		ResultRealTimeInfo cResultRealTimeInfo = DataWebStockRealTimeInfo.getRealTimeInfo(id);
		if(0 != cResultRealTimeInfo.error) return -20;
		String curAvailidDate = cResultRealTimeInfo.realTimeInfo.date;
		String curAvailidTime = cResultRealTimeInfo.realTimeInfo.time;
		
		ResultDayDetail cResultDayDetail = DataWebStockDayDetail.getDayDetail(id, date);
		if(0 == cResultDayDetail.error)
		{
			try
			{
				File cfile =new File(stockDataDetailFileName);
				FileOutputStream cOutputStream = new FileOutputStream(cfile);
				for(int i = 0; i < cResultDayDetail.resultList.size(); i++)  
		        {  
					DayDetailItem cDayDetailItem = cResultDayDetail.resultList.get(i);  
//			            System.out.println(cDayDetailItem.time + "," 
//			            		+ cDayDetailItem.price + "," + cDayDetailItem.volume);  
					cOutputStream.write((cDayDetailItem.time + ",").getBytes());
					cOutputStream.write((cDayDetailItem.price + ",").getBytes());
					cOutputStream.write((cDayDetailItem.volume + "\n").getBytes());
		        } 
				cOutputStream.close();
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
	
	private String s_workDir = "data";
	private String s_updateFinish = "updateFinish.txt";
	
	private String s_daykFile = "dayk.txt";
	private String s_DividendPayoutFile = "dividendPayout.txt";
	private String s_BaseInfoFile = "baseInfo.txt";
	
	private DataStorage m_dataStorage;
	
	private Formatter s_fmt = new Formatter(System.out);
}
