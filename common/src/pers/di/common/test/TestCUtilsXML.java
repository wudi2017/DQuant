package pers.di.common.test;

import pers.di.common.CLog;
import pers.di.common.CUtilsXML;

public class TestCUtilsXML {
	public static void main(String[] args) throws Exception {
		String xmlStr = "<a><b></b></a>";
		String xmlStrFmt = CUtilsXML.format(xmlStr);
		
		CLog.output("TEST", "%s", xmlStrFmt);
	}
}
