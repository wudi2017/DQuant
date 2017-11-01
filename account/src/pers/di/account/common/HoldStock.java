package pers.di.account.common;

public class HoldStock {
	
	public String createDate; // ��������
	public String stockID; // ��ƱID
	public int totalAmount; // �����������ɣ�
	public int availableAmount; // ��������
	public float avePrimeCostPrice; // ƽ���ɱ��ۣ�δȥ�����ã�
	public float curPrice; // ��ǰ��
	public float cost; // δ���㽻�׷���
	
	public HoldStock()
	{
		Clear();
	}
	
	public void Clear()
	{
		createDate = "0000-00-00";
		stockID = "";
		totalAmount = 0;
		availableAmount = 0;
		avePrimeCostPrice = 0.0f;
		curPrice = 0.0f;
		cost = 0.0f;
	}
	
	public void CopyFrom(HoldStock c)
	{
		createDate = c.createDate;
		stockID = c.stockID;
		totalAmount = c.totalAmount;
		availableAmount = c.availableAmount;
		avePrimeCostPrice = c.avePrimeCostPrice;
		curPrice = c.curPrice;
		cost = c.cost;
	}
	
	public float profit() // ����ֵ��ӯ���������㽻�׷��ã�
	{
		return (curPrice - avePrimeCostPrice)*totalAmount - cost;
	}
	
	public float profitRatio() // ����ȣ�ӯ��������
	{
		return profit()/(curPrice*totalAmount - cost);
	}
}
