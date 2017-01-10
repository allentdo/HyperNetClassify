package com.wanghong.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omg.CORBA.INTERNAL;

/**
 * ������
 * 
 * @author WangHong
 * 
 */
public class Hyperedge {
	/**
	 * ���ɸó��ߵ��������
	 */
	private int sampleNum;
	/**
	 * ���ߵ����
	 */
	private String flag;
	/**
	 * �˼��ϴ�ų������ӵĶ�����ż���ֵ
	 */
	private Map<Integer, Integer> nodeMap;
	/**
	 * ���ߵĲ�����ȷ������������Ӧֵ
	 */
	private int fit_w;
	/**
	 * ���ߵ�����ȷ������������Ӧֵ
	 */
	private int fit_c;

	/**
	 * ���췽������ʼ���������ӵĵ㼯Ϊ�ռ�����ȷ����ʹ��������Ӧֵ��Ϊ0�����Ϊnull
	 * 
	 * @param sampleNum
	 *            ���ɸó��ߵ��������
	 */
	public Hyperedge(int sampleNum) {
		super();
		// ��ʼ��
		this.setSampleNum(sampleNum);
		this.setNodeMap(new HashMap());
		this.setFit_w(0);
		this.setFit_c(0);
		this.setFlag(null);
	}

	/**
	 * 
	 * @param nodes
	 *            �����ĳ������ӵĵ㼯��
	 * @param fit_w
	 *            �����ĳ��ߵķ��������Ӧֵ
	 * @param fit_c
	 *            �����ĳ��ߵķ�����ȷ��Ӧֵ
	 * @param flag
	 *            �����ĳ��ߵı��
	 */
	public Hyperedge(int sampleNum, Map<Integer, Integer> nodes, int fit_w,
			int fit_c, String flag) {
		super();
		// ���ݴ��������������
		this.setSampleNum(sampleNum);
		this.setNodeMap(nodes);
		this.setFit_w(fit_w);
		this.setFit_c(fit_c);
		this.setFlag(flag);
	}

	// ��дequal������Ϊ���жϳ����Ƿ��ظ�
	@Override
	public boolean equals(Object obj) {
		// ������߶�Ӧ��������Ų�ͬ����ֱ�ӷ��ز����
		if (this.getSampleNum() != ((Hyperedge) obj).getSampleNum()) {
			return false;
		}
		// ��������������ӵĽڵ�ı�ż���
		Integer[] nodes1 = this.getNodeMap().keySet().toArray(new Integer[0]);
		Integer[] nodes2 = ((Hyperedge) obj).getNodeMap().keySet()
				.toArray(new Integer[0]);
		// ѭ���Ƚϣ�һ�������в���ȵĽڵ��ţ��򷵻ز����
		for (int i = 0; i < Constant.k; i++) {
			for (int j = 0; j < Constant.k; j++) {
				if ((j == (Constant.k - 1))
						&& nodes1[i].intValue() != nodes2[j].intValue()) {
					return false;
				}
				if (nodes1[i].intValue() == nodes2[j].intValue()) {
					break;
				}
			}
		}
		// ����ڵ㶼������������Ҳ��ȣ��������
		return true;
	}

	// set and get method
	public Map<Integer, Integer> getNodeMap() {
		return nodeMap;
	}

	public int getSampleNum() {
		return sampleNum;
	}

	public void setSampleNum(int sampleNum) {
		this.sampleNum = sampleNum;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public void setNodeMap(Map<Integer, Integer> nodeMap) {
		this.nodeMap = nodeMap;
	}

	public int getFit_w() {
		return fit_w;
	}

	public void setFit_w(int fit_w) {
		this.fit_w = fit_w;
	}

	public int getFit_c() {
		return fit_c;
	}

	public void setFit_c(int fit_c) {
		this.fit_c = fit_c;
	}

	/**
	 * ��һ�����㵽�������ӵĶ��㼯��
	 * 
	 * @param key
	 *            Ҫ���붥������
	 * @param value
	 *            Ҫ���붥���ֵ
	 * @return �����Ƿ�ɹ�
	 */
	public boolean addNode(int key, int value) {
		return nodeMap.put(key, value) == null;
	}

	/**
	 * ɾ��һ������Ӷ��㼯����
	 * 
	 * @param key
	 *            Ҫɾ����������
	 * @return ɾ���Ƿ�ɹ�
	 */
	public boolean deleteNode(int key) {
		return nodeMap.get(key) == nodeMap.remove(key);
	}

	// ��дtoString����������һЩ������Ϣ
	@Override
	public String toString() {
		String string = "Flag:" + this.getFlag() + " Fit_w:" + this.getFit_w()
				+ " Fit_c:" + this.getFit_c() + " SampleNum:"
				+ this.getSampleNum();
		return string;
	}

}
