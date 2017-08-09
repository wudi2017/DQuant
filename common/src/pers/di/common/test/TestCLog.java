package pers.di.common.test;

import java.io.IOException;
import java.nio.file.FileSystems;  
import java.nio.file.Paths;  
import java.nio.file.StandardWatchEventKinds;  
import java.nio.file.WatchEvent;  
import java.nio.file.WatchKey;  
import java.nio.file.WatchService;

import pers.di.common.*;

public class TestCLog {
	public static void main(String[] args) {
		//CLog.config_setLogDir("testlog");
		CLog.config_setLogCfg("config", "log_config.xml");
		CLog.config_setLogFile("output", "default.log");
		CLog.config_setTag("TAG1", true);
		CLog.config_setTag("TAG2", true);
		CLog.config_setTag("TAG3", false);
		CLog.start();
		
		CLog.output("TAG1", "testlog TAG1 string abcdedf1!\n");
		CLog.output("TAG2", "testlog TAG2 string abcdedf2! %d\n", 25);
		CLog.output("TAG3", "testlog TAG2 string abcdedf3!\n");
		CLog.output("TAG4", "testlog TAG3 string abcdedf4!\n");
		CLog.output("TAG5", "testlog TAG3 string abcdedf4!\n");
		CLog.output("TAG6", "testlog TAG3 string abcdedf4!\n");
		
		
		for(int i=0; i< 20; i++)
		{
			CLog.output("TAG1", "testlog TAG1 string abcdedf1!\n");
			CLog.output("TAG2", "testlog TAG2 string abcdedf2! %d\n", 25);
			CLog.output("TAG3", "testlog TAG2 string abcdedf3!\n");
			CLog.output("TAG4", "testlog TAG3 string abcdedf4!\n");
			CLog.output("TAG5", "testlog TAG3 string abcdedf4!\n");
			CLog.output("TAG6", "testlog TAG3 string abcdedf4!\n");
			CThread.sleep(1000);
		}
		
		CLog.stop();
	}
}
