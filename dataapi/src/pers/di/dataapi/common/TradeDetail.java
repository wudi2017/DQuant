package pers.di.dataapi.common;

/*
 * ���ڽ�����ϸ
 * ʱ��-�۸�-�ɽ���
 */
public class TradeDetail implements Comparable
{
	public String time;
	public double price;
	public double volume; 
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		TradeDetail sdto = (TradeDetail)o;
	    return this.time.compareTo(sdto.time);
	}
}