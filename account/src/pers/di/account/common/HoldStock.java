package pers.di.account.common;

public class HoldStock {
	
	public String createDate; // ��������
	public String stockID; // ��ƱID
	public int totalAmount; // �����������ɣ�
	public int availableAmount; // ��������
	public float refPrimeCostPrice; // �ο��ɱ���
	public float curPrice; // ��ǰ��
	
	
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
		refPrimeCostPrice = 0.0f;
		curPrice = 0.0f;
	}
	
	public void CopyFrom(HoldStock c)
	{
		createDate = c.createDate;
		stockID = c.stockID;
		totalAmount = c.totalAmount;
		availableAmount = c.availableAmount;
		refPrimeCostPrice = c.refPrimeCostPrice;
		curPrice = c.curPrice;
	}
	
	public float profit() // ����ֵ��ӯ���������㽻�׷��ã�
	{
		return (curPrice - refPrimeCostPrice)*totalAmount;
	}
	
	public float profitRatio() // ����ȣ�ӯ��������
	{
		return (curPrice - refPrimeCostPrice)/refPrimeCostPrice;
	}
}
