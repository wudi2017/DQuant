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
	
	public void TestCUtilsDateTime()
	{
		CSystem.start();
	}
	
	@test
	public void test_GetCurrentTimeMillis() {
		long testdata = 0;
		long test_cnt = 10000 * 100;
		for(int i=0; i<test_cnt; i++)
		{
			testdata = testdata + CUtilsDateTime.GetCurrentTimeMillis();
		}
		CTest.EXPECT_TRUE(CTest.CURRENT_COSTTIME() < 30);
		CLog.output("X", "dump:%d\n", testdata);
	}
	
	@test
	public void test_GetCurDateStr() {
		
		String stestdata = "";
		long test_cnt = 10000 * 100;
		Date x = null;
		for(int i=0; i<test_cnt; i++)
		{
			 x = CUtilsDateTime.getCurDate();
			 //SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
			// sdf.format(x);
		}
		CTest.EXPECT_TRUE(CTest.CURRENT_COSTTIME() < 50);
		CLog.output("X", "dump:%s\n", stestdata , x.toString());
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
		
		
		String testdate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-01-31", 2);
		CLog.output("TEST", "testdate = %s\n", testdate);
		
		String testtime = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM("12:22:34", 2);
		CLog.output("TEST", "testtime = %s\n", testtime);
		
		String curDateStr = CUtilsDateTime.GetCurDateStr();
		String curTimeStr = CUtilsDateTime.GetCurTimeStr();
		String curDateTimeStr = CUtilsDateTime.GetCurDateTimeStr();
		CLog.output("TEST", "curDate %s curTime %s \n", curDateStr, curTimeStr);
		CLog.output("TEST", "curDateTimeStr %s \n", curDateTimeStr);
		
		String beforeTimeStr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(curTimeStr, -2);
		String afterTimeStr = CUtilsDateTime.getTimeStrForSpecifiedTimeOffsetM(curTimeStr, 2);
		CLog.output("TEST", "curDateStr %s beforeTimeStr = %s\n", curDateStr, beforeTimeStr);
		CLog.output("TEST", "curDateStr %s afterTimeStr = %s\n", curDateStr, afterTimeStr);
		boolean bwaitbefore = CUtilsDateTime.waitDateTime(curDateStr, beforeTimeStr);
		CLog.output("TEST", "waitDateTime beforeTimeStr = %b\n", bwaitbefore);
		boolean bwaitafter = CUtilsDateTime.waitDateTime(curDateStr, afterTimeStr);
		CLog.output("TEST", "waitDateTime bwaitafter = %b\n", bwaitafter);
	}
	
	public static void test_getDateStrForSpecifiedDateOffsetD()
	{
		String testdate = CUtilsDateTime.getDateStrForSpecifiedDateOffsetD("2016-01-31", 2);
		CLog.output("TEST", "testdate = %s\n", testdate);
	}
	
	public static void test_GetCurDateTimeStr()
	{
		String teststr = "";
		long TCB = CUtilsDateTime.GetCurrentTimeMillis();
		for(int i=0; i<100000; i++)
		{
			teststr = CUtilsDateTime.GetCurDateTimeStr();
			//CLog.output("TEST", "teststr: %s\n", teststr);
			//CThread.sleep(1);
		}
		long TCE = CUtilsDateTime.GetCurrentTimeMillis();
		CLog.output("TEST", "cost: %ds\n", TCE-TCB);
	}
	
	public static void test_GetSecond()
	{
		long Sec = 0;
		long TCB = CUtilsDateTime.GetCurrentTimeMillis();
		for(int i=0; i<1000*1000; i++)
		{
			Sec = Sec + CUtilsDateTime.GetSecond("12:34:56");
			//CLog.output("TEST", "teststr: %s\n", teststr);
			//CThread.sleep(1);
			
		}
		long TCE = CUtilsDateTime.GetCurrentTimeMillis();
		CLog.output("TEST", "cost: %dms\n", TCE-TCB);
		CLog.output("TEST", "dump:%d\n", Sec);
	}
	
	public static void main(String[] args) {

		CTest.ADD_TEST(TestCUtilsDateTime.class);
		CTest.RUN_ALLTEST();
		
		//test_GetCurDateStr();
		
		//test_GetCurDateTimeStr();
		// test_GetSecond();

		//long lB = CUtilsDateTime.GetCurrentTimeMillis();
//		
//		String teststr = "";
//		for(int i=0; i<10000; i++)
//		{
//			teststr = CUtilsDateTime.GetCurDateStr();
//		}
//		
//		long lE = CUtilsDateTime.GetCurrentTimeMillis();
//		CLog.output("TEST", "cost %d %s\n", lE-lB, teststr);
//		
//		CLog.output("TEST", "GetCurTimeStrHM %s\n" , CUtilsDateTime.GetCurTimeStrHM());
	
		CSystem.stop();
	}
}
