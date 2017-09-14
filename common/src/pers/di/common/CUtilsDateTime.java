package pers.di.common;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.*;

import com.google.protobuf.TextFormat.ParseException;

public class CUtilsDateTime {
	/*
	 *  当前时间戳
	 */
	static public long GetCurrentTimeMillis()
	{
		return System.currentTimeMillis();
	}
	
	/*
	 *  当前日期对象
	 *  class Date
	 */
	static public Date GetCurDate()
	{
		return s_cDateTimeProvider.curDate();
		// return GetDateTimeStr(s_cDateTimeProvider.curDate());
	}
	
	/*
	 *  当前日期时间字符串
	 *  XXXX-XX-XX XX:XX:XX
	 */
	static public String GetCurDateTimeStr()
	{
		return s_cDateTimeProvider.curDateTime();
		// return GetDateTimeStr(new Date());
	}
	
	/*
	 *  当前日期
	 *  XXXX-XX-XX
	 */
	static public String GetCurDateStr()
	{
		return GetCurDateTimeStr().substring(0, 10);
	}
	
	/*
	 *  当前时间
	 *  XX:XX:XX
	 */
	static public String GetCurTimeStr()
	{
		return GetCurDateTimeStr().substring(11, 19);
	}
	
//	/*
//	 *  转换日期对象Date到字符串
//	 *  返回  "yyyy-MM-dd"
//	 */
//	static public String GetDateStr(Date cDate)
//	{
//		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
//		return sdf.format(cDate);
//	}
	
//	/*
//	 *  转换日期对象Date到字符串
//	 *  返回  "HH:mm:ss"
//	 */
//	static public String GetTimeStr(Date cDate)
//	{
//		SimpleDateFormat sdf =new SimpleDateFormat("HH:mm:ss");
//		return sdf.format(cDate);
//	}
	
//	/*
//	 *  转换日期对象Date到字符串
//	 *  返回  "HH:mm"
//	 */
//	static public String GetTimeStrHM(Date cDate)
//	{
//		SimpleDateFormat sdf =new SimpleDateFormat("HH:mm");
//		return sdf.format(cDate);
//	}
	
//	/*
//	 *  转换日期对象Date到字符串
//	 *  返回  "yyyy-MM-dd HH:mm:ss"
//	 */
//	static public String GetDateTimeStr(Date cDate)
//	{
//		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		return sdf.format(cDate);
//	}
	
//	/*
//	 * 转换日期字符串到对象 Date
//	 * 输入日期字符串可以为 "yyyy-MM-dd"
//	 * 输入日期字符串可以为 "yyyy-MM-dd HH:mm:ss"
//	 */
//	static public Date GetDate(String dateStr)
//	{
//		SimpleDateFormat sdf = null;
//		if(dateStr.length() == "yyyy-MM-dd".length())
//		{
//			sdf =new SimpleDateFormat("yyyy-MM-dd");
//		}
//		else
//		{
//			sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		}
//		Date cDate = null;
//		try
//		{
//			cDate = sdf.parse(dateStr);  
//		}
//		catch (Exception e)  
//		{  
//			CLog.output("TIME", e.getMessage());  
//		}  
//		return cDate;
//	}
	
