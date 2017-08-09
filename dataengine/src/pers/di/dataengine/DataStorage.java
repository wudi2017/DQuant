package pers.di.dataengine;

import java.util.Formatter;

import pers.di.common.CPath;

public class DataStorage {
	
	public DataStorage (String workDir) 
	{
		s_workDir = workDir;
		CPath.createDir(s_workDir);
	}

	private String s_workDir = "data";
	private String s_updateFinish = "updateFinish.txt";
	
	private String s_daykFile = "dayk.txt";
	private String s_DividendPayoutFile = "dividendPayout.txt";
	private String s_BaseInfoFile = "baseInfo.txt";
	
	static private Formatter s_fmt = new Formatter(System.out);
}
