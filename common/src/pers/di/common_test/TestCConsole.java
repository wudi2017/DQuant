package pers.di.common_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import pers.di.common.*;

public class TestCConsole {
	public static class TestConsole implements CConsole.IHandler
	{
		public void command(String cmd) 
		{
			CLog.output("TEST", "command:%s\n", cmd);
		}
	}
	
	public static void main(String[] args) {
		CLog.output("TEST", "Test TestCConsole begin\n");
		
		CConsole console = new CConsole();
		console.Start(new TestConsole());
		
		CThread.msleep(1000*10);
		
		CLog.output("TEST", "Stop Console\n");
		
		console.Stop();
		
		CLog.output("TEST", "Test TestCConsole end\n");
	}
}
