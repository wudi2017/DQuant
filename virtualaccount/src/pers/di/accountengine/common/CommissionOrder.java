package pers.di.accountengine.common;

public class CommissionOrder {
	
	public String date;
	public String time;
	public String stockID;
	public TRANACT tranAct;
	public int amount; // 委托数量
	public float price; // 委托价格
	
	public void CopyFrom(CommissionOrder c)
	{
		date = c.date;
		time = c.time;
		stockID = c.stockID;
		tranAct = c.tranAct;
		amount = c.amount;
		price = c.price;
	}
}
