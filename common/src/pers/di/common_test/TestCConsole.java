package pers.di.common_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pers.di.common.*;

public class TestCConsole {
	public static class TestConsole extends CConsole 
	{
		public void command(String cmd) 
		{
			CLog.output("TEST", "command:%s\n", cmd);
		}
		
		@Override
		protected void finalize()
		{
			CLog.output("TEST", "finalize\n");
		}
	}
	public static void main(String[] args) {
		CLog.output("TEST", "Test TestCConsole begin\n");
		
		TestConsole cTestConsole = new TestConsole();
		cTestConsole.startThread();
		
		CThread.sleep(1);
		CLog.output("TEST", "XXX\n");
		
		cTestConsole.stopThread();
		
		CLog.output("TEST", "Test TestCConsole end\n");
	}
}
