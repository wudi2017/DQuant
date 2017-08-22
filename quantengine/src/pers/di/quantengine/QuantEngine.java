package pers.di.quantengine;

public class QuantEngine {
	/*
	 * 配置量化引擎
	 * 1可以历史回测与实时运行
	 * 2可以配置触发时间
	 */
	public int config()
	{
		return 0;
	}
	
	/*
	 * 进入运行状态，根据配置进行触发回调
	 */
	public int run(QuantTriger triger)
	{
		return 0;
	}
}
