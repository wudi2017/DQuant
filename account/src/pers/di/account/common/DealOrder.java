package pers.di.account.common;

public class DealOrder {
	
	public String date;
	public String time;
	public String stockID;
	public TRANACT tranAct;
	public int amount; // 成交数量
	public float price; // 成交价格
	public float cost; // 成交费用
	
	public void CopyFrom(DealOrder c)
	{
		date = c.date;
		time = c.time;
		stockID = c.stockID;
		tranAct = c.tranAct;
		amount = c.amount;
		price = c.price;
	}
}
