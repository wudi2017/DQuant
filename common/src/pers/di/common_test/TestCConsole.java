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
		cTestConsole.Start();
		
		CThread.msleep(1000*10);
		
		CLog.output("TEST", "Stop Console\n");
		
		cTestConsole.Stop();
		
		CLog.output("TEST", "Test TestCConsole end\n");
	}
}
