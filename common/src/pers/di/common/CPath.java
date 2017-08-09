package pers.di.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CPath {
	public static String getOutputDir()
	{
		return s_outputDir;
	}
	public static boolean createDir(String dirName)
	{
		File folder = new File(dirName);
		folder.mkdirs();
		return true;
	}
	
	private static String initOutputDir()
	{
		String outputDir = "output\\";
		SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmss");
		outputDir = outputDir + sdf.format(new Date());
		createDir(outputDir);
		return outputDir;
	}
	private static String s_outputDir = initOutputDir();
}
