package pers.di.common_test;

import pers.di.common.CLog;
import pers.di.common.CUtilsXML;

public class TestCUtilsXML {
	public static void main(String[] args) throws Exception {
		String xmlStr = "<a><b></b></a>";
		String xmlStrFmt = CUtilsXML.format(xmlStr);
		
		CLog.debug("TEST", "%s", xmlStrFmt);
	}
}
