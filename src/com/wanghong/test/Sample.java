package com.wanghong.test;

/**
 * 样本类，代表训练集或者测试集中的一条记录
 * 
 * @author WangHong
 * 
 */
public class Sample {
	/**
	 * 样本的编号
	 */
	private int index;
	/**
	 * 样本的特征维度集合
	 */
	private int[] D;
	/**
	 * 样本的标签
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
