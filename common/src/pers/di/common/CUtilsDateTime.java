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
	 *  ��ǰʱ���
	 */
	static public long GetCurrentTimeMillis()
	{
		return System.currentTimeMillis();
	}
	
	/*
	 *  ��ǰ���ڶ���
	 *  class Date
	 */
	static public Date GetCurDate()
	{
		return s_cDateTimeProvider.curDate();
		// return GetDateTimeStr(s_cDateTimeProvider.curDate());
	}
	
	/*
	 *  ��ǰ����ʱ���ַ���
	 *  XXXX-XX-XX XX:XX:XX
	 */
	static public String GetCurDateTimeStr()
	{
		return s_cDateTimeProvider.curDateTime();
		// return GetDateTimeStr(new Date());
	}
	
	/*
	 *  ��ǰ����
	 *  XXXX-XX-XX
	 */
	static public String GetCurDateStr()
	{
		return GetCurDateTimeStr().substring(0, 10);
	}
	
	/*
	 *  ��ǰʱ��
	 *  XX:XX:XX
	 */
	static public String GetCurTimeStr()
	{
		return GetCurDateTimeStr().substring(11, 19);
	}
	
//	/*
//	 *  ת�����ڶ���Date���ַ���
//	 *  ����  "yyyy-MM-dd"
//	 */
//	static public String GetDateStr(Date cDate)
//	{
//		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
//		return sdf.format(cDate);
//	}
	
//	/*
//	 *  ת�����ڶ���Date���ַ���
//	 *  ����  "HH:mm:ss"
//	 */
//	static public String GetTimeStr(Date cDate)
//	{
//		SimpleDateFormat sdf =new SimpleDateFormat("HH:mm:ss");
//		return sdf.format(cDate);
//	}
	
//	/*
//	 *  ת�����ڶ���Date���ַ���
//	 *  ����  "HH:mm"
//	 */
//	static public String GetTimeStrHM(Date cDate)
//	{
//		SimpleDateFormat sdf =new SimpleDateFormat("HH:mm");
//		return sdf.format(cDate);
//	}
	
//	/*
//	 *  ת�����ڶ���Date���ַ���
//	 *  ����  "yyyy-MM-dd HH:mm:ss"
//	 */
//	static public String GetDateTimeStr(Date cDate)
//	{
//		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		return sdf.format(cDate);
//	}
	
//	/*
//	 * ת�������ַ��������� Date
//	 * ���������ַ�������Ϊ "yyyy-MM-dd"
//	 * ���������ַ�������Ϊ "yyyy-MM-dd HH:mm:ss"
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
     * ���ָ������ƫ�ƺ�������ַ���
     * ���紫�� "2016-01-06", 4 �򷵻�  "2016-01-10"
     */  
    public static String getDateStrForSpecifiedDateOffsetD(String specifiedDate, int offset_d) {
        Calendar c = Calendar.getInstance();  
        Date date = null;  
        try {  
            date = new SimpleDateFormat("yyyy-MM-dd").parse(specifiedDate);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        c.setTime(date);  
        int day = c.get(Calendar.DATE);  
        c.set(Calendar.DATE, day + offset_d);  
  
        String dayNew = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());  
        return dayNew;  
    } 
    
	/*
     * ���ָ��ʱ��ƫ����������ʱ���ַ���
     * ���紫�� "12:33:05", 65 �򷵻�  "12:34:10"
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
     * ��ȡʱ������
     * 01:00:01 ����Ϊ 3601
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
     * ��ȡ����ʱ��
     *  3601 ��ʱ��Ϊ 01:00:01
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
     * �ȴ�����ʱ��
     * �ȴ���ʱ��󷵻�true
     * ����ʱ�Ѿ���ʱ����false
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
     * ʱ���룩
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
     * ���ڲ�죩
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
					// ÿ30s��syncʱ��һ��
					c_syncObj.Lock();
					c_curDate = getSyncRealDate();
					c_curDateTimeStr = c_sdf.format(c_curDate);
					c_syncObj.UnLock();
					c_lLastSystemSyncTC = curTC;
					
				}
				if(curTC - c_lLastLocalProgSyncTC >= 1000)
				{
					// ÿ�룬���ظ���ʱ��
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
		            URL url = new URL("http://www.baidu.com");// ȡ����Դ����
		            URLConnection uc = url.openConnection();// �������Ӷ���
		            uc.connect();// ��������
		            long ld = uc.getDate();// ��ȡ��վ����ʱ��
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
