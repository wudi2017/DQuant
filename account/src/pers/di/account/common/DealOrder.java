package pers.di.account.common;

public class DealOrder {
	
	public String date;
	public String time;
	public String stockID;
	public TRANACT tranAct;
	public int amount; // �ɽ�����
	public double price; // �ɽ��۸�
	
	public double cost; // �ɽ�����(����ʱΪ������ã�����ʱΪƽ���������������֮��)
	
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
