package pers.di.common;

import java.lang.reflect.Method;
import java.util.Formatter;

import org.json.JSONObject;

public abstract class CTest {
	
	public CTest(String module, String function)
	{
		m_module = module;
		m_function = function;
		m_errCount = 0;
	}
	
	public abstract void TESTCASE();
	
	public void EXPECT_TRUE(boolean bResult)
	{
		if(!bResult) m_errCount++;
	}
	
	public long CURRENT_COST()
	{
		long CTC = System.currentTimeMillis();
		return CTC - m_TCB;
	}

	public int start()
	{
		outputProcess("[CTEST] %s.%s", m_module, m_function);
		m_TCB = System.currentTimeMillis();
		TESTCASE();
		long TCE = System.currentTimeMillis();
		if(0 == m_errCount)
		{
			outputProcess("[CTEST] %s.%s [%dms][OK]", m_module, m_function, TCE-m_TCB);
		}
		else
		{
			outputProcess("[CTEST] %s.%s [%dms][NG]", m_module, m_function, TCE-m_TCB);
		}
		return m_errCount;
	}

	private String m_module;
	private String m_function;
	private long m_TCB;
	private int m_errCount;
	
	/**
	 * ****************************************************************************************
	 */
	
	public static int RUNCTEST(Class<?> cls)
	{
		try {
			Class<?> ctest = Class.forName(CTest.class.getName());
			Method md = ctest.getMethod("start");
			md.invoke(cls.newInstance());
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	private static void outputProcess(String format, Object... args)
	{
		String logstr = String.format(format, args);
		s_fmt.format("%s\n", logstr);
	}
	static private Formatter s_fmt = new Formatter(System.out);
}
