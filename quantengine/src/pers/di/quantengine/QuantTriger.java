package pers.di.quantengine;

public abstract class QuantTriger {
	public abstract void onNewDayInit(QuantContext ctx);
	public abstract void onHandler(QuantContext ctx);
}
