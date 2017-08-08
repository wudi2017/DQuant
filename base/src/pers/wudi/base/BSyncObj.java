package pers.wudi.base;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BSyncObj
{
	private Lock lock;
	public BSyncObj()
	{
		lock = new ReentrantLock();
	}
	public boolean Lock()
	{
		lock.lock(); 
		return true;
	}
	public boolean UnLock()
	{
		lock.unlock(); 
		return true;
	}
}
	