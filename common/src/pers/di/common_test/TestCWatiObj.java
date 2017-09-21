package pers.di.common_test;

import pers.di.common.CLog;
import pers.di.common.CWaitObject;

public class TestCWatiObj {
	public static class TestThread extends Thread
	{
		public  TestThread(CWaitObject waitObj)
		{
			m_WaitObj = waitObj;
		}
		
		@Override
		public void run()
		{
			CLog.output("TEST", "TestThread run begin\n");
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			m_WaitObj.Notify();
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			m_WaitObj.Notify();
			
			
			CLog.output("TEST", "TestThread run end\n");
		}
		
		private CWaitObject m_WaitObj;
	}
	
	public static void main(String[] args) {
		
		CWaitObject cCWaitObject = new CWaitObject();
		
		TestThread cThread = new TestThread(cCWaitObject);
		cThread.start();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CLog.output("TEST", "CWaitObject.Wait ...1\n");
		cCWaitObject.Wait(Long.MAX_VALUE);
		CLog.output("TEST", "CWaitObject.Wait ...1 Return\n");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CLog.output("TEST", "CWaitObject.Wait ...2\n");
		cCWaitObject.Wait(Long.MAX_VALUE);
		CLog.output("TEST", "CWaitObject.Wait ...2 Return\n");
	}
}
