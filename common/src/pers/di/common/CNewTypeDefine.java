package pers.di.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CNewTypeDefine {
	
	/*
	 * ��������
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
	 * List�۲���
	 * ���ڷ���list���ڲ�Ϊ���ã�ֻ�ܷ��ʣ����ܸı�
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
		
		// ����������
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
