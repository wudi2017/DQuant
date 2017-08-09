package pers.di.common_test;

import pers.di.common.CLog;
import pers.di.common.CUtilsMath;

public class TestCUtilsMath {
	public static void main(String[] args) {
		CLog.output("TEST", "TestBUtilsDateTime begin\n");
		
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
		

		CLog.output("TEST", "TestBUtilsDateTime end\n");
	}
}
