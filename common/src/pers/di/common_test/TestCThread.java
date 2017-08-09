package pers.di.common_test;

import pers.di.common.CLog;
import pers.di.common.CThread;

public class TestCThread {
	
	public static class TestThread extends CThread 
	{
		@Override
		public void run() {
			CLog.output("TEST", "TestThread Run\n");
			while(!checkQuit())
			{
				CLog.output("TEST", "TestThread Running...\n");
				Wait(Long.MAX_VALUE);
			}
		}
		
	}
	public static void main(String[] args) {
		CLog.output("TEST", "Test TestCThread begin\n");
		
		TestThread cThread = new TestThread();
		
		cThread.startThread();
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		cThread.stopThread();
		
		CLog.output("TEST", "Test TestCThread end\n");
	}
}
