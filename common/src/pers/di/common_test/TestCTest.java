package pers.di.common_test;

import pers.di.common.CTest;
import pers.di.common.CTest.test;
import pers.di.common.CThread;

public class TestCTest {
	
	public static class TestFunctionX
	{
		@test
		public void testcaseX1()
		{
			CThread.sleep(20);
		}
	}
	
	public static class TestFunctionY
	{
		@test
		public void testcaseY1()
		{
			CThread.sleep(100);
		}
		
		@test
		public void testcaseY2()
		{
			CThread.sleep(200);
		}
	}
	
	public static class TestFunctionZ
	{
		public void testcaseZ1()
		{
			CThread.sleep(1000);
		}
		
		public void testcaseZ2()
		{
			CThread.sleep(1100);
		}
		
		@test
		public void testcaseZ3()
		{
			CThread.sleep(1200);
			CTest.EXPECT_TRUE(CTest.CURRENT_COSTTIME() < 1000);
		}
	}

	public static void main(String[] args) {
		
		CTest.ADD_TEST(TestFunctionX.class);
		CTest.ADD_TEST(TestFunctionY.class);
		CTest.ADD_TEST(TestFunctionZ.class);
		
		CTest.RUN_ALLTEST();
	}
}
