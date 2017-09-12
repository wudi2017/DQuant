package pers.di.common;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.*;

public abstract class CTest {
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public @interface test {
	}
	
	public static long CURRENT_COSTTIME()
	{
		long TCE = System.currentTimeMillis();
		return TCE-s_curTestInnnerTCB;
	}
	public static void EXPECT_TRUE(boolean bResult)
	{
		if(!bResult) s_curTestInnnerErrorCount++;
	}
	
	public static int ADD_TEST(Class<?> cls)
	{
		try {
			s_clsList.add(cls);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public static int RUN_ALLTEST()
	{
		outputProcess("RUN_ALLTEST BEGIN");
		
		for(int iCls = 0; iCls<s_clsList.size(); iCls++)
		{
			Class<?> testCls = s_clsList.get(iCls);
			String clsName = testCls.getName();
			String clsSimpleName = testCls.getSimpleName();
			//outputProcess("TestClass: %s", clsSimpleName);
			
			try {
				
				Object obj = testCls.newInstance();
				Method[] methods = testCls.getDeclaredMethods();
				
				for(int iMethod = 0; iMethod < methods.length; iMethod++)
				{
					Method method = methods[iMethod];
					String methodName = method.getName();
					//outputProcess("    method: %s", methodName);
					
					boolean bCallTest = false;
					Annotation[] allAnnotations = method.getAnnotations();
					for(Annotation an : allAnnotations)
					{
				        Class annotationType = an.annotationType();
				        //outputProcess("        annotationType: %s", annotationType.getName());
				        
				        if(annotationType.getName().equals(test.class.getName()))
				        {
				        	bCallTest = true;
				        }
					}

					// call test
					if(bCallTest)
					{
						outputProcess("[CTEST] %s.%s", clsSimpleName, methodName);
						s_curTestInnnerErrorCount = 0;
						s_curTestInnnerTCB = System.currentTimeMillis();
						method.invoke(obj);
						long TCE = System.currentTimeMillis();
						if(0 == s_curTestInnnerErrorCount)
						{
							outputProcess("[CTEST] %s.%s [%dms][OK]", clsSimpleName, methodName, TCE-s_curTestInnnerTCB);
						}
						else
						{
							outputProcess("[CTEST] %s.%s [%dms][NG]", clsSimpleName, methodName, TCE-s_curTestInnnerTCB);
							s_testErrorCount++;
						}
					}
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		outputProcess("RUN_ALLTEST END");
		outputProcess("error(%d)", s_testErrorCount);
		return 0;
	}
	
	public static int s_curTestInnnerErrorCount = 0;
	public static long s_curTestInnnerTCB = 0;
	
	public static int s_testErrorCount = 0;
	
	public static List<Class<?>> s_clsList = new ArrayList<Class<?>>();
	
	/**
	 * ****************************************************************************************
	 * LOG
	 */
	
	private static void outputProcess(String format, Object... args)
	{
		String logstr = String.format(format, args);
		s_fmt.format("%s\n", logstr);
	}
	private static Formatter s_fmt = new Formatter(System.out);
}
