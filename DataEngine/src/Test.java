import base.BLog;
import base.BUtilsXML;

public class Test {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String xmlStr = "<a><b></b></a>";
		String xmlStrFmt = BUtilsXML.format(xmlStr);
		
		BLog.output("TEST", "%s", xmlStrFmt);
	}

}
