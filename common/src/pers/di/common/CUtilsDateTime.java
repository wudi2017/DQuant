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
	 *  当前日期
	 */
	static public String GetCurDateStr()
	{
		return GetDateStr(s_cDateTimeProvider.curDate());
	}
	
	/*
	 *  当前时间
	 *  XX:XX:XX
	 */
	static public String GetCurTimeStr()
	{
		return GetTimeStr(s_cDateTimeProvider.curDate());
	}
	
	/*
	 *  当前时间
	 *  XX:XX
	 */
	static public String GetCurTimeStrHM()
	{
		return GetTimeStrHM(s_cDateTimeProvider.curDate());
	}
	
	/*
	 *  当前日期时间
	 */
	static public String GetCurDateTimeStr()
	{
		return GetDateTimeStr(s_cDateTimeProvider.curDate());
	}
	
	/*
	 *  转换日期对象Date到字符串
	 *  返回  "yyyy-MM-dd"
	 */
	static public String GetDateStr(Date cDate)
	{
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(cDate);
	}
	
	/*
	 *  转换日期对象Date到字符串
	 *  返回  "HH:mm:ss"
	 */
	static public String GetTimeStr(Date cDate)
	{
		SimpleDateFormat sdf =new SimpleDateFormat("HH:mm:ss");
		return sdf.format(cDate);
	}
	
	/*
	 *  转换日期对象Date到字符串
	 *  返回  "HH:mm"
	 */
	static public String GetTimeStrHM(Date cDate)
	{
		SimpleDateFormat sdf =new SimpleDateFormat("HH:mm");
		return sdf.format(cDate);
	}
	
	/*
	 *  转换日期对象Date到字符串
	 *  返回  "yyyy-MM-dd HH:mm:ss"
	 */
	static public String GetDateTimeStr(Date cDate)
	{
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(cDate);
	}
	
	/*
	 * 转换日期字符串到对象 Date
	 * 输入日期字符串可以为 "yyyy-MM-dd"
	 * 输入日期字符串可以为 "yyyy-MM-dd HH:mm:ss"
	 */
	static public Date GetDate(String dateStr)
	{
		SimpleDateFormat sdf = null;
		if(dateStr.length() == "yyyy-MM-dd".length())
		{
			sdf =new SimpleDateFormat("yyyy-MM-dd");
		}
		else
		{
			sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		Date cDate = null;
		try
		{
			cDate = sdf.parse(dateStr);  
		}
		catch (Exception e)  
		{  
			CLog.output("TIME", e.getMessage());  
		}  
		return cDate;
	}
	
	/*
     * 获得指定日期偏移后的日期字符串
     * 例如传入 "2016-01-06", 4 则返回  "2016-01-10"
     */  
    public static String getDateStrForSpecifiedDateOffsetD(String specifiedDate, int offset) {
        Calendar c = Calendar.getInstance();  
        Date date = null;  
        try {  
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDate);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        c.setTime(date);  
        int day = c.get(Calendar.DATE);  
        c.set(Calendar.DATE, day + offset);  
  
        String dayNew = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());  
        return dayNew;  
    } 
    
	/*
     * 获得指定时间偏移若干分钟后的时间字符串
     * 例如传入 "12:33:05", 4 则返回  "12:37:05"
     */  
    public static String getTimeStrForSpecifiedTimeOffsetM(String specifiedTime, int offset_m) {
        Calendar c = Calendar.getInstance();  
        Date date = null;  
        try {  
            date = new SimpleDateFormat("HH:mm:ss").parse(specifiedTime);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        c.setTime(date);  
        int minute = c.get(Calendar.MINUTE);  
        c.set(Calendar.MINUTE, minute + offset_m);  
  
        String timeNew = new SimpleDateFormat("HH:mm:ss").format(c.getTime());  
        return timeNew;  
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
        	Date curDate = s_cDateTimeProvider.curDate();
    		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String curDateTimeStr = sdf.format(curDate);
    		if(curDateTimeStr.compareTo(waitDateTimeStr) > 0) 
    			return false;
    	}
    	
    	while(true)
    	{
    		Date curDate = s_cDateTimeProvider.curDate();
    		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    		String curDateTimeStr = sdf.format(curDate);
    		
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
     * 获取时间秒数
     * 01:00:01 秒数为 3601
     */
    public static int GetSecond(String time)
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
     * 时间差（秒）
     */
    public static long subTime(String time1, String time2)
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
    
    /*
     * 日期差（天）
     */
    public static long subDate(String date1, String date2)
    {  
    	long diffsec = 0;
		try {
			Date dateObj1 = new SimpleDateFormat("yyyy-MM-dd").parse(date1);
			Date dateObj2 = new SimpleDateFormat("yyyy-MM-dd").parse(date2);  
	        long diff = dateObj1.getTime() - dateObj2.getTime();
	        diffsec = diff / 1000 / 3600 / 24; 
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		} 
        return diffsec;
    }
    
    public static Date getCurDate()
    {
    	return s_cDateTimeProvider.curDate();
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
		
		@Override
		public void run() {
			
			c_startFlag = true;
			
			c_syncObj.Lock();
			c_curDate = getSyncRealDate();
			c_syncObj.UnLock();
			c_lLastSystemSyncTC = System.currentTimeMillis();
			c_lLastLocalProgSyncTC = c_lLastSystemSyncTC;
			
			while(!this.checkQuit())
			{
				CThread.sleep(100);
				long curTC = System.currentTimeMillis();
				if(curTC - c_lLastSystemSyncTC >= 1000*30)
				{
					// 每30s，sync时间一次
					c_syncObj.Lock();
					c_curDate = getSyncRealDate();
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
			        c_syncObj.UnLock();
			        c_lLastLocalProgSyncTC = curTC;
			        
					//System.out.println("DateTimeProvider: Local sync:" + CUtilsDateTime.GetDateTimeStr(c_curDate)); 
				}
			}
		}
		
		private Date getSyncRealDate()
		{
			Date curDate = null;
			curDate = getWebsiteDatetime();
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
	            URL url = new URL("http://www.baidu.com");// 取得资源对象
	            URLConnection uc = url.openConnection();// 生成连接对象
	            uc.connect();// 发出连接
	            long ld = uc.getDate();// 读取网站日期时间
	            Date date = new Date(ld);// 转换为标准时间对象
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
		
		private long c_lLastSystemSyncTC;
		private long c_lLastLocalProgSyncTC;
		
		private CSyncObj c_syncObj;
	}
	
	private static DateTimeProvider s_cDateTimeProvider = new DateTimeProvider();
}
