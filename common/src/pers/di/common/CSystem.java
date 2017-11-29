package pers.di.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CSystem {
	
	public static void start()
	{
		CUtilsDateTime.start();
		
		// config log
		String logconfigDir = getRWRoot() + "\\config";
		String logDir = getRunSessionRoot();
		CLog.config_setLogCfg(logconfigDir, "log_config.xml");
		CLog.config_setLogFile(logDir, "default.log");
		CLog.start();
	}
	
	public static void stop()
	{
		CLog.stop();
		
		CUtilsDateTime.stop();
	}
	
	public static String getRWRoot()
	{
		return s_RWRoot;
	}
	
	public static String getRunSessionRoot()
	{
		return s_RunSessionRoot;
	}
	
	private static String initRWRootDir()
	{
		String rwRoot = CSystem.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		rwRoot = CFileSystem.getParentDir(rwRoot);
//		rwRoot = CFileSystem.getParentDir(rwRoot);
		rwRoot = rwRoot + "\\rw";
		if(!CFileSystem.isDirExist(rwRoot))
		{
			CFileSystem.createDir(rwRoot);
		}
		return rwRoot;
	}
	private static String initRunSessionDir()
	{
		String rwRoot = initRWRootDir();
		SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmss");
		String runSessionRoot = rwRoot + "\\RunSession\\" + sdf.format(new Date());
		if(!CFileSystem.isDirExist(runSessionRoot))
		{
			CFileSystem.createDir(runSessionRoot);
		}
		return runSessionRoot;
	}
	private static String s_RWRoot = initRWRootDir();
	private static String s_RunSessionRoot = initRunSessionDir();
}
