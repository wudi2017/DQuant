package pers.di.quantengine;

public abstract class QuantTriger {
	/*
	 * ��������
	 * 
	 * ע�⣺
	 *     ��ʵʱģʽ�£������״η��ʷ�ʱ���ݵĹ�Ʊ�����뵱��۲����ڣ�֮����Գ���ÿ���ӻ�÷�ʱ���ݣ�������Ч��
	 *     ����ʷ�ز�ģʽ�£����Է��ʱ������еķ��ӷ�ʱ��
	 */
	public abstract void onHandleData(QuantContext ctx);
}
