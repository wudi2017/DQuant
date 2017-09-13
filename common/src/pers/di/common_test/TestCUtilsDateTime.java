package pers.di.common_test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
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
		CLog.output("X", "dump:%d\n", testdata);
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
		CLog.output("TEST", "dump:%s\n" , cDate.toString());
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
		CLog.output("x", "dump:%s \n", stestdata);
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
		CLog.output("x", "dump[%s] \n", stestdata);
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
		CLog.output("x", "dump[%s] \n", stestdata);
	}
	
	@test
	public static void test_getDateStrForSpecifiedDateOffsetD()
	{
		CTest.TEST_PERFORMANCE_BEGIN();
		String stestdata = "";
		long test_cnt = 10000*10;
		for(int i=0; i<test_cnt; i++)
		{
			stestdata = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-01-31", 2);
		}
		CTest.EXPECT_TRUE(CTest.TEST_PERFORMANCE_END() < 1000);
		CLog.output("x", "dump[%s] \n", stestdata);
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
		CLog.output("TEST", "dump[%s] \n", stestdata);
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
		CLog.output("x", "dump[%d] \n", itestdata);
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
		CLog.output("TEST", "dump[%s] \n", stestdata);
	}
	
	@test
	public static void test_waitDateTime()
	{
		String curDateStr = CUtilsDateTime.GetCurDateStr();
		String curTimeStr = CUtilsDateTime.GetCurTimeStr();
		String beforeTimeStr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(curTimeStr, -2);
		String afterTimeStr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetS(curTimeStr, 1);
		CLog.output("X", "curDateStr %s beforeTimeStr = %s\n", curDateStr, beforeTimeStr);
		CLog.output("X", "curDateStr %s afterTimeStr = %s\n", curDateStr, afterTimeStr);
		boolean bwaitbefore = CUtilsDateTime.waitDateTime(curDateStr, beforeTimeStr);
		CLog.output("X", "waitDateTime beforeTimeStr = %b\n", bwaitbefore);
		boolean bwaitafter = CUtilsDateTime.waitDateTime(curDateStr, afterTimeStr);
		CLog.output("X", "waitDateTime bwaitafter = %b\n", bwaitafter);
	}
	
	public static void test_all()
	{
		String time1 = "12:22:23";
		String time2 = "13:20:21";
		long diffSec = CUtilsDateTime.subTime(time1,time2);
		CLog.output("TEST", "time1(%s) - time2(%s) = %d s\n", time1,time2,diffSec);
		
		String date1 = "2016-01-01";
		String date2 = "0000-01-01";
		long diffDay = CUtilsDateTime.subDate(date1, date2);
		CLog.output("TEST", "date1(%s) - date2(%s) = %d day\n", date1,date2,diffDay);
	}
	
	public static void main(String[] args) {
		CSystem.start();
		
		CTest.ADD_TEST(TestCUtilsDateTime.class);
		CTest.RUN_ALL_TESTS("TestCUtilsDateTime.test_getTimeStrForSpecifiedTimeOffsetS");
	
		CSystem.stop();
	}
}
