package pers.di.dataengine.common;

/*
 * 日内交易明细
 * 时间-价格-成交量
 */
public class TradeDetail implements Comparable
{
	public String time;
	public float price;
	public float volume; 
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		TradeDetail sdto = (TradeDetail)o;
	    return this.time.compareTo(sdto.time);
	}
}