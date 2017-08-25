package pers.di.quantengine;

public abstract class QuantTriger {
	public abstract void onHandler(String date, String time, 
			DataAccessor da, AccountController ac);
}
