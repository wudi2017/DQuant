package pers.di.dataapi.webapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import pers.di.dataapi.common.TradeDetail;
import pers.di.common.CLog;
import pers.di.common.CThread;
import pers.di.dataapi.common.*;

public class DataWebStockDayDetail extends HttpHelper 
{
	/*
	 * ������ĳֻ��Ʊĳ���ڵĽ���ϸ������
	 * 
	 * ����ֵ��
	 *     ����0Ϊ�ɹ�������ֵΪʧ��
	 * ������
	 *     container ��������
	 */
	public int getDayDetail(String id, String date, List<TradeDetail> container)
	{
		limitAccessSpeed(1500);
		
		int error = 0;
		
		// e.g "http://market.finance.sina.com.cn/downxls.php?date=2015-02-16&symbol=sz300163"
		String urlStr = "http://market.finance.sina.com.cn/downxls.php?";
		String tmpId = "";
		if(id.startsWith("60") && 6 == id.length())
		{
			tmpId = "sh" + id;
		}
		else if((id.startsWith("00") ||  id.startsWith("30")) && 6 == id.length())
		{
			tmpId = "sz" + id;
		}
		else
		{
			error = -10;
			return error;
		}
		
		try
		{
			urlStr = urlStr + "date=" + date + "&symbol=" + tmpId;
			URL url = new URL(urlStr);    
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();    
	                 
	        conn.setConnectTimeout(5*1000);  //�������ӳ�ʱ�� 
	        conn.setReadTimeout(15*1000); //���ö�ȡ��ʱʱ��
	        
	        //��ֹ���γ���ץȡ������403����  
	        conn.setRequestProperty("User-Agent", getRandomUserAgent());  
	        //�õ�������  
	        InputStream inputStream = conn.getInputStream();   
	        //��ȡ�Լ�����  
	        byte[] getData = readInputStream(inputStream);    
	        String data = new String(getData, "gbk");  
	        //System.out.println(data);
	        String[] lines = data.split("\n");
	        for(int i=0; i < lines.length; i++)
	        {
	        	if(i==0) continue;
	        	String line = lines[i].trim();
	        	String[] cols = line.split("\t");
	        	
	        	TradeDetail cTradeDetail = new TradeDetail();
	        	cTradeDetail.time = cols[0];
	        	cTradeDetail.price = Double.parseDouble(cols[1]);
	        	cTradeDetail.volume = Double.parseDouble(cols[3]);
	        	
	        	container.add(cTradeDetail);
	        }
	        
	        if(container.size() <= 0) 
        	{
	        	error = -30;
	        	return error;
        	}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception[WebStockDayDetail]:" + e.getMessage()); 
			if(e.getMessage().contains("Server returned HTTP response code: 456 for URL:"))
			{
				CLog.error("DATAAPI", "Exception[WebStockDayDetail]:%s, need to wait!", e.getMessage());
				// ��Ƶ���ʼ��, ֹͣ6����
				CThread.msleep(1000*60*6);
			}
			error = -1;
        	return error;
		}
	
		Collections.sort(container);
		return error;
	}
	
    private byte[] readInputStream(InputStream inputStream) throws IOException {    
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