	/*
     * 获得指定日期偏移后的日期字符串
     * 例如传入 "2016-01-06", 4 则返回  "2016-01-10"
     */  
    public static String getDateStrForSpecifiedDateOffsetD(String specifiedDate, int offset_d) {
//        Calendar c = Calendar.getInstance();  
//        Date date = null;  
//        try {  
//            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDate);  
//        } catch (Exception e) {  
//            e.printStackTrace();  
//        }  
//        c.setTime(date);  
//        int day = c.get(Calendar.DATE);  
//        c.set(Calendar.DATE, day + offset_d);  
//  
//        String dayNew = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());  
//        return dayNew;  
    	
    	// convert to number
    	
    	int iC0 = (int)specifiedDate.charAt(0) - 48;
    	int iC1 = (int)specifiedDate.charAt(1) - 48;
    	int iC2 = (int)specifiedDate.charAt(2) - 48;
    	int iC3 = (int)specifiedDate.charAt(3) - 48;
    	int year = 1000*iC0+ 100*iC1 + 10*iC2 + iC3;
    	
    	int iC5 = (int)specifiedDate.charAt(5) - 48;
    	int iC6 = (int)specifiedDate.charAt(6) - 48;
    	int month = 10*iC5 + iC6;
    	
    	int iC8 = (int)specifiedDate.charAt(8) - 48;
    	int iC9 = (int)specifiedDate.charAt(9) - 48;
    	int day = 10*iC8 + iC9;
    	
    	int iNewDay = day;
    	int iNewMonth = month;
    	int iNewYear = year;
    	
    	if(offset_d > 0)
    	{
    		for(int i = 0; i < offset_d; i++)
        	{
        		// calc DMonthMax
        		int DMonthMax_current = 30;
            	boolean runYear = false;
            	if((iNewYear%4 == 0 && iNewYear%100 != 0)
            		|| iNewYear%400 == 0)
            	{
            		runYear = true;
            	}
            	if(1 == iNewMonth) DMonthMax_current=31;
            	else if(2 == iNewMonth) {
            		DMonthMax_current=28;
            		if(runYear) DMonthMax_current=29;
            	}
            	else if(3 == iNewMonth) DMonthMax_current=31;
            	else if(4 == iNewMonth) DMonthMax_current=30;
            	else if(5 == iNewMonth) DMonthMax_current=31;
            	else if(6 == iNewMonth) DMonthMax_current=30;
            	else if(7 == iNewMonth) DMonthMax_current=31;
            	else if(8 == iNewMonth) DMonthMax_current=31;
            	else if(9 == iNewMonth) DMonthMax_current=30;
            	else if(10 == iNewMonth) DMonthMax_current=31;
            	else if(11 == iNewMonth) DMonthMax_current=30;
            	else if(12 == iNewMonth) DMonthMax_current=31;
            	
            	iNewDay = iNewDay + 1;
            	boolean bJinWeiMonth = iNewDay>DMonthMax_current?true:false;
            	if(bJinWeiMonth)
            	{
            		iNewDay = 1;
            		iNewMonth = iNewMonth + 1;
            		
            		boolean bJinWeiYear = iNewMonth>12?true:false;
            		if(bJinWeiYear)
            		{
            			iNewMonth = 1;
            			iNewYear = iNewYear + 1;
            		}
            	}
        	}
    	}
    	else
    	{
    		for(int i = 0; i < -offset_d; i++)
        	{
        		// calc DMonthMax
        		int DMonthMax_before = 30;
        		
            	boolean runYear = false;
            	if(iNewMonth == 1 && iNewDay == 1)
            	{
            		if(((iNewYear-1)%4 == 0 && (iNewYear-1)%100 != 0)
                    		|| (iNewYear-1)%400 == 0)
                	{
                		runYear = true;
                	}
            	}
            	else
            	{
            		if((iNewYear%4 == 0 && iNewYear%100 != 0)
                    		|| iNewYear%400 == 0)
                	{
                		runYear = true;
                	}
            	}
            	int beforeMonth = iNewMonth-1;
            	beforeMonth = beforeMonth==0?12:beforeMonth;
            	
            	if(1 == beforeMonth) DMonthMax_before=31;
            	else if(2 == beforeMonth) {
            		DMonthMax_before=28;
            		if(runYear) DMonthMax_before=29;
            	}
            	else if(3 == beforeMonth) DMonthMax_before=31;
            	else if(4 == beforeMonth) DMonthMax_before=30;
            	else if(5 == beforeMonth) DMonthMax_before=31;
            	else if(6 == beforeMonth) DMonthMax_before=30;
            	else if(7 == beforeMonth) DMonthMax_before=31;
            	else if(8 == beforeMonth) DMonthMax_before=31;
            	else if(9 == beforeMonth) DMonthMax_before=30;
            	else if(10 == beforeMonth) DMonthMax_before=31;
            	else if(11 == beforeMonth) DMonthMax_before=30;
            	else if(12 == beforeMonth) DMonthMax_before=31;
            	
            	iNewDay = iNewDay - 1;
            	boolean bJianweiMonth = iNewDay==0?true:false;
            	if(bJianweiMonth)
            	{
            		iNewDay = DMonthMax_before;
            		
            		iNewMonth = iNewMonth - 1;
            		
            		boolean bJianweiYear = iNewMonth==0?true:false;
            		if(bJianweiYear)
            		{
            			iNewMonth = 12;
            			iNewYear = iNewYear - 1;
            		}
            	}
        	}
    	}
    	
    	// convert to new string
    	
    	StringBuilder strBuilder = new StringBuilder(specifiedDate);
    	
    	strBuilder.setCharAt(0, (char)('0' + iNewYear%10000/1000));
    	strBuilder.setCharAt(1, (char)('0' + iNewYear%1000/100));
    	strBuilder.setCharAt(2, (char)('0' + iNewYear%100/10));
    	strBuilder.setCharAt(3, (char)('0' + iNewYear%10));
    	//strBuilder.setCharAt(4, '-');
    	strBuilder.setCharAt(5, (char)('0' + iNewMonth/10));
    	strBuilder.setCharAt(6, (char)('0' + iNewMonth%10));
    	//strBuilder.setCharAt(7, '-');
    	strBuilder.setCharAt(8, (char)('0' + iNewDay/10));
    	strBuilder.setCharAt(9, (char)('0' + iNewDay%10));
    	
    	String newDate = strBuilder.toString();
    	return newDate;

    } 
    
