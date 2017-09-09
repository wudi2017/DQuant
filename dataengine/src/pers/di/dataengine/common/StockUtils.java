package pers.di.dataengine.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import pers.di.dataengine.common.*;
import pers.di.common.*;

/**
 * 
 * @author wudi
 *
 * ��Ʊ������չ������
 * 1-��Ʊ��K���ܼ���
 * 2-��Ʊ���ڷ�ʱ���ܼ���
 */
public class StockUtils {
	
	// ����index���ٽ���ƽ���ۣ�count=1ʱ����ʾ���죬���죬����ľ��ۣ�
	static public float GetAveNear(List<KLine> dayklist, int count, int index)
	{
		if(dayklist.size() == 0) return 0.0f;
		float value = 0.0f;
		int iB = index-count;
		int iE = index+count;
		if(iB<0) iB=0;
		if(iE>dayklist.size()-1) iB=dayklist.size()-1;
		float sum = 0.0f;
		int sumcnt = 0;
		for(int i = iB; i <= iE; i++)  
        {  
			KLine cDayKData = dayklist.get(i);  
			sum = sum + cDayKData.midle();
			sumcnt++;
        }
		value = sum/sumcnt;
		return value;
	}
	
	// ���߼��㣬����date����ǰcount����߼۸�
	static public float GetMA(List<KLine> dayklist, int count, int index)
	{
		if(dayklist.size() == 0) return 0.0f;
		float value = 0.0f;
		int iE = index;
		int iB = iE-count+1;
		if(iB<0) iB=0;
		float sum = 0.0f;
		int sumcnt = 0;
		for(int i = iB; i <= iE; i++)  
        {  
			KLine cDayKData = dayklist.get(i);  
			sum = sum + cDayKData.close;
			sumcnt++;
        }
		value = sum/sumcnt;
		return value;
	}
	
	
	// ����ĳ�������ǵ������ο����̣�
	static public float GetInreaseRatioRefOpen(List<KLine> dayklist, int index)
	{
		float ratio = 0.0f;
		if(index >= 0 && index < dayklist.size())
		{
			KLine cKLineCur = dayklist.get(index);
			if(cKLineCur.close != 0)
			{
				ratio = (cKLineCur.close - cKLineCur.open)/cKLineCur.close;
			}
		}
		return ratio;
	}
	static public float GetInreaseRatioRefOpen(List<KLine> dayklist, String date)
	{
		int index = StockUtils.indexDayK(dayklist, date);
		return GetInreaseRatioRefOpen(dayklist, index);
	}
	
