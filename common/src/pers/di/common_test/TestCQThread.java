package pers.di.common_test;

import pers.di.common.*;
import pers.di.common.CQThread.*;

public class TestCQThread {
	
	public static class TestRequest extends CQThreadRequest
	{
		public TestRequest(int index)
		{
			m_index = index;
		}
		@Override
		public void doAction() {
			CLog.output("TEST", "TestRequest doAction! m_index [%d]\n", m_index);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		public int m_index;
	}

	public static void main(String[] args) {
		CLog.output("TEST", "TestCQThread Begin\n");
		
		CQThread cCQThread = new CQThread();
		
		cCQThread.startThread();
		
		for(int i=0; i<100; i++)
		{
			cCQThread.postRequest(new TestRequest(i));
		}
		
		cCQThread.stopThread();
		
		CLog.output("TEST", "TestCQThread End\n");
	}
}
