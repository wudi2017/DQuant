package pers.di.accountengine.common;

public class CommissionOrder {
	
	public String date;
	public String time;
	public String stockID;
	public TRANACT tranAct;
	public int amount; // ί������
	public float price; // ί�м۸�
	
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
