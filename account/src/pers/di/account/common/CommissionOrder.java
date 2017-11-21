package pers.di.account.common;

public class CommissionOrder implements Comparable {
	
	public String date;
	public String time;
	public String stockID;
	public TRANACT tranAct;
	public int amount; // ί������
	public double price; // ί�м۸�
	public int dealAmount; // �ѳɽ�����
	
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
	public int compareTo(Object o) {
		CommissionOrder c0 = (CommissionOrder)this;
		CommissionOrder c1 = (CommissionOrder)o;
		
		// �Ȱ�id����
		if(!c0.stockID.equals(c1.stockID))
		{
			return c0.stockID.compareTo(c1.stockID);
		}
		
		// �ٰ���������
		if(TRANACT.BUY == c0.tranAct && TRANACT.SELL == c1.tranAct)
		{
			return -1;
		}
		else if(TRANACT.SELL == c0.tranAct && TRANACT.BUY == c1.tranAct)
		{
			return 1;
		}
		
		// ����������ͬ ���۸�����
		if(TRANACT.BUY == c0.tranAct)
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
				// ���ʱ������
				return c0.time.compareTo(c1.time);
			}
		}
		else if(TRANACT.SELL == c0.tranAct)
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
				// ���ʱ������
				return c0.time.compareTo(c1.time);
			}
		}
		else
		{
			return 0;
		}
		
	}
}