	/*
     * 获得指定时间偏移若干秒后的时间字符串
     * 例如传入 "12:33:05", 65 则返回  "12:34:10"
     */  
    public static String getTimeStrForSpecifiedTimeOffsetS(String specifiedTime, int offset_s) {
//        Calendar c = Calendar.getInstance();  
//        Date date = null;  
//        try {  
//            date = new SimpleDateFormat("HH:mm:ss").parse(specifiedTime);  
//        } catch (Exception e) {  
//            e.printStackTrace();  
//        }  
//        c.setTime(date);  
//        int second = c.get(Calendar.SECOND);  
//        c.set(Calendar.SECOND, second + offset_s);  
//  
//        String timeNew = new SimpleDateFormat("HH:mm:ss").format(c.getTime());  
    	int second = GetSecondFromTimeStr(specifiedTime);
    	int newSecond = second + offset_s;
    	if(newSecond < 0)
    		newSecond = 0;
        return GetTimeStrFromSecond(newSecond);  
    } 
    
    /*
     * 获取时间秒数
     * 01:00:01 秒数为 3601
     */
    public static int GetSecondFromTimeStr(String time)
    {
		int iSec = 0;
		{
			char c1 = time.charAt(0);
			int iC1 = (int)c1 - 48;
			char c2 = time.charAt(1);
			int iC2 = (int)c2 - 48;
			iSec = iSec + (iC1*10+iC2)*3600;
		}
		{
			char c1 = time.charAt(3);
			int iC1 = (int)c1 - 48;
			char c2 = time.charAt(4);
			int iC2 = (int)c2 - 48;
			iSec = iSec + (iC1*10+iC2)*60;
		}
		{
			char c1 = time.charAt(6);
			int iC1 = (int)c1 - 48;
			char c2 = time.charAt(7);
			int iC2 = (int)c2 - 48;
			iSec = iSec + (iC1*10+iC2);
		}
        return iSec;
    }
    
    /*
     * 获取秒数时间
     *  3601 的时间为 01:00:01
     */
    public static String GetTimeStrFromSecond(int second)
    {
    	int HH = second/3600%24;
		int MM = second%3600/60;
		int SS = second%3600%60;

		StringBuilder strBuilder = new StringBuilder();
		strBuilder.setLength(8);
		
		strBuilder.setCharAt(0, (char)('0' + HH/10));
		strBuilder.setCharAt(1, (char)('0' + HH%10));
		strBuilder.setCharAt(2, ':');
		
		strBuilder.setCharAt(3, (char)('0' + MM/10));
		strBuilder.setCharAt(4, (char)('0' + MM%10));
		strBuilder.setCharAt(5, ':');
		
		strBuilder.setCharAt(6, (char)('0' + SS/10));
		strBuilder.setCharAt(7, (char)('0' + SS%10));
		
		String time = strBuilder.toString();
        return time;
    }
    
