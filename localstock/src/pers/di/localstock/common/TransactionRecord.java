package pers.di.localstock.common;

/*
 * ���ڽ�����ϸ
 * ʱ��-�۸�-�ɽ���
 */
public class TransactionRecord implements Comparable
{
	public String time;
	public double price;
	public double volume; 
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		TransactionRecord sdto = (TransactionRecord)o;
	    return this.time.compareTo(sdto.time);
	}
}