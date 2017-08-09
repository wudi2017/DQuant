package pers.di.dataengine;

public class DataEngine {
	private static DataEngine s_instance = new DataEngine();  
	private DataEngine () {}
	public static DataEngine instance() {  
		return s_instance;  
	}  
	
	/*
	 * update
	 */
}
