package pers.di.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class CConsole extends CThread {
	
	public CConsole()
	{
//		m_Scanner = new Scanner(System.in);
		m_IStreamReader = new InputStreamReader(System.in);
		m_BufferReader = new BufferedReader(m_IStreamReader);  
	}
	
	// implement by user
	public void command(String cmd) {}
		
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//BLog.output("TEST", "BConsole Run\n");
		while(!checkQuit())
		{
			//BLog.output("TEST", "readDataFromConsole...\n");
			try {  
				if(m_BufferReader.ready())
				{
					String cmd = m_BufferReader.readLine();  
					command(cmd);
				}
				else
				{
					CThread.msleep(300);
				}
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
			
		}
		//BLog.output("TEST", "BConsole Run exit\n");
	} 
	
	public void Start()
	{
		super.startThread();
	}
	
	public void Stop()
	{
		
		try {

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.stopThread();
	}
	
	private String readDataFromConsole() {   
        String str = null;  
        try {  
            str = m_BufferReader.readLine();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        
        return str;  
    }
	
//	private Scanner m_Scanner;
	private InputStreamReader m_IStreamReader;
	private BufferedReader m_BufferReader;
}
