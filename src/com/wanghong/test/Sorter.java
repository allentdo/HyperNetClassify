package com.wanghong.test;

import java.util.HashMap;
import java.util.Iterator;

/**
 * ��������
 * 
 * @author WangHong
 * 
 */
public class Sorter {
	/**
	 * ���ڷ���ĳ��߿�
	 */
	private Hyperedge[] L;

	/**
	 * ����Ϊ����������һ�����߿����ھ��߲������������
	 * 
	 * @param l
	 *            ����ĳ��߿�
	 */
	public Sorter(Hyperedge[] l) {
		super();
		L = l;
	}

	/**
	 * �������������ࣨ�˷�������ѵ��ʱʹ�ã�
	 * 
	 * @param sample
	 *            Ҫ���з��������
	 * @return ����������0����1��
	 */
	public String sort_train(Sample sample) {
		// ��¼�������ƥ��ĳ�����
		int matchNum = 0;
		// ��¼ƥ��ĳ������б�־Ϊ��0���ĳ�����
		int zeroNum = 0;
		// �����߿��е����г��ߺ�����ƥ�䣬����¼�������ƥ��ĳ���������ĳ������б�־Ϊ��0���ĳ�����
		for (int i = 0; i < L.length; i++) {
			if (this.match(L[i], sample)&&(L[i].getSampleNum()!=sample.getIndex())) {
				matchNum++;
				if (L[i].getFlag().equals("0")) {
					zeroNum++;
				}
			}
		}
		// ���ط�����
		return (matchNum - zeroNum) >= zeroNum ? "1" : "0";

	}

	/**
	 * �������������ࣨ�˷�������ѵ��ʱʹ�ã�
	 * 
	 * @param sample
	 *            Ҫ���з��������
	 * @return ����������0����1��
	 */
	public String sort_test(Sample sample) {
		// ��¼�������ƥ��ĳ�����
		int matchNum = 0;
		// ��¼ƥ��ĳ������б�־Ϊ��0���ĳ�����
		int zeroNum = 0;
		// �����߿��е����г��ߺ�����ƥ�䣬����¼�������ƥ��ĳ���������ĳ������б�־Ϊ��0���ĳ�����
		for (int i = 0; i < L.length; i++) {
			if (this.match(L[i], sample)) {
				matchNum++;
				if (L[i].getFlag().equals("0")) {
					zeroNum++;
				}
			}
		}
		// ���ط�����
		return (matchNum - zeroNum) >= zeroNum ? "1" : "0";

	}
	
	/**
	 * �ж�����ĳ��ߺ������Ƿ�ƥ��
	 * 
	 * @param hyperedge
	 *            ����
	 * @param sample
	 *            ����
	 * @return ƥ�����true������Ϊfalse
	 */
	public boolean match(Hyperedge hyperedge, Sample sample) {
		// ��¼ƥ����
		boolean isMatch = true;
		// �õ����ߵĵ㼯��
		HashMap<Integer, Integer> hashMap = (HashMap<Integer, Integer>) hyperedge
				.getNodeMap();
		Iterator<Integer> iterator = hashMap.keySet().iterator();
		// �õ�������ά�ȼ���
		int[] D = sample.getD();
		// �����ߵ����е㼯�������ж�Ӧ�����ݱȽϣ����ȫ����ȷ���true�����򷵻�false
		while (iterator.hasNext()) {
			int index = iterator.next();
			if (hashMap.get(index).intValue() != D[index]) {
				isMatch = false;
			}
		}
		// ���ؽ��
		return isMatch;
	}
}
