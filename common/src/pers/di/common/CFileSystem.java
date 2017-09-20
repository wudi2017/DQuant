package pers.di.common;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CFileSystem {
	public static String getOutputDir()
	{
		return s_outputDir;
	}
	public static boolean isDirExist(String dirName)
	{
		File folder = new File(dirName);
		if(folder.exists() && folder.isDirectory())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public static boolean isFileExist(String fileName)
	{
		File folder = new File(fileName);
		if(folder.exists() && !folder.isDirectory())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public static boolean createDir(String dirName)
	{
		File folder = new File(dirName);
		folder.mkdirs();
		return true;
	}
	public static int clearDir(String dirName)
	{
		File folder =new File(dirName);
		if(!folder.exists())      
		{        
			return 0;
		}
		return help_deleteFile(folder);
	}
	public static int removeDir(String dirName)
	{
		File folder =new File(dirName);
		if(!folder.exists())      
		{        
			return 0;
		}
		if(0 == help_deleteFile(folder))
		{
			return 0;
		}
		if(folder.exists())
		{
			return -1;
		}
		else
		{
			return 0;
		}
	}
	
	/*
	 * ********************************************************************
	 */
	
	private static int help_deleteFile(File file) {  
	    if (file.exists()) 
	    {
			if (file.isFile()) 
			{
				//���ļ�  
			    if(!file.delete()) //ɾ���ļ�   
			    {
			    	return -1;
			    }
			} 
			else if (file.isDirectory()) 
			{
				//��һ��Ŀ¼  
			    File[] files = file.listFiles();//����Ŀ¼�����е��ļ� files[];  
			    for (int i = 0;i < files.length;i ++) {//����Ŀ¼�����е��ļ�  
			    	help_deleteFile(files[i]);//��ÿ���ļ�������������е���  
			    }  
			    if(!file.delete()) //ɾ���ļ���  
			    {
			    	return -1;
			    }
			 }  
	    } 
	    return 0;
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
