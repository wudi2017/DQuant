package pers.di.accountengine.detail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import pers.di.accountengine.common.*;
import pers.di.common.*;

public class AccountStore {
	
	public static class StoreEntity
	{
		public StoreEntity()
		{
			date = null;
			time = null;
			commissionOrderList = null;
			holdStockInvestigationDaysMap = null;
		}
		public String date;
		public String time;
		public List<CommissionOrder> commissionOrderList;
		public Map<String, Integer> holdStockInvestigationDaysMap;
	}
	
	public AccountStore(String accountID, String password)
	{
		m_storeEntity = null;
		m_accountID = accountID;
		m_password = password;
		m_accXMLFile = "rw\\ACCOUNT_EXTEND_" + m_accountID + ".xml";
	}
	
	public StoreEntity storeEntity()
	{
		return m_storeEntity;
	}
	
	public boolean storeInit()
	{
		Document doc=null;
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder= factory.newDocumentBuilder();
			doc=builder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 创建元素
		Element root=doc.createElement("account");
		root.setAttribute("ID", m_accountID);
		root.setAttribute("password", m_password);
        doc.appendChild(root);
		
		TransformerFactory tfactory=TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tfactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(null != doc && null != transformer)
		{
			DOMSource source=new DOMSource(doc);
			StreamResult result=new StreamResult(new File(m_accXMLFile));
			transformer.setOutputProperty("encoding","GBK");
			try {
				transformer.transform(source,result);
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}
	
	public boolean flush()
	{
		File cfile=new File(m_accXMLFile);
		if(cfile.exists())
		{
			cfile.delete();
		}
		
		Document doc=null;
		DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder= factory.newDocumentBuilder();
			doc=builder.newDocument();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 创建元素
		Element root=doc.createElement("account");
		root.setAttribute("ID", m_accountID);
		root.setAttribute("password", m_password);
        doc.appendChild(root);
        
        if(null != m_storeEntity)
        {
        	// date time
        	if(null != m_storeEntity.date)
        	{
        		root.setAttribute("date", m_storeEntity.date);
        	}
        	if(null != m_storeEntity.time)
        	{
        		root.setAttribute("time", m_storeEntity.time);
        	}

        	// CommissionOrderList
        	if(null != m_storeEntity.commissionOrderList)
        	{
        		Element Node_CommissionOrderList=doc.createElement("CommissionOrderList");
            	root.appendChild(Node_CommissionOrderList);
            	for(int i=0;i<m_storeEntity.commissionOrderList.size();i++)
            	{
            		CommissionOrder cCommissionOrder = m_storeEntity.commissionOrderList.get(i);
            		String tranactVal = "";
            		if(cCommissionOrder.tranAct == TRANACT.BUY) tranactVal= "BUY";
            		if(cCommissionOrder.tranAct == TRANACT.SELL) tranactVal= "SELL";
            		String amountVal = String.format("%d", cCommissionOrder.amount);
            		String priceVal =String.format("%.3f", cCommissionOrder.price);
            				
            		Element Node_Stock = doc.createElement("Stock");
            		Node_Stock.setAttribute("time", cCommissionOrder.time);
            		Node_Stock.setAttribute("tranAct", tranactVal);
            		Node_Stock.setAttribute("stockID", cCommissionOrder.stockID);
            		Node_Stock.setAttribute("amount", amountVal);
            		Node_Stock.setAttribute("price", priceVal);
            		
            		Node_CommissionOrderList.appendChild(Node_Stock);
            	}
        	}

        	// holdStockInvestigationDaysMap
        	if(null != m_storeEntity.holdStockInvestigationDaysMap)
        	{
            	Element Node_holdStockInvestigationDaysMap=doc.createElement("holdStockInvestigationDaysMap");
            	root.appendChild(Node_holdStockInvestigationDaysMap);
            	for (Map.Entry<String, Integer> entry : m_storeEntity.holdStockInvestigationDaysMap.entrySet()) {  
            		String stockID = entry.getKey();
            		Integer investigationDays = entry.getValue();
            		Element Node_StockInvestigation = doc.createElement("StockInvestigation");
            		Node_StockInvestigation.setAttribute("stockID", stockID);
            		Node_StockInvestigation.setAttribute("investigationDays", investigationDays.toString());
            		Node_holdStockInvestigationDaysMap.appendChild(Node_StockInvestigation);
            	} 
        	}
        }
		
		TransformerFactory tfactory=TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tfactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 获得最终oriXmlStr
		String oriXmlStr ="";
		if(null != doc && null != transformer)
		{
			transformer.setOutputProperty("encoding","GBK");
			DOMSource source=new DOMSource(doc);
			
			StringWriter writer = new StringWriter();
			StreamResult result=new StreamResult(writer);
			try {
				transformer.transform(source,result);
				oriXmlStr = writer.toString();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// 格式化XmlStr
		String formatedXmlStr = "";
		try {
			formatedXmlStr = CUtilsXML.format(oriXmlStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 更新到文件
		File cfile_new = new File(m_accXMLFile);
		try {
			FileWriter fw = new FileWriter(cfile_new.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(formatedXmlStr);
		    bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean load()
	{
		String xmlStr = "";
		File cfile=new File(m_accXMLFile);
		if(!cfile.exists())
		{
			CLog.output("ACCOUNT", "AccountStore storeInit (no file)\n");
			return false; // 没有文件 load失败
		}
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(cfile));
	        int fileLen = (int)cfile.length();
	        char[] chars = new char[fileLen];
	        reader.read(chars);
	        xmlStr = String.valueOf(chars);
//			String tempString = "";
//			while ((tempString = reader.readLine()) != null) {
//				xmlStr = xmlStr + tempString + "\n";
//	        }
			reader.close();
			//fmt.format("XML:\n" + xmlStr);
			if(xmlStr.length()<=0)
			{
				CLog.output("ACCOUNT", "AccountStore storeInit (no content)\n");
				return false; // 没有内容 load失败
			}
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    StringReader sr = new StringReader(xmlStr);
		    InputSource is = new InputSource(sr);
		    Document doc = builder.parse(is);
		    Element rootElement = doc.getDocumentElement();
		    
		    // 检查返回数据有效性
		    if(!rootElement.getTagName().contains("account")) 
			{
		    	CLog.output("ACCOUNT", "AccountStore storeInit (no account root)\n");
				return false; // 没有root load失败
			}
		    
		    // 账户属性判断并加载
		    String accountID = rootElement.getAttribute("ID");
		    String password = rootElement.getAttribute("password");
		    if(!accountID.equals(m_accountID) || !password.equals(m_password))
			{
		    	CLog.error("ACCOUNT", "AccountStore storeInit (accountID or password error)\n");
				return false; // 账号秘密不对 load失败
			}
		    
        	// date time
		    String accDate = rootElement.getAttribute("date");
		    String accTime = rootElement.getAttribute("time");
		    
		    // 选股列表加载
		    List<String> stockSelectList = null;
		    {
		    	NodeList nodelist_SelectList = rootElement.getElementsByTagName("SelectList");
		        if(nodelist_SelectList.getLength() == 1)
	        	{
		        	stockSelectList = new ArrayList<String>();
		        	
		        	Node Node_SelectList = nodelist_SelectList.item(0);
		        	NodeList nodelist_Stock = Node_SelectList.getChildNodes();
			        for (int i = 0; i < nodelist_Stock.getLength(); i++) {
			        	Node node_Stock = nodelist_Stock.item(i);
			        	if(node_Stock.getNodeType() == Node.ELEMENT_NODE)
			        	{
				        	String stockID = ((Element)node_Stock).getAttribute("stockID");
				        	//CLog.output("ACCOUNT", "stockID:%s \n", stockID);
				        	stockSelectList.add(stockID); 
			        	}
			        }
	        	}
		    }
		    
		    // 委托单加载
		    List<CommissionOrder> commissionOrderList = new ArrayList<CommissionOrder>();
		    {
		    	NodeList nodelist_CommissionOrderList = rootElement.getElementsByTagName("CommissionOrderList");
		        if(nodelist_CommissionOrderList.getLength() == 1)
	        	{
		        	Node Node_CommissionOrderList = nodelist_CommissionOrderList.item(0);
		        	NodeList nodelist_Stock = Node_CommissionOrderList.getChildNodes();
			        for (int i = 0; i < nodelist_Stock.getLength(); i++) {
			        	Node node_Stock = nodelist_Stock.item(i);
			        	if(node_Stock.getNodeType() == Node.ELEMENT_NODE)
			        	{
			        		String time = ((Element)node_Stock).getAttribute("time");
				        	String tranAct = ((Element)node_Stock).getAttribute("tranAct");
				        	String stockID = ((Element)node_Stock).getAttribute("stockID");
				        	String amount = ((Element)node_Stock).getAttribute("amount");
				        	String price = ((Element)node_Stock).getAttribute("price");
				        	
				        	CommissionOrder cCommissionOrder = new CommissionOrder();
				        	cCommissionOrder.time = time;
				        	if(tranAct.equals("BUY")) cCommissionOrder.tranAct = TRANACT.BUY;
				        	if(tranAct.equals("SELL")) cCommissionOrder.tranAct = TRANACT.SELL;
				        	cCommissionOrder.stockID = stockID;
				        	cCommissionOrder.amount = Integer.parseInt(amount);
				        	cCommissionOrder.price = Float.parseFloat(price);
				        	commissionOrderList.add(cCommissionOrder);
			        	}
			        }
	        	}
		    }
		    
        	// holdStockInvestigationDaysMap
		    Map<String, Integer> holdStockInvestigationDaysMap = null;
		    {
		    	NodeList nodelist_HoldStockInvestigationDaysMap = rootElement.getElementsByTagName("holdStockInvestigationDaysMap");
		    	if(nodelist_HoldStockInvestigationDaysMap.getLength() == 1)
	        	{
		    		holdStockInvestigationDaysMap = new HashMap<String, Integer>();
		    		
		        	Node HoldStockInvestigationDaysMap = nodelist_HoldStockInvestigationDaysMap.item(0);
		        	NodeList nodelist_StockInvestigation = HoldStockInvestigationDaysMap.getChildNodes();
			        for (int i = 0; i < nodelist_StockInvestigation.getLength(); i++) {
			        	Node node_StockInvestigation = nodelist_StockInvestigation.item(i);
			        	if(node_StockInvestigation.getNodeType() == Node.ELEMENT_NODE)
			        	{
			        		String stockID = ((Element)node_StockInvestigation).getAttribute("stockID");
			        		String investigationDays = ((Element)node_StockInvestigation).getAttribute("investigationDays");
				        	//CLog.output("ACCOUNT", "stockID:%s \n", stockID);
			        		holdStockInvestigationDaysMap.put(stockID, Integer.parseInt(investigationDays));
			        	}
			        }
	        	}
		    }
		 
		    m_storeEntity.date = accDate;
		    m_storeEntity.time = accTime;
		    m_storeEntity.commissionOrderList = commissionOrderList;
		    return true;
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage()); 
			return false;
		}
	}
	
	/**
	 * 成员-----------------------------------------------------------------------
	 */
	private StoreEntity m_storeEntity;
	private String m_accountID;
	private String m_password;
	private String m_accXMLFile;
}
