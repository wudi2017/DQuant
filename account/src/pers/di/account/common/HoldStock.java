package pers.di.account.common;

public class HoldStock {
	
	/*
	 * ע���ο�XX��ָ��Ա��ν��ֺ󣬾��������������γɵ�ԭʼ�ο���ֵ
	 */
	
	public String createDate; // ��������
	public String stockID; // ��ƱID
	public int totalAmount; // �����������ɣ�
	public int availableAmount; // ��������
	public double curPrice; // ��ǰ��
	public double refPrimeCostPrice; // �ο��ɱ��ۣ�ֻ�����β�λ���ֺ�Ĳο��ɱ��۸�δ��ʵ����;��
	
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
		curPrice = 0.0f;
		refPrimeCostPrice = 0.0f;
	}
	
	public void CopyFrom(HoldStock c)
	{
		createDate = c.createDate;
		stockID = c.stockID;
		totalAmount = c.totalAmount;
		availableAmount = c.availableAmount;
		curPrice = c.curPrice;
		refPrimeCostPrice = c.refPrimeCostPrice;
	}
	
	public double refProfit() // �ο�����ֵ��ֻ�����β�λ���ֺ�Ĳο�ӯ���������㽻�׷��ã�
	{
		return (curPrice - refPrimeCostPrice)*totalAmount;
	}
	
	public double refProfitRatio() // �ο�����ȣ�ֻ�����β�λ���ֺ�Ĳο�ӯ��������
	{
		return (curPrice - refPrimeCostPrice)/refPrimeCostPrice;
	}
}
