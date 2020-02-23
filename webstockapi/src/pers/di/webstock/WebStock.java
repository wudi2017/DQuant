package pers.di.webstock;

import pers.di.webstock.func.WebStockAPIImpl;

public class WebStock {
	
	public static IWebStock instance() {  
		return s_instance;  
	} 
	
	private static IWebStock s_instance = new WebStockAPIImpl(); 
	private WebStock () {}
}
