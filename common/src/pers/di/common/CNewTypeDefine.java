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
			return m_listContainer.get(m_iBase+i);
		}
		
		public int size() {
			// TODO Auto-generated method stub
			return m_iSize;
		}
		
		// 构建访问器
		public void build(List<T> origin, int iBase, int iSize)
		{
			m_listContainer = origin;
			m_iBase = iBase;
			m_iSize= iSize;
		}
		
		private List<T> m_listContainer;
		private int m_iBase;
		private int m_iSize;
	}
}
