package pers.di.dataengine;

public class StockDataEngine {
	private static StockDataEngine s_instance = new StockDataEngine(); 
	private StockDataEngine ()
	{
	}
	public static StockDataEngine instance() {  
		return s_instance;  
	} 
}
