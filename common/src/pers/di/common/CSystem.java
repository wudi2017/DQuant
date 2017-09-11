package pers.di.common;

public class CSystem {
	
	public static void start()
	{
		CUtilsDateTime.start();
		
		CLog.config_setLogCfg("config", "log_config.xml");
		CLog.config_setLogFile("output", "default.log");
		CLog.start();
	}
	
	public static void stop()
	{
		CLog.stop();
		
		CUtilsDateTime.stop();
	}
}
