package pers.di.dataapi.common;

/*
 * �ֺ���Ϣ����
 * ����-�͹�-ת��-��Ϣ
 */
public class DividendPayout implements Comparable
{
	public String date;
	public double songGu;
	public double zhuanGu;
	public double paiXi;
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		DividendPayout sdto = (DividendPayout)o;
	    return this.date.compareTo(sdto.date);
	}
}
