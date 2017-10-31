package pers.di.account.detail;

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

import pers.di.account.common.*;
import pers.di.common.*;

public class AccountStore {
	
	public static class StoreEntity
	{
		public StoreEntity()
		{
			date = CUtilsDateTime.GetCurDateStr();
		    time = CUtilsDateTime.GetCurTimeStr();
		    commissionOrderList = new ArrayList<CommissionOrder>();
		    holdStockList = new ArrayList<HoldStock>();
		}
		public void reset()
		{
			date = CUtilsDateTime.GetCurDateStr();
		    time = CUtilsDateTime.GetCurTimeStr();
		    commissionOrderList.clear();
		    holdStockList.clear();
		}
		public String date;
		public String time;
		public List<CommissionOrder> commissionOrderList;
		public List<HoldStock> holdStockList;
	}
	
	public AccountStore(String accountID)
	{
		// init root dir
		if(!CFileSystem.isDirExist(s_accountDataRoot))
		{
			CFileSystem.createDir(s_accountDataRoot);
		}
		
		m_accountID = accountID;
		m_accXMLFile = s_accountDataRoot + "\\" + m_accountID + ".xml";
		m_storeEntity = new StoreEntity();
	}
	
	public StoreEntity storeEntity()
	{
		return m_storeEntity;
	}

	public boolean storeInit()
	{
        m_storeEntity.reset();
		return sync2File();
	}
	
	public boolean sync2File()
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
            				
            		Element Node_commissionOrder = doc.createElement("commissionOrder");
            		Node_commissionOrder.setAttribute("time", cCommissionOrder.time);
            		Node_commissionOrder.setAttribute("tranAct", tranactVal);
            		Node_commissionOrder.setAttribute("stockID", cCommissionOrder.stockID);
            		Node_commissionOrder.setAttribute("amount", amountVal);
            		Node_commissionOrder.setAttribute("price", priceVal);
            		
            		Node_CommissionOrderList.appendChild(Node_commissionOrder);
            	}
        	}
        	
        	// holdStockList
        	if(null != m_storeEntity.holdStockList)
        	{
        		Element Node_holdStockList=doc.createElement("holdStockList");
            	root.appendChild(Node_holdStockList);
            	for(int i=0;i<m_storeEntity.holdStockList.size();i++)
            	{
            		HoldStock cHoldStock = m_storeEntity.holdStockList.get(i);
  
            		String totalAmount = String.format("%d", cHoldStock.totalAmount);
            		String availableAmount = String.format("%d", cHoldStock.availableAmount);
            		String refPrimeCostPrice =String.format("%.3f", cHoldStock.refPrimeCostPrice);
            		String curPrice =String.format("%.3f", cHoldStock.curPrice);
            				
            		Element Node_holdStock = doc.createElement("holdStock");
            		Node_holdStock.setAttribute("createDate", cHoldStock.createDate);
            		Node_holdStock.setAttribute("stockID", cHoldStock.stockID);
            		Node_holdStock.setAttribute("totalAmount", totalAmount);
            		Node_holdStock.setAttribute("availableAmount", availableAmount);
            		Node_holdStock.setAttribute("refPrimeCostPrice", refPrimeCostPrice);
            		Node_holdStock.setAttribute("curPrice", curPrice);
            		
            		Node_holdStockList.appendChild(Node_holdStock);
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
	
	public boolean sync2Mem()
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
		    
        	// date time
		    String accDate = rootElement.getAttribute("date");
		    String accTime = rootElement.getAttribute("time");

		    // 委托单加载
		    List<CommissionOrder> commissionOrderList = new ArrayList<CommissionOrder>();
		    {
		    	NodeList nodelist_CommissionOrderList = rootElement.getElementsByTagName("CommissionOrderList");
		        if(nodelist_CommissionOrderList.getLength() == 1)
	        	{
		        	Node Node_CommissionOrderList = nodelist_CommissionOrderList.item(0);
		        	NodeList nodelist_commissionOrder = Node_CommissionOrderList.getChildNodes();
			        for (int i = 0; i < nodelist_commissionOrder.getLength(); i++) {
			        	Node node_commissionOrder = nodelist_commissionOrder.item(i);
			        	if(node_commissionOrder.getNodeType() == Node.ELEMENT_NODE)
			        	{
			        		String time = ((Element)node_commissionOrder).getAttribute("time");
				        	String tranAct = ((Element)node_commissionOrder).getAttribute("tranAct");
				        	String stockID = ((Element)node_commissionOrder).getAttribute("stockID");
				        	String amount = ((Element)node_commissionOrder).getAttribute("amount");
				        	String price = ((Element)node_commissionOrder).getAttribute("price");
				        	
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
		    
		    // 委托单加载
		    List<HoldStock> holdStockList = new ArrayList<HoldStock>();
		    {
		    	NodeList nodelist_holdStockList = rootElement.getElementsByTagName("holdStockList");
		        if(nodelist_holdStockList.getLength() == 1)
	        	{
		        	Node Node_holdStockList = nodelist_holdStockList.item(0);
		        	NodeList nodelist_holdStock = Node_holdStockList.getChildNodes();
			        for (int i = 0; i < nodelist_holdStock.getLength(); i++) {
			        	Node node_holdStock = nodelist_holdStock.item(i);
			        	if(node_holdStock.getNodeType() == Node.ELEMENT_NODE)
			        	{
			        		String createDate = ((Element)node_holdStock).getAttribute("createDate");
				        	String stockID = ((Element)node_holdStock).getAttribute("stockID");
				        	String totalAmount = ((Element)node_holdStock).getAttribute("totalAmount");
				        	String availableAmount = ((Element)node_holdStock).getAttribute("availableAmount");
				        	String refPrimeCostPrice = ((Element)node_holdStock).getAttribute("refPrimeCostPrice");
				        	String curPrice = ((Element)node_holdStock).getAttribute("curPrice");
				        	
				        	HoldStock cHoldStock = new HoldStock();
				        	cHoldStock.createDate = createDate;
				        	cHoldStock.stockID = stockID;
				        	cHoldStock.totalAmount = Integer.parseInt(totalAmount);
				        	cHoldStock.availableAmount = Integer.parseInt(availableAmount);
				        	cHoldStock.refPrimeCostPrice = Float.parseFloat(refPrimeCostPrice);
				        	cHoldStock.curPrice = Float.parseFloat(curPrice);
				        	holdStockList.add(cHoldStock);
			        	}
			        }
	        	}
		    }
		    
		    m_storeEntity.date = accDate;
		    m_storeEntity.time = accTime;
		    m_storeEntity.commissionOrderList = commissionOrderList;
		    m_storeEntity.holdStockList = holdStockList;
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
	private static String s_accountDataRoot = "rw\\account";
	
	private String m_accountID;
	private String m_accXMLFile;
	private StoreEntity m_storeEntity;
}
