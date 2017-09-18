package pers.di.dataengine;

public abstract class DataListener {

	public abstract void onDayBegin(DataContext ctx);
	
	public abstract void onTransactionEveryMinute(DataContext ctx);
	
	public abstract void onDayEnd(DataContext ctx);
}
