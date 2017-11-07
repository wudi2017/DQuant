package pers.di.common_test;

import pers.di.common.*;

public class TestCUtilsMath {
	
	@CTest.test
	public static void test_math45()
	{
		float pi=3.1415926f;
		int iTest = 0;
		for(int i=0; i<10000*10000; i++)
		{
			iTest += Math.round(pi);
		}
		CLog.output("TEST", "iTest %d",iTest);
	}
	
	@CTest.test
	public static void test_ooo()
	{
		
		CLog.output("TEST", "saveNDecimal %f\n", CUtilsMath.saveNDecimalIgnore(2.199f, 1));
		CLog.output("TEST", "saveNDecimal %f\n", CUtilsMath.saveNDecimalIgnore(2.199f, 2));
		CLog.output("TEST", "saveNDecimal %f\n", CUtilsMath.saveNDecimalIgnore(2.199f, 3));
		
		
		CLog.output("TEST", "randomFloat %f\n", CUtilsMath.randomFloat());
		CLog.output("TEST", "randomFloat %f\n", CUtilsMath.randomFloat());
		CLog.output("TEST", "randomFloat %f\n", CUtilsMath.randomFloat());
		
		
		
		CLog.output("TEST", "saveNDecimal45 %.3f = 9.19?\n", CUtilsMath.saveNDecimal(8.35f*1.1f, 2));
		CLog.output("TEST", "saveNDecimal45 %.3f = 27.42?\n", CUtilsMath.saveNDecimal(24.93f*1.1f, 2));
		CLog.output("TEST", "saveNDecimal45 %.3f = 30.16?\n", CUtilsMath.saveNDecimal(27.42f*1.1f, 2));
		CLog.output("TEST", "saveNDecimal45 %.3f = 30.18?\n", CUtilsMath.saveNDecimal(30.16f*1.1f, 2));
		
	}
	public static void main(String[] args) {
		CTest.ADD_TEST(TestCUtilsMath.class);
		CTest.RUN_ALL_TESTS();
	}
}
