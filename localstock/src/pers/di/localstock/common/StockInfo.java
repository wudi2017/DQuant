package pers.di.localstock.common;

/*
 * ��Ʊ������Ϣ
 * ����-��ǰ��-����ֵ-��ͨ��ֵ-��ӯ�ʵȣ�������չΪ��ҵ�ȣ�
 */
public class StockInfo
{
	public String name;
	public String date;
	public String time;
	public double curPrice;
	public double allMarketValue; // ��
	public double circulatedMarketValue; // ��
	public double peRatio;
	public StockInfo()
	{
		name = "";
		date = "0000-00-00";
		time = "00:00:00";
		curPrice =0.0f;
		allMarketValue = 0.0f;
		circulatedMarketValue = 0.0f;
		peRatio = 0.0f;
	}
	public void CopyFrom(StockInfo cCopyFromObj)
	{
		name = cCopyFromObj.name;
		date = cCopyFromObj.date;
		time = cCopyFromObj.time;
		curPrice = cCopyFromObj.curPrice;
		allMarketValue = cCopyFromObj.allMarketValue;
		circulatedMarketValue = cCopyFromObj.circulatedMarketValue;
		peRatio = cCopyFromObj.peRatio;
	}
}