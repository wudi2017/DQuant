package pers.di.common;

import java.util.ArrayList;
import java.util.List;

public class CTypeDefine {
	
	public static class ObjContainer<T>
	{
		public ObjContainer()
		{
			m_obj = null;
		}
		public void set(T obj)
		{
			m_obj = obj;
		}
		public T get()
		{
			return m_obj;
		}
		public T move()
		{
			T tmp = m_obj;
			m_obj = null;
			return tmp;
		}
		private T m_obj;
	}
}
