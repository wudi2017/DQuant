package pers.di.account.common;

import java.util.Comparator;

public class CommissionOrder implements Comparator {
	
	public String date;
	public String time;
	public String stockID;
	public TRANACT tranAct;
	public int amount; // 委托数量
	public double price; // 委托价格
	public int dealAmount; // 已成交数量
	
	public void CopyFrom(CommissionOrder c)
	{
		date = c.date;
		time = c.time;
		stockID = c.stockID;
		tranAct = c.tranAct;
		amount = c.amount;
		price = c.price;
		dealAmount = c.dealAmount;
	}

	@Override
	public int compare(Object arg0, Object arg1) {
		
		CommissionOrder c0 = (CommissionOrder)arg0;
		CommissionOrder c1 = (CommissionOrder)arg1;
		
		if(TRANACT.BUY == tranAct)
		{
			if(c0.price < c1.price)
			{
				return 1;
			}
			else if(c0.price > c1.price)
			{
				return -1;
			}
			else
			{
				return c0.time.compareTo(c1.time);
			}
		}
		else if(TRANACT.SELL == tranAct)
		{
			if(c0.price < c1.price)
			{
				return -1;
			}
			else if(c0.price > c1.price)
			{
				return 1;
			}
			else
			{
				return c0.time.compareTo(c1.time);
			}
		}
		else
		{
			return 0;
		}
	}
}
