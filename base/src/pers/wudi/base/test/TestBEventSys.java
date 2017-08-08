package pers.wudi.base.test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pers.wudi.base.*;
import pers.wudi.base.BEventSys.*;

public class TestBEventSys {

	public static class Module1
	{
		public Module1()
		{
			cRecever = new EventReceiver("Module1");
			cRecever.Subscribe("BEV_NOTIFYTEST1", this, "recv_notifytest1");
			cRecever.startReceive();
		}
		public void recv_notifytest1(JSONObject jsonObj) {
			BLog.output("TEST", "Module1 RECV notifytest1 jsonObj[%s]\n", jsonObj.toString());
		}
		public EventReceiver cRecever;
	}
	
	public static class Module2
	{
		public Module2()
		{
			cRecever = new EventReceiver("Module2");
			cRecever.Subscribe("BEV_NOTIFYTEST1", this, "recv_notifytest1");
			cRecever.Subscribe("BEV_NOTIFYTEST2", this, "recv_notifytest2");
			cRecever.startReceive();
		}
		public void recv_notifytest1(JSONObject jsonObj) {
			BLog.output("TEST", "Module2 RECV notifytest1 jsonObj[%s]\n", jsonObj.toString());
		}
		public void recv_notifytest2(JSONObject jsonObj) {
			BLog.output("TEST", "Module2 RECV notifytest2 jsonObj[%s]\n", jsonObj.toString());
		}
		public EventReceiver cRecever;
	}
	
	public static void test_eventsys()
	{
		BLog.output("TEST", "TestBEventSys begin\n");
		
		BEventSys.start();
		
		Module1 module1 = new Module1();
		Module2 module2 = new Module2();
		
		for(int i=0; i<1; i++)
		{
			EventSender cSender = new EventSender();
			
			// Send Event BEV_NOTIFYTEST1
			JSONObject jObject1 = new JSONObject("{'name':'BEV_NOTIFYTEST1_data'}");
			cSender.Send("BEV_NOTIFYTEST1", jObject1);
						
			// Send Event BEV_NOTIFYTEST2
			JSONObject jObject2 = new JSONObject("{'name':'BEV_NOTIFYTEST2_data'}");
			cSender.Send("BEV_NOTIFYTEST2", jObject2);	
		}
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		BEventSys.stop();
		
		BLog.output("TEST", "TestBEventSys end\n");
	}
	
	public static void test_protobuf()
	{
//		Notifytest1.notifytest1.Builder builder = Notifytest1.notifytest1.newBuilder(); 
//		builder.setID(1);
//		builder.setData("abc");
//		Notifytest1.notifytest1 cTest1 = builder.build();
//		
//		com.google.protobuf.GeneratedMessage s = cTest1;
//		byte[] buffer= s.toByteArray();
//		BLog.output("TEST", "clsname:%s\n", cTest1.getClass().getName());
//		
//		//--------------------------------------------
//		
//		com.google.protobuf.GeneratedMessage  test = null;
//		
//		String clsname = "dreamstock.fw.event.Notifytest1$notifytest1";
//		Method create = null;
//		try {
//			Class<?> clz= Class.forName(clsname);
//			create = clz.getMethod("parseFrom", byte[].class);
//			test = (com.google.protobuf.GeneratedMessage) create.invoke(null, buffer);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		Notifytest1.notifytest1  testObj  = (Notifytest1.notifytest1) test;
//		BLog.output("TEST", "data = [%s]\n", testObj.getData());
	}
	
	public static void main(String[] args) {
		//BLog.start();
		
		//BLog.config_setTag("TEST", true);
		//BLog.config_setTag("EVENT", true);
		
		//test_protobuf();
		test_eventsys();
		
		//BLog.stop();
		
		JSONObject jObject = new JSONObject("{'name':'x'}");
		System.out.println(jObject.get("name")); 
	}
}
