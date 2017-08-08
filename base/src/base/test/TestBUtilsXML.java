package base.test;

import base.BLog;
import base.BUtilsXML;

public class TestBUtilsXML {
	public static void main(String[] args) throws Exception {
		String xmlStr = "<a><b></b></a>";
		String xmlStrFmt = BUtilsXML.format(xmlStr);
		
		BLog.output("TEST", "%s", xmlStrFmt);
	}
}
