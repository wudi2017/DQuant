package pers.di.common_test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import pers.di.common.*;
import pers.di.common.CTest.test;

public class TestCUtilsDateTime {

	@test
	public void test_GetCurrentTimeMillis() {
		CTest.TEST_PERFORMANCE_BEGIN();
		long testdata = 0;
		long test_cnt = 10000 * 100;
		for(int i=0; i<test_cnt; i++)
		{
			testdata = testdata + CUtilsDateTime.GetCurrentTimeMillis();
		}
		CTest.EXPECT_TRUE(CTest.TEST_PERFORMANCE_END() < 50);
		CLog.output("TEST", "dump:%d", testdata);
	}
	
	@test
	public void test_GetCurDate() {
		CTest.TEST_PERFORMANCE_BEGIN();
		long test_cnt = 10000 * 100;
		Date cDate = null;
		for(int i=0; i<test_cnt; i++)
		{
			cDate = CUtilsDateTime.GetCurDate();
		}
		CTest.EXPECT_TRUE(CTest.TEST_PERFORMANCE_END() < 50);
		CLog.output("TEST", "dump:%s" , cDate.toString());
	}
	
	@test
	public static void test_GetCurDateTimeStr()
	{
		CTest.TEST_PERFORMANCE_BEGIN();
		String stestdata = "";
		long test_cnt = 10000*100;
		for(int i=0; i<test_cnt; i++)
		{
			stestdata = CUtilsDateTime.GetCurDateTimeStr();
			//CThread.sleep(1000);
			//CLog.output("TEST", "dump:%s \n", stestdata);
		}
		CTest.EXPECT_TRUE(CTest.TEST_PERFORMANCE_END() < 50);
		CLog.output("TEST", "dump:%s", stestdata);
	}
	
	@test
	public static void test_GetCurDateStr()
	{
		CTest.TEST_PERFORMANCE_BEGIN();
		String stestdata = "";
		long test_cnt = 10000*100;
		for(int i=0; i<test_cnt; i++)
		{
			stestdata = CUtilsDateTime.GetCurDateStr();
		}
		CTest.EXPECT_TRUE(CTest.TEST_PERFORMANCE_END() < 50);
		CLog.output("TEST", "dump[%s]", stestdata);
	}
	
	@test
	public static void test_GetCurTimeStr()
	{
		CTest.TEST_PERFORMANCE_BEGIN();
		String stestdata = "";
		long test_cnt = 10000*100;
		for(int i=0; i<test_cnt; i++)
		{
			stestdata = CUtilsDateTime.GetCurTimeStr();
		}
		CTest.EXPECT_TRUE(CTest.TEST_PERFORMANCE_END() < 50);
		CLog.output("TEST", "dump[%s]", stestdata);
	}
	
	@test
	public static void test_getDateStrForSpecifiedDateOffsetD()
	{
		CTest.TEST_PERFORMANCE_BEGIN();
		String stestdata = "";
		long test_cnt = 10000*100;
		for(int i=0; i<test_cnt; i++)
		{
			stestdata = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-01-31", 2);
		}
		CTest.EXPECT_TRUE(CTest.TEST_PERFORMANCE_END() < 2000);
		CTest.EXPECT_STR_EQ(stestdata, stestdata);
		
		// +
		stestdata = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-01-15", 6);
		CTest.EXPECT_STR_EQ("2016-01-21", stestdata);
		
		stestdata = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-08-15", 6);
		CTest.EXPECT_STR_EQ("2016-08-21", stestdata);
		
		stestdata = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-01-25", 6);
		CTest.EXPECT_STR_EQ("2016-01-31", stestdata);
		
		stestdata = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD("1999-02-28", 1);
		CTest.EXPECT_STR_EQ("1999-03-01", stestdata);
		
		String baseDate = "1999-01-01";
		for(int iTest=0; iTest<1000; iTest++)
		{
			String testdateExpect = getDateStrForSpecifiedDateOffsetD_OK(baseDate, iTest);
			String testdate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(baseDate, iTest);
			
			CTest.EXPECT_STR_EQ(testdateExpect, testdate);
		}
		
		// -
		stestdata = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-01-15", -6);
		CTest.EXPECT_STR_EQ("2016-01-09", stestdata);
		
		stestdata = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-08-15", -6);
		CTest.EXPECT_STR_EQ("2016-08-09", stestdata);
		
		stestdata = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-01-05", -6);
		CTest.EXPECT_STR_EQ("2015-12-30", stestdata);
		
		stestdata = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD("1999-03-01", -1);
		CTest.EXPECT_STR_EQ("1999-02-28", stestdata);
		
		baseDate = "2030-01-01";
		for(int iTest=0; iTest<5000; iTest++)
		{
			String testdateExpect = getDateStrForSpecifiedDateOffsetD_OK(baseDate, -iTest);
			String testdate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD(baseDate, -iTest);
			
			CTest.EXPECT_STR_EQ(testdateExpect, testdate);
		}
		
	}
	
