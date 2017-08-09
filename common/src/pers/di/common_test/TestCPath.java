package pers.di.common_test;

import pers.di.common.CLog;
import pers.di.common.CPath;

public class TestCPath {
	public static void main(String[] args) {
		
		CLog.output("TEST", "%s", CPath.getOutputDir());
		
	}
}