    /*
     * 等待日期时间
     * 等待到时间后返回true
     * 调用时已经超时返回false
     */
    public static boolean waitDateTime(String date, String time)
    {
    	String waitDateTimeStr = date + " " + time;
    	
    	{
    		String curDateTimeStr = CUtilsDateTime.GetCurDateTimeStr();
    		if(curDateTimeStr.compareTo(waitDateTimeStr) > 0) 
    			return false;
    	}
    	
    	while(true)
    	{
    		String curDateTimeStr = CUtilsDateTime.GetCurDateTimeStr();
    		
    		if(curDateTimeStr.compareTo(waitDateTimeStr) > 0) 
    		{
    			return true;
    		}
  
    		try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }
    
    /*
     * 时间差（秒）
     */
    public static long subTime(String time1, String time2)
    {  
//    	long diffsec = 0;
//		try {
//			Date date1 = new SimpleDateFormat("HH:mm:ss").parse(time1);
//			Date date2 = new SimpleDateFormat("HH:mm:ss").parse(time2);  
//	        long diff = date1.getTime() - date2.getTime();
//	        diffsec = diff / 1000; 
//		} catch (java.text.ParseException e) {
//			e.printStackTrace();
//		} 
        return GetSecondFromTimeStr(time1) - GetSecondFromTimeStr(time2);
    }
    
    public static void start()
    {
    	s_cDateTimeProvider.start();
    }
    
    public static void stop()
    {
    	s_cDateTimeProvider.stop();
    }
    
    /*
     * -----------------------------------------------------------------------------------------------
     */
    
	public static class DateTimeProvider extends CThread
	{
		public DateTimeProvider()
		{
			c_startFlag = false;
			c_curDate = null;
			c_syncObj = new CSyncObj();
		}
		
		public void start()
		{
			c_syncObj.Lock();
			c_curDate = getSyncRealDate();
			c_curDateTimeStr = c_sdf.format(c_curDate);
			c_syncObj.UnLock();
			c_lLastSystemSyncTC = System.currentTimeMillis();
			c_lLastLocalProgSyncTC = c_lLastSystemSyncTC;
			
			this.startThread();
		}
		
		public void stop()
		{
			this.stopThread();
		}
		
		public Date curDate()
		{
			Date curDate = null;
			if(c_startFlag)
			{
				c_syncObj.Lock();
				curDate = c_curDate;
				c_syncObj.UnLock();
			}
			else
			{
				
				curDate = new Date();
			}
			return curDate;
		}
		
		public String curDateTime()
		{
			String curDateTimeStr = "0000-00-00 00:00:00";
			if(c_startFlag)
			{
				//c_syncObj.Lock();
				curDateTimeStr = c_curDateTimeStr;
				//c_syncObj.UnLock();
			}
			else
			{
				curDateTimeStr = c_sdf.format(new Date());
			}
			return curDateTimeStr;
		}
		
		@Override
		public void run() {
			
			c_startFlag = true;
			
			while(!this.checkQuit())
			{
				CThread.msleep(200);
				long curTC = System.currentTimeMillis();
				if(curTC - c_lLastSystemSyncTC >= 1000*30)
				{
					// 每30s，sync时间一次
					c_syncObj.Lock();
					c_curDate = getSyncRealDate();
					c_curDateTimeStr = c_sdf.format(c_curDate);
					c_syncObj.UnLock();
					c_lLastSystemSyncTC = curTC;
					
				}
				if(curTC - c_lLastLocalProgSyncTC >= 1000)
				{
					// 每秒，本地更新时间
					Calendar c = Calendar.getInstance();  
					c_syncObj.Lock();
			        c.setTime(c_curDate);  
			        int second = c.get(Calendar.SECOND);  
			        c.set(Calendar.SECOND, second + 1);  
			        c_curDate = c.getTime();
			        c_curDateTimeStr = c_sdf.format(c_curDate);
			        c_syncObj.UnLock();
			        c_lLastLocalProgSyncTC = curTC;
			        
					//System.out.println("DateTimeProvider: Local sync:" + CUtilsDateTime.GetDateTimeStr(c_curDate)); 
				}
			}
		}
		
		private Date getSyncRealDate()
		{
			Date curDate = getWebsiteDatetime();
			if(null != curDate)
			{
				//System.out.println("DateTimeProvider: web sync:" + CUtilsDateTime.GetDateTimeStr(curDate)); 
			}
			else
			{
				curDate = new Date();
				//System.out.println("DateTimeProvider: system sync:" + CUtilsDateTime.GetDateTimeStr(curDate));
			}
			return curDate;
		}
		private Date getWebsiteDatetime(){
	        try {
	        	Date date = null;
	        	
	        	for(int i=0; i<5 ; i++)
	        	{
		            URL url = new URL("http://www.baidu.com");// 取得资源对象
		            URLConnection uc = url.openConnection();// 生成连接对象
		            uc.connect();// 发出连接
		            long ld = uc.getDate();// 读取网站日期时间
		            if(ld > 1505239488000L)
		            {
		            	date = new Date(ld);
		            	break;
		            }
		            CThread.usleep(1000*100);
	        	}

	            //System.out.println("DateTimeProvider: getWebsiteDatetime...");
	            return date;
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
		
		private boolean c_startFlag;
		
		private Date c_curDate;
		private String c_curDateTimeStr;
		private SimpleDateFormat c_sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		private long c_lLastSystemSyncTC;
		private long c_lLastLocalProgSyncTC;
		
		private CSyncObj c_syncObj;
	}
	
	private static DateTimeProvider s_cDateTimeProvider = new DateTimeProvider();
}
