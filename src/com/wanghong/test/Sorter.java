package com.wanghong.test;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 分类器，
 * 
 * @author WangHong
 * 
 */
public class Sorter {
	/**
	 * 用于分类的超边库
	 */
	private Hyperedge[] L;

	/**
	 * 必须为分类器输入一个超边库用于决策测试样本的类别
	 * 
	 * @param l
	 *            输入的超边库
	 */
	public Sorter(Hyperedge[] l) {
		super();
		L = l;
	}

	/**
	 * 将传入样本分类（此方法用于训练时使用）
	 * 
	 * @param sample
	 *            要进行分类的样本
	 * @return 分类结果，“0”或“1”
	 */
	public String sort_train(Sample sample) {
		// 记录与该样本匹配的超边数
		int matchNum = 0;
		// 记录匹配的超边数中标志为“0”的超边数
		int zeroNum = 0;
		// 将超边库中的所有超边和样本匹配，并记录与该样本匹配的超边数和配的超边数中标志为“0”的超边数
		for (int i = 0; i < L.length; i++) {
			if (this.match(L[i], sample)&&(L[i].getSampleNum()!=sample.getIndex())) {
				matchNum++;
				if (L[i].getFlag().equals("0")) {
					zeroNum++;
				}
			}
		}
		// 返回分类结果
		return (matchNum - zeroNum) >= zeroNum ? "1" : "0";

	}

	/**
	 * 将传入样本分类（此方法用于训练时使用）
	 * 
	 * @param sample
	 *            要进行分类的样本
	 * @return 分类结果，“0”或“1”
	 */
	public String sort_test(Sample sample) {
		// 记录与该样本匹配的超边数
		int matchNum = 0;
		// 记录匹配的超边数中标志为“0”的超边数
		int zeroNum = 0;
		// 将超边库中的所有超边和样本匹配，并记录与该样本匹配的超边数和配的超边数中标志为“0”的超边数
		for (int i = 0; i < L.length; i++) {
			if (this.match(L[i], sample)) {
				matchNum++;
				if (L[i].getFlag().equals("0")) {
					zeroNum++;
				}
			}
		}
		// 返回分类结果
		return (matchNum - zeroNum) >= zeroNum ? "1" : "0";

	}
	
	/**
	 * 判断输入的超边和样本是否匹配
	 * 
	 * @param hyperedge
	 *            超边
	 * @param sample
	 *            样本
	 * @return 匹配输出true，否则为false
	 */
	public boolean match(Hyperedge hyperedge, Sample sample) {
		// 记录匹配结果
		boolean isMatch = true;
		// 得到超边的点集合
		HashMap<Integer, Integer> hashMap = (HashMap<Integer, Integer>) hyperedge
				.getNodeMap();
		Iterator<Integer> iterator = hashMap.keySet().iterator();
		// 得到样本的维度集合
		int[] D = sample.getD();
		// 将超边的所有点集与样本中对应的数据比较，如果全部相等返回true，否则返回false
		while (iterator.hasNext()) {
			int index = iterator.next();
			if (hashMap.get(index).intValue() != D[index]) {
				isMatch = false;
			}
		}
		// 返回结果
		return isMatch;
	}
}
