package pers.di.quantplatform.dataaccessor;

public class DAStock {

	/*
	 * 获取日K线
	 * 注意：
	 *     只能获取到数据更新后的历史日K，当时实时日K线无法获取
	 */
	public DAKLines dayKLines()
	{
		return new DAKLines();
	}
	
	public DATimePrices timePrices(String date)
	{
		return new DATimePrices();
	}
}