	private static String getDateStrForSpecifiedDateOffsetD_OK(String dateStr, int add)
	{
	      Calendar c = Calendar.getInstance();  
	      Date date = null;  
	      try {  
	          date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);  
	      } catch (Exception e) {  
	          e.printStackTrace();  
	      }  
	      c.setTime(date);  
	      int day = c.get(Calendar.DATE);  
	      c.set(Calendar.DATE, day + add);  
	
	      String dayNew = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());  
	      return dayNew;  
	}
	
	@test
	public static void test_getTimeStrForSpecifiedTimeOffsetS()
	{
		CTest.TEST_PERFORMANCE_BEGIN();
		String stestdata = "";
		long test_cnt = 10000*100;
		for(int i=0; i<test_cnt; i++)
		{
			stestdata = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS("12:33:05", 3600*2+60*40+60);
			//String afterTimeStr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM("12:22:34", 2);
		}
		CTest.EXPECT_TRUE(stestdata.equals("15:14:05"));
		CTest.EXPECT_TRUE(CTest.TEST_PERFORMANCE_END() < 200);
		CLog.output("TEST", "dump[%s]", stestdata);
	}
	
	@test
	public static void test_GetSecondFromTimeStr()
	{
		CTest.TEST_PERFORMANCE_BEGIN();
		int itestdata = 0;
		long test_cnt = 10000*100;
		for(int i=0; i<test_cnt; i++)
		{
			itestdata = CUtilsDateTime.GetSecondFromTimeStr("12:34:56");
		}
		CTest.EXPECT_TRUE(CTest.TEST_PERFORMANCE_END() < 50);
		CLog.output("TEST", "dump[%d]", itestdata);
	}
	
	@test
	public static void test_GetTimeStrFromSecond()
	{
		CTest.TEST_PERFORMANCE_BEGIN();
		String stestdata = "'";
		long test_cnt = 10000*100;
		for(int i=0; i<test_cnt; i++)
		{
			stestdata = CUtilsDateTime.GetTimeStrFromSecond(25*3600+61*60+72);
		}
		CTest.EXPECT_TRUE(stestdata.equals("02:02:12"));
		CTest.EXPECT_TRUE(CTest.TEST_PERFORMANCE_END() < 200);
		CLog.output("TEST", "dump[%s]", stestdata);
	}
	
	@test
	public static void test_waitDateTime()
	{
		String curDateStr = CUtilsDateTime.GetCurDateStr();
		String curTimeStr = CUtilsDateTime.GetCurTimeStr();
		String beforeTimeStr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(curTimeStr, -2);
		String afterTimeStr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(curTimeStr, 1);
		CLog.output("TEST", "curDateStr %s beforeTimeStr = %s", curDateStr, beforeTimeStr);
		CLog.output("TEST", "curDateStr %s afterTimeStr = %s", curDateStr, afterTimeStr);
		boolean bwaitbefore = CUtilsDateTime.waitDateTime(curDateStr, beforeTimeStr);
		CLog.output("TEST", "waitDateTime beforeTimeStr = %b", bwaitbefore);
		boolean bwaitafter = CUtilsDateTime.waitDateTime(curDateStr, afterTimeStr);
		CLog.output("TEST", "waitDateTime bwaitafter = %b", bwaitafter);
	}
	
	@CTest.test
	public static void test_subTime()
	{
		CTest.TEST_PERFORMANCE_BEGIN();
		String stestdata = "'";
		long test_cnt = 10000*100;
		String time1 = "15:22:05";
		String time2 = "13:20:21";
		long diffSec = 0;
		for(int i=0; i<test_cnt; i++)
		{
			diffSec = CUtilsDateTime.subTime(time1,time2);
			
		}
		CTest.EXPECT_TRUE(CTest.TEST_PERFORMANCE_END() < 30);
		CTest.EXPECT_LONG_EQ(diffSec, subTime_OK(time1, time2));
	}
	private static long subTime_OK(String time1, String time2)
	{
    	long diffsec = 0;
		try {
			Date date1 = new SimpleDateFormat("HH:mm:ss").parse(time1);
			Date date2 = new SimpleDateFormat("HH:mm:ss").parse(time2);  
	        long diff = date1.getTime() - date2.getTime();
	        diffsec = diff / 1000; 
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		} 
		return diffsec;
	}
	
	public static void main(String[] args) {
		
		CSystem.start();
		
		CTest.ADD_TEST(TestCUtilsDateTime.class);
		CTest.RUN_ALL_TESTS("");
	
		CSystem.stop();
	}
}
