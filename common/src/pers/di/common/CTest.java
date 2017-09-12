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
	
	public CTest()
	{
		m_errCount = 0;
	}

	public void EXPECT_TRUE(boolean bResult)
	{
		if(!bResult) m_errCount++;
	}

	private int start()
	{
//		outputProcess("[CTEST] %s.%s", m_module, m_function);
//		
//		Method[] methods = userClass.getDeclaredMethods();
//		
//		
//		Method method;
//	    try {
//	      method = MyQuantTest.class.getMethod("onHandleData",QuantContext.class);
//	      
//	      Annotation[] methodAnnotations = method.getAnnotations();
//
//	      for(Annotation me : methodAnnotations){
//	        Class annotationType =  me.annotationType();
//	        System.out.println("setPwd方法上的注释有: " + annotationType);
//	      }
//	    } catch (SecurityException e) {
//	      e.printStackTrace();
//	    } catch (NoSuchMethodException e) {
//	      e.printStackTrace();
//	    }
//	    
//	    
//		long TCB = System.currentTimeMillis();
//		
//		long TCE = System.currentTimeMillis();
//		if(0 == m_errCount)
//		{
//			outputProcess("[CTEST] %s.%s [%dms][OK]", m_module, m_function, TCE-m_TCB);
//		}
//		else
//		{
//			outputProcess("[CTEST] %s.%s [%dms][NG]", m_module, m_function, TCE-m_TCB);
//		}
		return m_errCount;
	}
	
	private int m_errCount;
	
	/**
	 * ****************************************************************************************
	 * Static
	 */
	
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
		outputProcess("RUN_ALLTEST...");
		
		for(int iCls = 0; iCls<s_clsList.size(); iCls++)
		{
			Class<?> testCls = s_clsList.get(iCls);
			outputProcess("TestClass: %s", testCls.getName());
			
			
			try {
				
				Method[] methods = testCls.getDeclaredMethods();
				
				for(int iMethod = 0; iMethod < methods.length; iMethod++)
				{
					Method method = methods[0];
					outputProcess("    method: %s", method.getName());
					//method.invoke(testCls.newInstance(););
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return 0;
	}
	
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
