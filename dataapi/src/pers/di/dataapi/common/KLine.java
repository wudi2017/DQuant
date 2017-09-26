package pers.di.dataapi.common;

/*
 * K����
 * ʱ�� date time ����ʱ��ν���ʱ���
 * ����ʱ��-���̼�-���̼�-��ͼ�-��߼�-�ɽ���
 */
public class KLine implements Comparable
{
	// 2015-09-18 or null
	public String date;
	// 13:25:20 or null
	public String time;
	
	public float open;
	public float close;
	public float low;
	public float high;
	public float volume;
	
	public float midle()
	{
		return (open + close) / 2;
	}
	
	public void CopyFrom(KLine c)
	{
		date = c.date;
		time = c.time;
		open = c.open;
		close = c.close;
		high = c.high;
		low = c.low;
		volume = c.volume;
	}
	
	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		KLine sdto = (KLine)arg0;
		int iRet = 0;
		// date compare
		if(null != this.date && null != sdto.date)
		{
			iRet = this.date.compareTo(sdto.date);
			if(0 == iRet)
			{
				// time compare
				if(null != this.time && null != sdto.time)
				{
					return this.time.compareTo(sdto.time);
				}
				else if(null != this.time && null == sdto.time)
				{
					return 1;
				}
				else 
				{
					return 0;
				}
			}
			else
			{
				return iRet;
			}
		}
		else if(null != this.date && null == sdto.date)
		{
			return 1;
		}
		else 
		{
			return 0;
		}
	}
}