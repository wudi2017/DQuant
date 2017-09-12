package pers.di.quantengine;

public abstract class QuantTriger {
	/*
	 * 数据推送
	 * 
	 * 注意：
	 *     在实时模式下，当天首次访问分时数据的股票，加入当天观察周期，之后可以持续每分钟获得分时数据，隔天无效。
	 *     在历史回测模式下，可以访问本地所有的分钟分时线
	 */
	public abstract void onHandleData(QuantContext ctx);
}
