package pers.di.dataengine.common;

/*
 * 股票基本信息
 * 名字-当前价-总市值-流通市值-市盈率等（将来扩展为行业等）
 */
public class StockBaseInfo
{
	public String name;
	public String date;
	public String time;
	public float allMarketValue; // 亿
	public float circulatedMarketValue; // 亿
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