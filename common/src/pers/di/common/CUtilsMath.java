package pers.di.common;

import java.math.BigDecimal;
import java.util.Random;

public class CUtilsMath {
	// 保留2位小数，非四舍五入
	public static float saveNDecimalIgnore(float val, int n)
	{
		float newVal = 0.0f;
		int iScale = (int) Math.pow(10,n);
		newVal = (int)(val*iScale)/(float)iScale;
		return newVal;
	}
	
	// 保留N位小数，四舍五入
	public static double saveNDecimal(double val, int n)
	{
		double newVal = 0.0f;
		int iScale = (int) Math.pow(10,n);
		newVal= Math.round(val*iScale)/(double)iScale;
		return newVal;
	}
	public static float saveNDecimal(float val, int n)
	{
		float newVal = 0.0f;
		int iScale = (int) Math.pow(10,n);
		newVal= Math.round(val*iScale)/(float)iScale;
		return newVal;
	}
	
//	// multiply float*float->double
//	public static double multiply(float f1, float f2)
//	{
//		BigDecimal bd_f1 = new BigDecimal(Float.toString(f1));
//		BigDecimal bd_f2 = new BigDecimal(Float.toString(f2));
//		return bd_f1.multiply(bd_f2).doubleValue();
//	}
//	
//	// multiply float+float->double
//	public static double add(float f1, float f2)
//	{
//		BigDecimal bd_f1 = new BigDecimal(Float.toString(f1));
//		BigDecimal bd_f2 = new BigDecimal(Float.toString(f2));
//		return bd_f1.add(bd_f2).doubleValue();
//	}
	
	// float to double
	public static double toDouble(float f1)
	{
		BigDecimal bd_f1 = new BigDecimal(Float.toString(f1));
		return bd_f1.doubleValue();
	}
	
	// 获取0.0-1.0之间的float随机
	public static float randomFloat()
	{
		return s_random.nextFloat();
	}
	
	private static Random s_random = new Random(CUtilsDateTime.GetCurrentTimeMillis());
}