	// ����ĳ�������ǵ������ο��������̣�
	static public float GetInreaseRatio(List<KLine> dayklist, int index)
	{
		float ratio = 0.0f;
		if(index > 0 && index < dayklist.size())
		{
			KLine cKLineCur = dayklist.get(index);
			KLine cKLineBefore = dayklist.get(index-1);
			if(cKLineBefore.close != 0)
			{
				ratio = (cKLineCur.close - cKLineBefore.close)/cKLineBefore.close;
			}
		}
		return ratio;
	}
	static public float GetInreaseRatio(List<KLine> dayklist, String date)
	{
		int index = StockUtils.indexDayK(dayklist, date);
		return GetInreaseRatio(dayklist, index);
	}

	
	// ������������������list��ĳ����index����, -1Ϊû���ҵ�
	static public int indexDayK(List<KLine> dayklist, String dateStr)
	{
		int index = -1;
		for(int k = 0; k<dayklist.size(); k++ )
		{
			KLine cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.date.compareTo(dateStr) == 0)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	
	// ������������������list��ĳ���ڣ���/������֮��ĵ�һ��index����
	static public int indexDayKAfterDate(List<KLine> dayklist, String dateStr, boolean bContainSelf)
	{
		int index = 0;
		for(int k = 0; k<dayklist.size(); k++ )
		{
			KLine cDayKDataTmp = dayklist.get(k);
			boolean bFind = false;
			if(bContainSelf)
			{
				bFind = cDayKDataTmp.date.compareTo(dateStr) >= 0;
			}
			else
			{
				bFind = cDayKDataTmp.date.compareTo(dateStr) > 0;
			}
			if(bFind)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	
	// ������������������list��ĳ���ڣ���/������֮ǰ�ĵ�һ��index����
	static public int indexDayKBeforeDate(List<KLine> dayklist, String dateStr, boolean bContainSelf)
	{
		int index = 0;
		for(int k = dayklist.size()-1; k >= 0; k-- )
		{
			KLine cDayKDataTmp = dayklist.get(k);
			boolean bFind = false;
			if(bContainSelf)
			{
				bFind = cDayKDataTmp.date.compareTo(dateStr) <= 0;
			}
			else
			{
				bFind = cDayKDataTmp.date.compareTo(dateStr) < 0;
			}
			if(bFind)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	
	// ����i��j�յ���߼۸������
	static public int indexHigh(List<KLine> dayklist, int i, int j)
	{
		int index = i;
		float high = -100000.0f;
		for(int k = i; k<=j; k++ )
		{
			KLine cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.high > high) 
			{
				high = cDayKDataTmp.high;
				index = k;
			}
		}
		return index;
	}
	
	// ����i��j�յ���ͼ۸������
	static public int indexLow(List<KLine> dayklist, int i, int j)
	{
		int index = i;
		float low = 100000.0f;
		for(int k = i; k<=j; k++ )
		{
			KLine cDayKDataTmp = dayklist.get(k);
			if(cDayKDataTmp.low < low) 
			{
				low = cDayKDataTmp.low;
				index = k;
			}
		}
		return index;
	}
	
	public static List<KLine> subKLineData(List<KLine> oriList, String fromDate, String endDate)
	{
		List<KLine> newKLineData = new ArrayList<KLine>();
		for(int i = 0; i <oriList.size(); i++)  
        {  
			KLine cKLine = oriList.get(i);  
			if(cKLine.date.compareTo(fromDate) >= 0 &&
					cKLine.date.compareTo(endDate) <= 0)
			{
				KLine cNewKLine = new KLine();
				cNewKLine.CopyFrom(cKLine);
				newKLineData.add(cNewKLine);
			}
        }
		return newKLineData;
	}
	
	public static List<TimePrice> subTimePriceData(List<TimePrice> oriList, String fromTime, String endTime)
	{
		List<TimePrice> newTimePriceData = new ArrayList<TimePrice>();
		for(int i = 0; i <oriList.size(); i++)  
        {  
			TimePrice cTimePrice = oriList.get(i);  
			if(cTimePrice.time.compareTo(fromTime) >= 0 &&
					cTimePrice.time.compareTo(endTime) <= 0)
			{
				TimePrice cNewTimePrice = new TimePrice();
				cNewTimePrice.CopyFrom(cTimePrice);
				newTimePriceData.add(cNewTimePrice);
			}
        }
		return newTimePriceData;
	}
	
	// ����i��j�յ���߼۸������
	static public int indexTimePriceHigh(List<TimePrice> list, int i, int j)
	{
		int index = i;
		float high = -100000.0f;
		for(int k = i; k<=j; k++ )
		{
			TimePrice cTimePrice = list.get(k);
			if(cTimePrice.price > high) 
			{
				high = cTimePrice.price;
				index = k;
			}
		}
		return index;
	}
	
	// ����i��j�յ���ͼ۸������
	static public int indexTimePriceLow(List<TimePrice> list, int i, int j)
	{
		int index = i;
		float low = 100000.0f;
		for(int k = i; k<=j; k++ )
		{
			TimePrice cTimePrice = list.get(k);
			if(cTimePrice.price < low) 
			{
				low = cTimePrice.price;
				index = k;
			}
		}
		return index;
	}
	
	
	/**
	 * ***************************************************************************
	 * 
	 */
	
	public static CListObserver<KLine> subKLineListObserver(CListObserver<KLine> oriObs, String fromDate, String endDate)
	{
		CListObserver<KLine> newObs = new CListObserver<KLine>();
		int iBase = -1;
		int iSize = 0;
		for(int i = 0; i <oriObs.size(); i++)  
        {  
			KLine cKLine = oriObs.get(i);  
			if(cKLine.date.compareTo(fromDate) >= 0)
			{
				iBase = i;
			}
			if(iBase >= 0 && cKLine.date.compareTo(endDate) <= 0)
			{
				iSize++;
			}
        }
		if(iBase >=0)
		{
			newObs.build(oriObs, iBase, iSize);
		}
		return newObs;
	}
	
	// ������������������list��ĳ���ڣ���/������֮��ĵ�һ��index����
	static public int indexDayKAfterDate(CListObserver<KLine> oriObs, String dateStr, boolean bContainSelf)
	{
		int index = 0;
		for(int k = 0; k<oriObs.size(); k++ )
		{
			KLine cDayKDataTmp = oriObs.get(k);
			boolean bFind = false;
			if(bContainSelf)
			{
				bFind = cDayKDataTmp.date.compareTo(dateStr) >= 0;
			}
			else
			{
				bFind = cDayKDataTmp.date.compareTo(dateStr) > 0;
			}
			if(bFind)
			{
				index = k;
				break;
			}
		}
		return index;
	}
	
	static public int indexDayKBeforeDate(CListObserver<KLine> oriObs, String dateStr, boolean bContainSelf)
	{
		int index = 0;
		for(int k = oriObs.size()-1; k >= 0; k-- )
		{
			KLine cDayKDataTmp = oriObs.get(k);
			boolean bFind = false;
			if(bContainSelf)
			{
				bFind = cDayKDataTmp.date.compareTo(dateStr) <= 0;
			}
			else
			{
				bFind = cDayKDataTmp.date.compareTo(dateStr) < 0;
			}
			if(bFind)
			{
				index = k;
				break;
			}
		}
		return index;
	}
}
