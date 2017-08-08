package pers.wudi.base.test;

import pers.wudi.base.BLog;
import pers.wudi.base.BUtilsXML;

public class TestBUtilsXML {
	public static void main(String[] args) throws Exception {
		String xmlStr = "<a><b></b></a>";
		String xmlStrFmt = BUtilsXML.format(xmlStr);
		
		BLog.output("TEST", "%s", xmlStrFmt);
	}
}
