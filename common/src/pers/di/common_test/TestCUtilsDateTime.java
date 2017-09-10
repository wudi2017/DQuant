package pers.di.common_test;

import java.util.Date;

import pers.di.common.CLog;
import pers.di.common.CUtilsDateTime;

public class TestCUtilsDateTime {
	
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
	public static void main(String[] args) {
		CLog.output("TEST", "TestCUtilsDateTime begin\n");
		
		long lB = CUtilsDateTime.GetCurrentTimeMillis();
		
		String teststr = "";
		for(int i=0; i<10000; i++)
		{
			teststr = CUtilsDateTime.GetCurDateStr();
		}
		
		long lE = CUtilsDateTime.GetCurrentTimeMillis();
		CLog.output("TEST", "cost %d %s\n", lE-lB, teststr);
		
		CLog.output("TEST", "GetCurTimeStrHM %s\n" , CUtilsDateTime.GetCurTimeStrHM());
	
		CLog.output("TEST", "TestCUtilsDateTime end\n");
	}
}
