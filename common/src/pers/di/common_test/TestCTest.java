package pers.di.common_test;

import pers.di.common.CTest;
import pers.di.common.CThread;

public class TestCTest {
	
	public class TestFunctionX
	{
		public TestFunctionX()
		{
			
		}
		
		public void testcaseX1()
		{
			CThread.sleep(20);
		}
	}
	
	public class TestFunctionY
	{
		public void testcaseY1()
		{
			CThread.sleep(100);
		}
		
		public void testcaseY2()
		{
			CThread.sleep(200);
		}
	}
	
	public class TestFunctionZ
	{
		public void testcaseZ1()
		{
			CThread.sleep(1000);
		}
		
		public void testcaseZ2()
		{
			CThread.sleep(1500);
		}
		
		public void testcaseZ3()
		{
			CThread.sleep(1800);
		}
	}

	public static void main(String[] args) {
		
		CTest.ADD_TEST(TestFunctionX.class);
		CTest.ADD_TEST(TestFunctionY.class);
		CTest.ADD_TEST(TestFunctionZ.class);
		
		CTest.RUN_ALLTEST();
	}
}
