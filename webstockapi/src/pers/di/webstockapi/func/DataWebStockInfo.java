package pers.di.webstockapi.func;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import pers.di.webstockapi.WebStockAPI.RealTimeInfoLite;
import pers.di.webstockapi.WebStockAPI.StockInfo;

public class DataWebStockInfo extends HttpHelper
{
	public DataWebStockInfo()
	{
		m_cDataWebStockRealTimeInfo = new DataWebStockRealTimeInfo();
	}
	
	/*
	 * �������ȡĳֻ��Ʊ��Ϣ��������Ϣ������ֵ����ͨ��ֵ����ӯ�ʵȣ�
	 * 
	 * ����ֵ��
	 *     ����0Ϊ�ɹ�������ֵΪʧ��
	 * ������
	 *     container ��������
	 */
	public int getStockInfo(String id, StockInfo container)
	{
		// limitAccessSpeed(0); // not limit
		
		int error = 0;
		
		// get base info
		List<RealTimeInfoLite> ctnRealTimeInfos = new ArrayList<RealTimeInfoLite>();
		List ids = new ArrayList<String>();
		ids.add(id);
		int errGetRealTimeInfo = m_cDataWebStockRealTimeInfo.getRealTimeInfo(ids, ctnRealTimeInfos);
		if(0 != errGetRealTimeInfo) 
		{
			error = -2;
			return error;
		}
		
		RealTimeInfoLite ctnRealTimeInfo = ctnRealTimeInfos.get(0);
		
		container.name = ctnRealTimeInfo.name;
		container.date = ctnRealTimeInfo.date;
		container.time = ctnRealTimeInfo.time;
		container.curPrice = ctnRealTimeInfo.curPrice;
		
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
		else if(id.startsWith("99")) // ��ָ֤��
		{
			tmpId = "sh" + "000001"; // ��ָ֤��û�и��������Ϣ
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

	        conn.setConnectTimeout(5*1000);  //�������ӳ�ʱ�� 
	        conn.setReadTimeout(15*1000); //���ö�ȡ��ʱʱ��
	        
	        //��ֹ���γ���ץȡ������403����  
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
			container.allMarketValue = Double.parseDouble(cells[45]); //����ֵ
			container.circulatedMarketValue = Double.parseDouble(cells[44]); // ��ͨ��ֵ
			if(cells[39].length() != 0)
				container.peRatio = Double.parseDouble(cells[39]); //��ӯ��
			else
				container.peRatio = 0.0f;
			
        }catch (Exception e) {  
        	e.printStackTrace();
        	System.out.println("Exception[getRealTimeInfoMore]:" + e.getMessage()); 
        	error = -1;
        	return error;
        }  
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
    
    private DataWebStockRealTimeInfo m_cDataWebStockRealTimeInfo;
}
