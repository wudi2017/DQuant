package pers.di.common;

import java.util.*;

/*
 * Least Recently Used
 * �������ʹ�� ��������
 */
public class CLRUMapCache<K, V> {
	
	public CLRUMapCache(int cacheSize)
	{
		m_currentSize = 0;  
		m_cacheSize = cacheSize;  
		m_nodes = new Hashtable<Object, Entry>(cacheSize);//��������  
	}
	
    /** 
     * �ж��Ƿ���key 
     */  
    public boolean containsKey(K key) {  
    	return m_nodes.containsKey(key);
    }  
	
	/** 
     * ��ȡ�����ж���,������������ǰ�� 
     */  
    public V get(K key) {  
        Entry node = m_nodes.get(key);  
        if (node != null) {  
            moveToHead(node);  
            return (V)node.value;  
        } else {  
            return null;  
        }  
    }  
      
    /** 
     * ��� entry��hashtable, ����entry  
     */  
    public void put(K key, V value) {  
        //�Ȳ鿴hashtable�Ƿ���ڸ�entry, ������ڣ���ֻ������value  
        Entry node = m_nodes.get(key);  
          
        if (node == null) {  
            //���������Ƿ��Ѿ�������С.  
            if (m_currentSize >= m_cacheSize) {  
            	m_nodes.remove(m_last.key);  
                removeLast();  
            } else {  
            	m_currentSize++;  
            }             
            node = new Entry();  
        }  
        node.key = key;
        node.value = value;  
        //������ʹ�õĽڵ�ŵ�����ͷ����ʾ����ʹ�õ�.  
        moveToHead(node);  
        m_nodes.put(key, node);  
    }  
    
    /** 
     * ��entryɾ��, ע�⣺ɾ������ֻ����cache���˲Żᱻִ�� 
     */  
    public void remove(K key) {  
        Entry node = m_nodes.get(key);  
        //��������ɾ��  
        if (node != null) {  
            if (node.prev != null) {  
                node.prev.next = node.next;  
            }  
            if (node.next != null) {  
                node.next.prev = node.prev;  
            }  
            if (m_last == node)  
            	m_last = node.prev;  
            if (m_first == node)  
            	m_first = node.next;  
        }  
        //��hashtable��ɾ��  
        m_nodes.remove(key);  
    }  
    
    /**
     * ��ջ��� 
     */  
    public void clear() {  
    	m_first = null;  
    	m_last = null;  
        m_currentSize = 0;  
    }  
    
    /***********************************************************************************/
  
    /** 
     * ɾ������β���ڵ㣬��ʹ����� ʹ�õ�entry 
     */  
    private void removeLast() {  
        //����β��Ϊ��,������βָ��null. ɾ������β��ɾ������ʹ�õĻ������  
        if (m_last != null) {  
            if (m_last.prev != null)  
            	m_last.prev.next = null;  
            else  
            	m_first = null;  
            m_last = m_last.prev;  
        }  
    }  
      
    /** 
     * �ƶ�������ͷ����ʾ����ڵ�������ʹ�ù��� 
     */  
    private void moveToHead(Entry node) {  
        if (node == m_first)  
            return;  
        if (node.prev != null)  
            node.prev.next = node.next;  
        if (node.next != null)  
            node.next.prev = node.prev;  
        if (m_last == node)  
        	m_last = node.prev;  
        if (m_first != null) {  
            node.next = m_first;  
            m_first.prev = node;  
        }  
        m_first = node;  
        node.prev = null;  
        if (m_last == null)  
        	m_last = m_first;  
    }  
  
	class Entry {  
	    Entry prev;//ǰһ�ڵ�  
	    Entry next;//��һ�ڵ�  
	    Object value;//ֵ  
	    Object key;//��  
	}  
	
	private int m_cacheSize;  
    private Hashtable<Object, Entry> m_nodes;//��������  
    private int m_currentSize;  
    private Entry m_first;//����ͷ  
    private Entry m_last;//����β
}
