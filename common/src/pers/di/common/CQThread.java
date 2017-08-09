package pers.di.common;

import java.util.ArrayList;
import java.util.List;

public class CQThread {
	
	abstract public static class CQThreadRequest
	{
		abstract public void doAction();
	}
	
	public CQThread()
	{
		m_thread = new CQThreadEntity();
	}
	public boolean postRequest(CQThreadRequest cReq)
	{
		if(m_thread.checkRunning())
		{
			return m_thread.postRequest(cReq);
		}
		return false;
	}
	
	public boolean startThread()
	{
		return m_thread.startThread();
	}
	
	public boolean stopThread()
	{
		return m_thread.stopThread();
	}
	
	
	private static class CQThreadEntity extends CThread
	{
		public CQThreadEntity()
		{
			m_requestList = new ArrayList<CQThreadRequest>();
			m_syncObj = new CSyncObj();
		}
		@Override
		public void run() {
			while(!super.checkQuit())
			{
				while(true)
				{
					CQThreadRequest cReq = popRequest();
					if(null == cReq) break;
					cReq.doAction();
				}
				super.Wait(Long.MAX_VALUE);
			}
		}
		public boolean postRequest(CQThreadRequest cReq)
		{
			m_syncObj.Lock();
			m_requestList.add(cReq);
			m_syncObj.UnLock();
			super.Notify();
			return true;
		}
		public CQThreadRequest popRequest()
		{
			CQThreadRequest cReq = null;
			m_syncObj.Lock();
			if(m_requestList.size()>0)
			{
				cReq = m_requestList.get(0);
				m_requestList.remove(0);
			}
			m_syncObj.UnLock();
			return cReq;
		}
		private List<CQThreadRequest> m_requestList;
		private CSyncObj m_syncObj;
	}
	
	private CQThreadEntity m_thread;
}
