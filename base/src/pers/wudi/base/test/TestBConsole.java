package pers.wudi.base.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pers.wudi.base.*;

public class TestBConsole {
	public static class TestConsole extends BConsole 
	{
		public void command(String cmd) 
		{
			BLog.output("TEST", "command:%s\n", cmd);
		}
		
		@Override
		protected void finalize()
		{
			BLog.output("TEST", "finalize\n");
		}
	}
	public static void main(String[] args) {
		BLog.output("TEST", "Test TestBConsole begin\n");
		
		TestConsole cTestConsole = new TestConsole();
		cTestConsole.startThread();
		
		BThread.sleep(1);
		BLog.output("TEST", "XXX\n");
		
		cTestConsole.stopThread();
		
		BLog.output("TEST", "Test TestBConsole end\n");
	}
}
