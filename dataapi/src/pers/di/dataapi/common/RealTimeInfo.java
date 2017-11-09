package pers.di.dataapi.common;

/*
 * 股票实时信息
 * 名字-日期-时间-当前价
 */
public class RealTimeInfo implements Comparable
{
	public String name;
	public String date;
	public String time;
	public double curPrice;
	
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		RealTimeInfo sdto = (RealTimeInfo)o;
		int iCheck1 = this.date.compareTo(sdto.date);
		if(0 == iCheck1)
		{
			int iCheck2 = this.time.compareTo(sdto.time);
			return iCheck2;
		}
		else
		{
			return iCheck1;
		}
	}
}