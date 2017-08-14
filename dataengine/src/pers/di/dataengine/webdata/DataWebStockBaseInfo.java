package pers.di.dataengine.webdata;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import pers.di.dataengine.webdata.CommonDef.StockBaseInfo;
import pers.di.dataengine.webdata.DataWebStockRealTimeInfo.ResultRealTimeInfo;

public class DataWebStockBaseInfo {
	
	public static class ResultStockBaseInfo
	{
		public ResultStockBaseInfo()
		{
			error = 0;
			stockBaseInfo = new StockBaseInfo();
		}
		public int error;
		public StockBaseInfo stockBaseInfo;
	}
	
	/*
	 * 从网络获取某只股票更多当前信息（基本信息，总市值，流通市值，市盈率）
	 * 返回0为成功，其他值为失败
	 */
	public static ResultStockBaseInfo getStockBaseInfo(String id)
	{
		ResultStockBaseInfo cResultStockBaseInfo = new ResultStockBaseInfo();
		
		// get base info
		ResultRealTimeInfo cResultRealTimeInfoBase = DataWebStockRealTimeInfo.getRealTimeInfo(id);
		if(0 != cResultRealTimeInfoBase.error) 
		{
			cResultStockBaseInfo.error = -2;
			return cResultStockBaseInfo;
		}
		
		cResultStockBaseInfo.stockBaseInfo.name = cResultRealTimeInfoBase.realTimeInfo.name;
		cResultStockBaseInfo.stockBaseInfo.date = cResultRealTimeInfoBase.realTimeInfo.date;
		cResultStockBaseInfo.stockBaseInfo.time = cResultRealTimeInfoBase.realTimeInfo.time;
		
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
			cResultStockBaseInfo.error = 0;
			return cResultStockBaseInfo;
		}
		else
		{
			cResultStockBaseInfo.error = -10;
			return cResultStockBaseInfo;
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
			String[] cells = data.split("~");
//			for(int i =0; i< cells.length; i++)
//			{
//				System.out.println(cells[i]);
//			}
			cResultStockBaseInfo.stockBaseInfo.allMarketValue = Float.parseFloat(cells[45]); //总市值
			cResultStockBaseInfo.stockBaseInfo.circulatedMarketValue = Float.parseFloat(cells[44]); // 流通市值
			if(cells[39].length() != 0)
				cResultStockBaseInfo.stockBaseInfo.peRatio = Float.parseFloat(cells[39]); //市盈率
			else
				cResultStockBaseInfo.stockBaseInfo.peRatio = 0.0f;
			
        }catch (Exception e) {  
        	System.out.println("Exception[getRealTimeInfoMore]:" + e.getMessage()); 
            // TODO: handle exception  
        	cResultStockBaseInfo.error = -1;
        	return cResultStockBaseInfo;
        }  
		return cResultStockBaseInfo;
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
