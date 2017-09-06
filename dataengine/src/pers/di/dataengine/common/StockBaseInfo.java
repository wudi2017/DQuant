package pers.di.dataengine.common;

/*
 * ��Ʊ������Ϣ
 * ����-��ǰ��-����ֵ-��ͨ��ֵ-��ӯ�ʵȣ�������չΪ��ҵ�ȣ�
 */
public class StockBaseInfo
{
	public String name;
	public String date;
	public String time;
	public float allMarketValue; // ��
	public float circulatedMarketValue; // ��
	public float peRatio;
	public StockBaseInfo()
	{
		name = "";
		date = "0000-00-00";
		time = "00:00:00";
		allMarketValue = 0.0f;
		circulatedMarketValue = 0.0f;
		peRatio = 0.0f;
	}
	public void CopyFrom(StockBaseInfo cCopyFromObj)
	{
		name = cCopyFromObj.name;
		date = cCopyFromObj.date;
		time = cCopyFromObj.time;
		allMarketValue = cCopyFromObj.allMarketValue;
		circulatedMarketValue = cCopyFromObj.circulatedMarketValue;
		peRatio = cCopyFromObj.peRatio;
	}
}