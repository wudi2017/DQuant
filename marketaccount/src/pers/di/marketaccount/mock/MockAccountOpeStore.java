package pers.di.marketaccount.mock;

import java.io.*;
import java.util.*;

import pers.di.accountengine.common.*;
import pers.di.common.*;

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

public class MockAccountOpeStore {
	
	public static class StoreEntity
	{
		public StoreEntity()
		{
			money = 0.0f;
			holdStockList = new ArrayList<HoldStock>();
		}
		public float money;
		public List<HoldStock> holdStockList;
	}
	
	public MockAccountOpeStore(String accountID, String password)
	{
		m_storeEntity = null;
		m_accountID = accountID;
		m_password = password;
		m_accXMLFile = "rw\\MOCK_ACCOUNT_" + m_accountID + ".xml";
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
		
		// ����Ԫ��
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

		m_storeEntity = new StoreEntity();
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
		
		// ����Ԫ��
		Element root=doc.createElement("account");
		root.setAttribute("ID", m_accountID);
		root.setAttribute("password", m_password);
        doc.appendChild(root);
        
        if(null != m_storeEntity)
        {
        	// money
        	String totalVal = String.format("%.3f", m_storeEntity.money);
        	Element Node_Money=doc.createElement("Money");
        	Node_Money.setAttribute("total", totalVal);
        	root.appendChild(Node_Money);

        	// HoldStockList
        	Element Node_HoldStockList=doc.createElement("HoldStockList");
        	root.appendChild(Node_HoldStockList);
        	for(int i=0;i<m_storeEntity.holdStockList.size();i++)
        	{
        		HoldStock cHoldStock = m_storeEntity.holdStockList.get(i);
        		String totalAmountVal =String.format("%d", cHoldStock.totalAmount);
        		String availableAmountVal =String.format("%d", cHoldStock.availableAmount);
        		String refPrimeCostPriceVal =String.format("%.3f", cHoldStock.refPrimeCostPrice);
        		String curPriceVal =String.format("%.3f", cHoldStock.curPrice);
        				
        		Element Node_Stock = doc.createElement("Stock");
        		Node_Stock.setAttribute("stockID", cHoldStock.stockID);
        		Node_Stock.setAttribute("totalAmount", totalAmountVal);
        		Node_Stock.setAttribute("availableAmount", availableAmountVal);
        		Node_Stock.setAttribute("refPrimeCostPrice", refPrimeCostPriceVal);
        		Node_Stock.setAttribute("curPrice", curPriceVal);
        		Node_HoldStockList.appendChild(Node_Stock);
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

		// �������oriXmlStr
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
		
		// ��ʽ��XmlStr
		String formatedXmlStr = "";
		try {
			formatedXmlStr = CUtilsXML.format(oriXmlStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// ���µ��ļ�
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
		CLog.output("ACCOUNT", "MockAccountOpeStore load\n");
		
		String xmlStr = "";
		File cfile=new File(m_accXMLFile);
		if(!cfile.exists())
		{
			CLog.output("ACCOUNT", "MockAccountOpeStore storeInit (no file)\n");
			return false; // û���ļ� loadʧ��
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
				CLog.output("ACCOUNT", "MockAccountOpeStore storeInit (no content)\n");
				return false; // û������ loadʧ��
			}
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		    DocumentBuilder builder = factory.newDocumentBuilder();
		    StringReader sr = new StringReader(xmlStr);
		    InputSource is = new InputSource(sr);
		    Document doc = builder.parse(is);
		    Element rootElement = doc.getDocumentElement();
		    
		    // ��鷵��������Ч��
		    if(!rootElement.getTagName().contains("account")) 
			{
		    	CLog.output("ACCOUNT", "MockAccountOpeStore storeInit (no account root)\n");
				return false; // û��root loadʧ��
			}
		    
		    // �˻������жϲ�����
		    String accountID = rootElement.getAttribute("ID");
		    String password = rootElement.getAttribute("password");
		    if(!accountID.equals(m_accountID) || !password.equals(m_password))
			{
		    	CLog.error("ACCOUNT", "MockAccountOpeStore storeInit (accountID or password error)\n");
				return false; // �˺����ܲ��� loadʧ��
			}
		    
		    String acc_date = rootElement.getAttribute("date");
		    //CLog.output("ACCOUNT", "accountID:%s password:%s acc_date:%s \n", accountID, password, acc_date);
	
		    // Ǯ����
		    float money = 0.0f;
		    {
		    	NodeList nodelist_Money = rootElement.getElementsByTagName("Money");
		    	if(nodelist_Money.getLength() == 1)
		    	{
		    		Node Node_Money = nodelist_Money.item(0);
		    		String total = ((Element)Node_Money).getAttribute("total");
		    		money = Float.parseFloat(total);
		    	}
		    }

		    // �ֹɼ���
		    List<HoldStock> holdStockList = new ArrayList<HoldStock>();
		    {
		    	NodeList nodelist_HoldStockList = rootElement.getElementsByTagName("HoldStockList");
		        if(nodelist_HoldStockList.getLength() == 1)
	        	{
		        	Node Node_HoldStockList = nodelist_HoldStockList.item(0);
		        	NodeList nodelist_Stock = Node_HoldStockList.getChildNodes();
			        for (int i = 0; i < nodelist_Stock.getLength(); i++) {
			        	Node node_Stock = nodelist_Stock.item(i);
			        	if(node_Stock.getNodeType() == Node.ELEMENT_NODE)
			        	{
			        		String stockID = ((Element)node_Stock).getAttribute("stockID");
				        	String totalAmount = ((Element)node_Stock).getAttribute("totalAmount");
				        	String availableAmount = ((Element)node_Stock).getAttribute("availableAmount");
				        	String refPrimeCostPrice = ((Element)node_Stock).getAttribute("refPrimeCostPrice");
				        	String curPrice = ((Element)node_Stock).getAttribute("curPrice");
				        	String transactionCost = ((Element)node_Stock).getAttribute("transactionCost");
				        	
				        	HoldStock cHoldStock = new HoldStock();
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
		    
		    m_storeEntity.money = money;
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
	 * ��Ա-----------------------------------------------------------------------
	 */
	private StoreEntity m_storeEntity;
	private String m_accountID;
	private String m_password;
	private String m_accXMLFile;
}