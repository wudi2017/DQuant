package pers.di.quantplatform;

public abstract class QuantStrategy {
	
	public abstract void onInit(QuantContext ctx);
	
	public abstract void onDayStart(QuantContext ctx);
	
	public abstract void onMinuteData(QuantContext ctx);
	
	public abstract void onDayFinish(QuantContext ctx);
	
	public final boolean addCurrentDayInterestMinuteDataID(String ID)
	{
		return true;
	}
}
