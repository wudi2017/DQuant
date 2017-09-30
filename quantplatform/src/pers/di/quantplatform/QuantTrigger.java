package pers.di.quantplatform;

public abstract class QuantTrigger {
	/*
	 * 交易日交易时间前触发 (09:27:00触发一次)
	 * 
	 */
	public abstract void onDayBegin(QuantContext ctx);
	
	/*
	 * 交易日交易时间每分钟数据推送  (上午09:30:00 ~ 11:30:00 下午13:00:00 ~ 15:00:00 每分钟触发一次)
	 * 
	 * 注意：
	 *     当天首次访问分时数据的股票，加入当天观察周期，之后可以持续获得每分钟分时数据，隔天后观察周期无效。
	 */
	public abstract void onEveryMinute(QuantContext ctx);
	
	/*
	 * 交易日交易时间后后触发 (21:00:00 触发一次)
	 * 
	 */
	public abstract void onDayEnd(QuantContext ctx);
}
