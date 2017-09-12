package pers.di.quantengine;

public abstract class QuantTriger {
	/*
	 * 数据推送
	 * 
	 * 注意：
	 *     当天首次访问分时数据的股票，加入当天观察周期，之后可以持续获得每分钟分时数据，隔天后观察周期无效。
	 */
	public abstract void onHandleData(QuantContext ctx);
}
