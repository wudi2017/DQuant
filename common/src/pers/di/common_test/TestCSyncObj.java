package pers.di.common_test;

import pers.di.common.CLog;
import pers.di.common.CSyncObj;

public class TestCSyncObj {
	
	public static void main(String[] args) {
		
		CLog.output("TEST", "TestCSyncObj begin\n");
		
		CSyncObj cSync = new CSyncObj();
		
		cSync.Lock();
		
		cSync.UnLock();
	
		CLog.output("TEST", "TestCSyncObj end\n");
	}
}
