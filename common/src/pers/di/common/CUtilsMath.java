package pers.di.common;

import java.math.BigDecimal;
import java.util.Random;

public class CUtilsMath {
	// ����NλС��������������
	public static double saveNDecimalIgnore(double val, int n)
	{
		double newVal = 0.0;
		int iScale = (int) Math.pow(10,n);
		newVal = (int)(val*iScale)/(double)iScale;
		return newVal;
	}
	
	// ����NλС������������
	public static double saveNDecimal(double val, int n)
	{
		double newVal = 0.0;
		int iScale = (int) Math.pow(10,n);
		newVal= Math.round(val*iScale)/(double)iScale;
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
	
//	// float to double
//	public static double toDouble(float f1)
//	{
//		BigDecimal bd_f1 = new BigDecimal(Float.toString(f1));
//		return bd_f1.doubleValue();
//	}
	
	// ��ȡ0.0-1.0֮���float���
	public static float randomFloat()
	{
		return s_random.nextFloat();
	}
	
	// d��i�η���
	public static double sqrt(double d, double i) {  
       i=1/i;  
       return Math.pow(d, i);  
	}  
	private static Random s_random = new Random(CUtilsDateTime.GetCurrentTimeMillis());
}
