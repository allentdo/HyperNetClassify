package com.wanghong.test;

/**
 * �����࣬����ѵ�������߲��Լ��е�һ����¼
 * 
 * @author WangHong
 * 
 */
public class Sample {
	/**
	 * �����ı��
	 */
	private int index;
	/**
	 * ����������ά�ȼ���
	 */
	private int[] D;
	/**
	 * �����ı�ǩ
	 */
	private String flag;

	/**
	 * 
	 * @param d
	 * @param flag
	 */
	public Sample(int index, int[] d, String flag) {
		super();
		this.setIndex(index);
		this.setD(d);
		this.setFlag(flag);
	}

	// set and get method
	public int[] getD() {
		return D;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setD(int[] d) {
		D = d;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		String string = "Flag:" + this.getFlag() + " Index:" + this.getIndex();
		return string;
	}
}
