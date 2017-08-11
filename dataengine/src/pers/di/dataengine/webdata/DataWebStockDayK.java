package pers.di.dataengine.webdata;

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
import pers.di.dataengine.webdata.DataWebCommonDef.*;

public class DataWebStockDayK {
	/*
	 * 从网络获取某只股票的日K数据
	 * 传入999999代表上证指数
	 * 返回0为成功，其他值为失败
	 */
	public static class ResultKData
	{
		public ResultKData()
		{
			error = 0;
			resultList = new ArrayList<KData>();
		}
		public int error;
		public List<KData> resultList;
	}
	
	/*
	 * id: 
	 * 上证指数：999999
	 * 上海股票：60****
	 * 深圳股票：00****或30****
	 * 
	 * doc: http://blog.csdn.net/xp5xp6/article/details/53121481
	 * v1.0: url: e.g "http://biz.finance.sina.com.cn/stock/flash_hq/kline_data.php?symbol=sz000002&begin_date=20160101&end_date=21000101"
	 * v2.0: url: e.g "http://quotes.money.163.com/service/chddata.html?code=0601857&start=20171105&end=20170809&fields=TCLOSE;HIGH;LOW;TOPEN;VOTURNOVER;"
	 */
	public static ResultKData getKData(String id, String begin_date, String end_date)
	{
		ResultKData cResultKData = new ResultKData();
		
		String innerID = "";
		if(id.startsWith("60") && 6 == id.length())
		{
			innerID = "0" + id;
		}
		else if((id.startsWith("00") ||  id.startsWith("30")) && 6 == id.length())
		{
			innerID = "1" + id;
		}
		else if(id.startsWith("999999")) // 上证指数
		{
			innerID = "0" + "000001";
		}
		else
		{
			cResultKData.error = -10;
			return cResultKData;
		}
		
		String urlStr = "http://quotes.money.163.com/service/chddata.html?";
		urlStr = urlStr + "code=" + innerID + "&start=" + begin_date + "&end=" + end_date + "&fields=TOPEN;TCLOSE;LOW;HIGH;VOTURNOVER;";
		
		try
		{
			String htmlstr = CHttp.getWebData(urlStr);
			parseHtml2(htmlstr, cResultKData);
		}
		catch(Exception e)
		{
			System.out.println("Exception[WebStockDayK]:" + e.getMessage()); 
        	cResultKData.error = -1;
        	return cResultKData;
		}
		
		Collections.sort(cResultKData.resultList);
		
		return cResultKData;
	}
	
	// for html: "http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?symbol=sz002095&scale=240&ma=no&datalen=1023"
	private static void parseHtml2(String in_str, ResultKData out_result)
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
        	
        	KData cKData = new KData();
        	cKData.date = date;
        	cKData.open = Float.parseFloat(open);
        	cKData.close = Float.parseFloat(close);
        	cKData.low = Float.parseFloat(low);
        	cKData.high = Float.parseFloat(high);
        	cKData.volume = Float.parseFloat(volume);
        	
        	if(0 == Float.compare(0.0f, cKData.close))
        	{
        		continue;
        	}
        	
        	out_result.resultList.add(cKData);
		}
	}
	
	// for html: "http://biz.finance.sina.com.cn/stock/flash_hq/kline_data.php?symbol=sz000002&begin_date=20160101&end_date=21000101"
	private static void parseHtml1(String in_str, ResultKData out_result)
	{
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringReader sr = new StringReader(in_str);
	        InputSource is = new InputSource(sr);
	        Document doc = builder.parse(is);
	        Element rootElement = doc.getDocumentElement();
	        // 检查返回数据有效性
	        if(!rootElement.getTagName().contains("control")) 
	        {
	        	out_result.error = -30;
	        }

	        NodeList contents = rootElement.getElementsByTagName("content");
	        int lenList = contents.getLength();
	        for (int i = 0; i < lenList; i++) {
	        	KData cKData = new KData();
	        	Node cnode = contents.item(i);
	        	String date = ((Element)cnode).getAttribute("d");
	        	String open = ((Element)cnode).getAttribute("o");
	        	String high = ((Element)cnode).getAttribute("h");
	        	String close = ((Element)cnode).getAttribute("c");
	        	String low = ((Element)cnode).getAttribute("l");
	        	String volume = ((Element)cnode).getAttribute("v");
	        	cKData.date = date;
	        	cKData.open = Float.parseFloat(open);
	        	cKData.close = Float.parseFloat(close);
	        	cKData.low = Float.parseFloat(low);
	        	cKData.high = Float.parseFloat(high);
	        	cKData.volume = Float.parseFloat(volume);
	        	
	        	out_result.resultList.add(cKData);
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
