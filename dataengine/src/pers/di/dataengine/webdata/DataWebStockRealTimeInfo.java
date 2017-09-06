package pers.di.dataengine.webdata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import pers.di.dataengine.common.*;

public class DataWebStockRealTimeInfo {
	
	public static class ResultRealTimeInfo
	{
		public ResultRealTimeInfo()
		{
			error = 0;
			realTimeInfo = new RealTimeInfo();
		}
		public int error;
		public RealTimeInfo realTimeInfo;
	}
	
	/*
	 * 从网络获取某只股票当前信息（基本：名字 日期 时间 价格）
	 * 返回0为成功，其他值为失败
	 */
	public static ResultRealTimeInfo getRealTimeInfo(String id)
	{
		ResultRealTimeInfo cResultRealTimeInfo = new ResultRealTimeInfo();
		// e.g http://hq.sinajs.cn/list=sz300163
		String urlStr = "http://hq.sinajs.cn/list=";
		String tmpId = "";
		if(id.startsWith("60") && 6 == id.length())
		{
			tmpId = "sh" + id;
		}
		else if((id.startsWith("00") ||  id.startsWith("30")) && 6 == id.length())
		{
			tmpId = "sz" + id;
		}
		else if(id.startsWith("99")) // 上证指数
		{
			tmpId = "sh" + "000001";
		}
		else
		{
			cResultRealTimeInfo.error = -10;
			return cResultRealTimeInfo;
		}
		urlStr = urlStr + tmpId;
		
		try{  
			
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();   
	        
	        conn.setConnectTimeout(5*1000);  //设置连接超时间 
	        conn.setReadTimeout(15*1000); //设置读取超时时间 
	        
	        //防止屏蔽程序抓取而返回403错误  
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
			InputStream inputStream = conn.getInputStream(); 
			byte[] getData = readInputStream(inputStream); 
			String data = new String(getData, "gbk");  
			//System.out.println(data);     
			String[] cells = data.split("\"");
			int lenCells = cells.length;
			String validdata = cells[lenCells - 2];
			//System.out.println(validdata);     
			String[] cols = validdata.split(",");
			cResultRealTimeInfo.realTimeInfo.name = cols[0];
			cResultRealTimeInfo.realTimeInfo.curPrice = Float.parseFloat(cols[3]);
			cResultRealTimeInfo.realTimeInfo.date = cols[30];
			cResultRealTimeInfo.realTimeInfo.time = cols[31];
			if(cResultRealTimeInfo.realTimeInfo.date.length() < 2 || cResultRealTimeInfo.realTimeInfo.name.length() < 2)
			{
				System.out.println("Exception[DataWebStockRealTimeInfo]: invalid data"); 
				cResultRealTimeInfo.error = -2;
				return cResultRealTimeInfo;
			}
			
        }catch (Exception e) {  
        	System.out.println("Exception[DataWebStockRealTimeInfo]:" + e.getMessage()); 
            // TODO: handle exception  
        	cResultRealTimeInfo.error = -1;
			return cResultRealTimeInfo;
        }  
		return cResultRealTimeInfo;
	}
	
    public static  byte[] readInputStream(InputStream inputStream) throws IOException {    
        byte[] buffer = new byte[1024];    
        int len = 0;    
        ByteArrayOutputStream bos = new ByteArrayOutputStream();    
        while((len = inputStream.read(buffer)) != -1) {    
            bos.write(buffer, 0, len);    
        }    
        bos.close();    
        return bos.toByteArray();    
    }  
}
