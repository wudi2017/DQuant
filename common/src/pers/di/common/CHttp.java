package pers.di.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CHttp {
	public static String getWebData(String urlStr) {
		
		String retData = null;

		try {
			
			URL url;
			url = new URL(urlStr);
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    

	        conn.setConnectTimeout(5*1000);  //设置连接超时间 
	        conn.setReadTimeout(15*1000); //设置读取超时时间
	        
	        //防止屏蔽程序抓取而返回403错误  
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");  
	        //得到输入流  
	        InputStream inputStream = conn.getInputStream();   
	        //获取自己数组  
	        byte[] getData = readInputStream(inputStream);    
	        String data = new String(getData, "gbk");
	        
	        retData = data;
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Exception[CHttp]:" + e.getMessage()); 
		}    
		
        //System.out.println(data.toString()); 
//        //文件保存位置  
//        File file = new File("D:/test.txt");      
//        FileOutputStream fos = new FileOutputStream(file);       
//        fos.write(getData);   
//        if(fos!=null){  
//            fos.close();    
//        }  
//        if(inputStream!=null){  
//            inputStream.close();  
//        }  
        
        return retData;
	}
	
    private static  byte[] readInputStream(InputStream inputStream) throws IOException {    
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
