package pers.di.quantplatform.dataaccessor;

public class DAStock {

	/*
	 * ��ȡ��K��
	 * ע�⣺
	 *     ֻ�ܻ�ȡ�����ݸ��º����ʷ��K����ʱʵʱ��K���޷���ȡ
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
