package pers.di.dataapi.webapi;

import java.util.*;

import pers.di.common.CLog;
import pers.di.common.CRandom;
import pers.di.common.CUtilsDateTime;

public class HttpHelper {
	
	public HttpHelper()
	{
		m_lastAccessTC = 0;
	}
	
	public void limitAccessSpeed(long msec)
	{
		long curTC = CUtilsDateTime.GetCurrentTimeMillis();
		if(curTC-m_lastAccessTC > msec)
		{
			m_lastAccessTC = curTC;
			return;
		}
		else
		{
			long alreadyCostTC = curTC-m_lastAccessTC;
			if(alreadyCostTC < 0)
			{
				alreadyCostTC = 0;
			}
				
			long waitms = msec - alreadyCostTC;
			if(waitms > msec)
			{
				waitms = msec;
			}
			if(waitms < 0)
			{
				waitms = 0;
			}
				
			try {
				Thread.sleep(waitms);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			m_lastAccessTC = CUtilsDateTime.GetCurrentTimeMillis();
		}
	}
	
	public String getRandomUserAgent()
	{
		int iSize = s_userAgentList.size();
		int iCurRandom = CRandom.randomUnsignedInteger()%iSize;
		String curUserAgent = s_userAgentList.get(iCurRandom);
		return curUserAgent;
	}
	
	/*
	 * **************************************************************************************
	 */
	public long m_lastAccessTC = 0;
	
	public static List<String> s_userAgentList = Arrays.asList(
			"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/22.0.1207.1 Safari/537.1", 
	        "Mozilla/5.0 (X11; CrOS i686 2268.111.0) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.57 Safari/536.11", 
	        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1092.0 Safari/536.6", 
	        "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.6 (KHTML, like Gecko) Chrome/20.0.1090.0 Safari/536.6", 
	        "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/19.77.34.5 Safari/537.1", 
	        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.9 Safari/536.5", 
	        "Mozilla/5.0 (Windows NT 6.0) AppleWebKit/536.5 (KHTML, like Gecko) Chrome/19.0.1084.36 Safari/536.5", 
	        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3", 
	        "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3", 
	        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_0) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1063.0 Safari/536.3", 
	        "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3", 
	        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1062.0 Safari/536.3", 
	        "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3", 
	        "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3", 
	        "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.1 Safari/536.3", 
	        "Mozilla/5.0 (Windows NT 6.2) AppleWebKit/536.3 (KHTML, like Gecko) Chrome/19.0.1061.0 Safari/536.3", 
	        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24", 
	        "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/535.24 (KHTML, like Gecko) Chrome/19.0.1055.1 Safari/535.24",
	        "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)"
	        );
}
