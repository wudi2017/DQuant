package pers.di.dataapi.webapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pers.di.dataapi.common.RealTimeInfo;
import pers.di.dataapi.common.StockInfo;
import pers.di.dataapi.common.*;

public class DataWebStockInfo extends HttpHelper
{
	/*
	 * 从网络获取某只股票信息（基本信息，总市值，流通市值，市盈率等）
	 * 
	 * 返回值：
	 *     返回0为成功，其他值为失败
	 * 参数：
	 *     container 接收容器
	 */
	public static int getStockInfo(String id, StockInfo container)
	{
		// limitAccessSpeed(0); // not limit
		
		int error = 0;
		
		// get base info
		RealTimeInfo ctnRealTimeInfo = new RealTimeInfo();
		int errGetRealTimeInfo = DataWebStockRealTimeInfo.getRealTimeInfo(id, ctnRealTimeInfo);
		if(0 != errGetRealTimeInfo) 
		{
			error = -2;
			return error;
		}
		
		if(ctnRealTimeInfo.curPrice <=0.0f)
		{
			error = -3;
			return error;
		}
		
		container.name = ctnRealTimeInfo.name;
		container.date = ctnRealTimeInfo.date;
		container.time = ctnRealTimeInfo.time;
		
		// e.g http://qt.gtimg.cn/q=sz000858
		String urlStr = "http://qt.gtimg.cn/q=";
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
			tmpId = "sh" + "000001"; // 上证指数没有更多基本信息
			error = 0;
			return error;
		}
		else
		{
			error = -10;
			return error;
		}
		urlStr = urlStr + tmpId;
		
		try{  
			
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    

	        conn.setConnectTimeout(5*1000);  //设置连接超时间 
	        conn.setReadTimeout(15*1000); //设置读取超时时间
	        
	        //防止屏蔽程序抓取而返回403错误  
	        conn.setRequestProperty("User-Agent", getRandomUserAgent());  
			InputStream inputStream = conn.getInputStream(); 
			byte[] getData = readInputStream(inputStream); 
			String data = new String(getData, "gbk");  
			//System.out.println(data);     
			String[] cells = data.split("~");
//			for(int i =0; i< cells.length; i++)
//			{
//				System.out.println(cells[i]);
//			}
			container.allMarketValue = Float.parseFloat(cells[45]); //总市值
			container.circulatedMarketValue = Float.parseFloat(cells[44]); // 流通市值
			if(cells[39].length() != 0)
				container.peRatio = Float.parseFloat(cells[39]); //市盈率
			else
				container.peRatio = 0.0f;
			
        }catch (Exception e) {  
        	System.out.println("Exception[getRealTimeInfoMore]:" + e.getMessage()); 
            // TODO: handle exception  
        	error = -1;
        	return error;
        }  
		return error;
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
