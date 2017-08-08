package pers.wudi.base.test;

import pers.wudi.base.BLog;
import pers.wudi.base.BSyncObj;

public class TestBSyncObj {
	
	public static void main(String[] args) {
		
		BLog.output("TEST", "TestBSyncObj begin\n");
		
		BSyncObj cSync = new BSyncObj();
		
		cSync.Lock();
		
		cSync.UnLock();
	
		BLog.output("TEST", "TestBSyncObj end\n");
	}
}
