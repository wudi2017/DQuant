package pers.di.dataengine.common;

/*
 * ��ʱ�۸�
 * ʱ��-�۸�
 */
public class TimePrice {
	
	public TimePrice()
	{
	}
	
	public void CopyFrom(TimePrice fromObj)
	{
		this.time = fromObj.time;
		this.price = fromObj.price;
	}
	
	public String time;
	
	public Float price;
}