package pers.di.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CNewTypeDefine {
	
	/*
	 * 对象容器
	 */
	public static class CObjectContainer<T> {
		public CObjectContainer()
		{
			m_object = null;
		}
		public void set(T obj)
		{
			m_object = obj;
		}
		public T get()
		{
			return m_object;
		}
		public T move()
		{
			T tmp = m_object;
			m_object = null;
			return tmp;
		}
		private T m_object;
	}

	/*
	 * 对象观察器
	 * 用于访问对象，内部为引用，只能访问，不能改变
	 */
	public static class CObjectObserver<T> {
		public CObjectObserver()
		{
			m_object = null;
		}
		
		public boolean build(T origin)
		{
			m_object = origin;
			return true;
		}
		public boolean build(CObjectObserver<T> origin)
		{
			m_object = origin.m_object;
			return true;
		}
		
		public T get()
		{
			return m_object;
		}
		private T m_object;
	}
	
	/*
	 * List观察器
	 * 用于访问list，内部为引用，只能访问，不能改变
	 */
	public static class CListObserver<T> {
		
		public CListObserver()
		{
			m_listContainer = null;
			m_iBase = 0;
			m_iSize = 0;
		}

		public T get(int i) {
			// TODO Auto-generated method stub
			if(i < m_iSize)
				return m_listContainer.get(m_iBase+i);
			else
				return null;
		}
		
		public int size() {
			// TODO Auto-generated method stub
			return m_iSize;
		}
		
		// 构建访问器
		public boolean build(List<T> origin, int iBase, int iSize)
		{
			m_listContainer = origin;
			m_iBase = iBase;
			m_iSize= iSize;
			return true;
		}
		public boolean build(List<T> origin)
		{
			return build(origin, 0, origin.size());
		}
		public boolean build(CListObserver<T> origin, int iBase, int iSize)
		{
			if(iBase >= m_iBase && iBase < m_iBase + m_iSize
					&& iBase + iSize < m_iSize)
			{
				m_iBase = m_iBase + iBase;
				m_iSize= iSize;
				return true;
			}
			else
			{
				return false;
			}
		}
		public boolean build(CListObserver<T> origin)
		{
			return build(origin, 0, origin.size());
		}
		
		private List<T> m_listContainer;
		private int m_iBase;
		private int m_iSize;
	}
}
