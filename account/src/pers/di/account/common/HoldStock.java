package pers.di.account.common;

public class HoldStock {
	
	public String stockID; // ��ƱID
	public int totalAmount; // �����������ɣ�
	public int availableAmount; // ��������
	public float refPrimeCostPrice; // �ο��ɱ���
	public float curPrice; // ��ǰ��
	public int investigationDays; // ��������������������տ�ʼ����
	
	public HoldStock()
	{
		Clear();
	}
	
	public void Clear()
	{
		stockID = "";
		totalAmount = 0;
		availableAmount = 0;
		refPrimeCostPrice = 0.0f;
		curPrice = 0.0f;
		investigationDays = 0;
	}
	
	public void CopyFrom(HoldStock c)
	{
		stockID = c.stockID;
		totalAmount = c.totalAmount;
		availableAmount = c.availableAmount;
		refPrimeCostPrice = c.refPrimeCostPrice;
		curPrice = c.curPrice;
		investigationDays = c.investigationDays;
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
