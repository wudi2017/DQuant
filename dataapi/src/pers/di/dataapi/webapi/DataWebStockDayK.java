package pers.di.dataapi.webapi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import pers.di.common.CHttp;
import pers.di.dataapi.common.KLine;
import pers.di.dataapi.common.*;

public class DataWebStockDayK {
	/*
	 * �������ȡĳֻ��Ʊ����K����
	 * 
	 * ����ֵ��
	 *     ����0Ϊ�ɹ�������ֵΪʧ��
	 * ������
	 *     ����999999������ָ֤��
	 *     �Ϻ���Ʊ��60****
	 *     ���ڹ�Ʊ��00****��30****
	 *     container ��������
	 *     
	 * ������
	 *     doc: http://blog.csdn.net/xp5xp6/article/details/53121481
	 *     v1.0: url: e.g "http://biz.finance.sina.com.cn/stock/flash_hq/kline_data.php?symbol=sz000002&begin_date=20160101&end_date=21000101"
	 *     v2.0: url: e.g "http://quotes.money.163.com/service/chddata.html?code=0601857&start=20171105&end=20170809&fields=TCLOSE;HIGH;LOW;TOPEN;VOTURNOVER;"
	 
	 */
	public static int getKLine(String id, String begin_date, String end_date, List<KLine> container)
	{
		int error = 0;
		
		String innerID = "";
		if(id.startsWith("60") && 6 == id.length())
		{
			innerID = "0" + id;
		}
		else if((id.startsWith("00") ||  id.startsWith("30")) && 6 == id.length())
		{
			innerID = "1" + id;
		}
		else if(id.startsWith("999999")) // ��ָ֤��
		{
			innerID = "0" + "000001";
		}
		else
		{
			error = -10;
			return error;
		}
		
		String urlStr = "http://quotes.money.163.com/service/chddata.html?";
		urlStr = urlStr + "code=" + innerID + "&start=" + begin_date + "&end=" + end_date + "&fields=TOPEN;TCLOSE;LOW;HIGH;VOTURNOVER;";
		
		try
		{
			String htmlstr = m_http.getWebData(urlStr, 1000);
			parseHtml2(htmlstr, container);
		}
		catch(Exception e)
		{
			System.out.println("Exception[WebStockDayK]:" + e.getMessage()); 
			error = -1;
        	return error;
		}
		
		Collections.sort(container);
		
		return error;
	}
	
	// for html: "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=sz002095&scale=240&ma=no&datalen=1023"
	private static void parseHtml2(String in_str, List<KLine> resultList)
	{
		String[] lines = in_str.split("\n");
		for(int i=0; i<lines.length; i++)
		{
			String line = lines[i];
			
			String[] cols = line.split(",");
			String date = cols[0];
        	String open = cols[3];
        	String close = cols[4];
        	String low = cols[5];
        	String high = cols[6];
        	String volume = cols[7];
        	
        	if(cols.length < 5 
        			|| date.length()!="0000-00-00".length())
        	{
        		continue;
        	}
        	
        	KLine cKLine = new KLine();
        	cKLine.date = date;
        	cKLine.open = Double.parseDouble(open);
        	cKLine.close = Double.parseDouble(close);
        	cKLine.low = Double.parseDouble(low);
        	cKLine.high = Double.parseDouble(high);
        	cKLine.volume = Double.parseDouble(volume);
        	
        	if(0 == Double.compare(0.0f, cKLine.close))
        	{
        		continue;
        	}
        	
        	resultList.add(cKLine);
		}
	}
	
	// for html: "http://biz.finance.sina.com.cn/stock/flash_hq/kline_data.php?symbol=sz000002&begin_date=20160101&end_date=21000101"
	private static int parseHtml1(String in_str, List<KLine> resultList)
	{
		int error = 0;
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringReader sr = new StringReader(in_str);
	        InputSource is = new InputSource(sr);
	        Document doc = builder.parse(is);
	        Element rootElement = doc.getDocumentElement();
	        // ��鷵��������Ч��
	        if(!rootElement.getTagName().contains("control")) 
	        {
	        	error = -30;
	        }

	        NodeList contents = rootElement.getElementsByTagName("content");
	        int lenList = contents.getLength();
	        for (int i = 0; i < lenList; i++) {
	        	KLine cKLine = new KLine();
	        	Node cnode = contents.item(i);
	        	String date = ((Element)cnode).getAttribute("d");
	        	String open = ((Element)cnode).getAttribute("o");
	        	String high = ((Element)cnode).getAttribute("h");
	        	String close = ((Element)cnode).getAttribute("c");
	        	String low = ((Element)cnode).getAttribute("l");
	        	String volume = ((Element)cnode).getAttribute("v");
	        	cKLine.date = date;
	        	cKLine.open = Double.parseDouble(open);
	        	cKLine.close = Double.parseDouble(close);
	        	cKLine.low = Double.parseDouble(low);
	        	cKLine.high = Double.parseDouble(high);
	        	cKLine.volume = Double.parseDouble(volume);
	        	
	        	resultList.add(cKLine);
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return error;
	}
	
	public static CHttp m_http = new CHttp();
}
