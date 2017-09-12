package pers.di.common_test;

import pers.di.common.*;

public class TestCTest {
	
	public static class TestFunctionX
	{
		@CTest.test
		public void testcaseX1()
		{
			CThread.msleep(20);
		}
	}
	
	public static class TestFunctionY
	{
		@CTest.test
		public void testcaseY1()
		{
			CThread.msleep(100);
		}
		
		@CTest.test
		public void testcaseY2()
		{
			CThread.msleep(200);
		}
	}
	
	public static class TestFunctionZ
	{
		@CTest.setup
		public void setup()
		{
			
		}
		
		@CTest.teardown
		public void teardown()
		{
			
		}
		
		public void testcaseZ1()
		{
			CThread.msleep(1000);
		}
		
		public void testcaseZ2()
		{
			CThread.msleep(1100);
		}
		
		@CTest.test
		public void testcaseZ3()
		{
			CTest.TEST_PERFORMANCE_BEGIN();
			CThread.msleep(1200);
			CTest.EXPECT_TRUE(CTest.TEST_PERFORMANCE_END() < 1000);
		}
	}

	public static void main(String[] args) {
		
		CTest.ADD_TEST(TestFunctionX.class);
		CTest.ADD_TEST(TestFunctionY.class);
		CTest.ADD_TEST(TestFunctionZ.class);
		
		CTest.RUN_ALL_TESTS();
	}
}
