package pers.di.quantplatform;

public abstract class QuantTrigger {
	/*
	 * �����ս���ʱ��ǰ���� (09:27:00����һ��)
	 * 
	 */
	public abstract void onDayBegin(QuantContext ctx);
	
	/*
	 * �����ս���ʱ��ÿ������������  (����09:30:00 ~ 11:30:00 ����13:00:00 ~ 15:00:00 ÿ���Ӵ���һ��)
	 * 
	 * ע�⣺
	 *     �����״η��ʷ�ʱ���ݵĹ�Ʊ�����뵱��۲����ڣ�֮����Գ������ÿ���ӷ�ʱ���ݣ������۲�������Ч��
	 */
	public abstract void onEveryMinute(QuantContext ctx);
	
	/*
	 * �����ս���ʱ���󴥷� (21:00:00 ����һ��)
	 * 
	 */
	public abstract void onDayEnd(QuantContext ctx);
}
