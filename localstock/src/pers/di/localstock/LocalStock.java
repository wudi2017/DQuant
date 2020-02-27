package pers.di.localstock;
import pers.di.common.CSystem;
import pers.di.localstock.ILocalStock;
import pers.di.localstock.impl.LocalStockImpl;


public class LocalStock {
	public static ILocalStock instance() {  
		return s_instance;  
	} 
	
	private static ILocalStock s_instance = new LocalStockImpl(CSystem.getRWRoot() + "\\data"); 
	private LocalStock () {}
}
