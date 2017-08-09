package pers.di.common_test;

import pers.di.common.CLog;
import pers.di.common.CWaitObj;

public class TestCWatiObj {
	public static class TestThread extends Thread
	{
		public  TestThread(CWaitObj waitObj)
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
		
		private CWaitObj m_WaitObj;
	}
	
	public static void main(String[] args) {
		
		CWaitObj cCWaitObj = new CWaitObj();
		
		TestThread cThread = new TestThread(cCWaitObj);
		cThread.start();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CLog.output("TEST", "CWaitObj.Wait ...1\n");
		cCWaitObj.Wait(Long.MAX_VALUE);
		CLog.output("TEST", "CWaitObj.Wait ...1 Return\n");
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CLog.output("TEST", "CWaitObj.Wait ...2\n");
		cCWaitObj.Wait(Long.MAX_VALUE);
		CLog.output("TEST", "CWaitObj.Wait ...2 Return\n");
	}
}
