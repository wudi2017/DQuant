package pers.di.dataengine.common;

/*
 * 分时价格
 * 时间-价格
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