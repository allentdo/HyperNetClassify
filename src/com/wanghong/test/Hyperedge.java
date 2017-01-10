package com.wanghong.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omg.CORBA.INTERNAL;

/**
 * 超边类
 * 
 * @author WangHong
 * 
 */
public class Hyperedge {
	/**
	 * 生成该超边的样本编号
	 */
	private int sampleNum;
	/**
	 * 超边的类别
	 */
	private String flag;
	/**
	 * 此集合存放超边连接的顶点序号及其值
	 */
	private Map<Integer, Integer> nodeMap;
	/**
	 * 超边的不能正确分类样本的适应值
	 */
	private int fit_w;
	/**
	 * 超边的能正确分类样本的适应值
	 */
	private int fit_c;

	/**
	 * 构造方法，初始化超边连接的点集为空集，正确分类和错误分类适应值都为0，标记为null
	 * 
	 * @param sampleNum
	 *            生成该超边的样本编号
	 */
	public Hyperedge(int sampleNum) {
		super();
		// 初始化
		this.setSampleNum(sampleNum);
		this.setNodeMap(new HashMap());
		this.setFit_w(0);
		this.setFit_c(0);
		this.setFlag(null);
	}

	/**
	 * 
	 * @param nodes
	 *            给定的超边连接的点集合
	 * @param fit_w
	 *            给定的超边的分类错误适应值
	 * @param fit_c
	 *            给定的超边的分类正确适应值
	 * @param flag
	 *            给定的超边的标记
	 */
	public Hyperedge(int sampleNum, Map<Integer, Integer> nodes, int fit_w,
			int fit_c, String flag) {
		super();
		// 根据传入参数设置属性
		this.setSampleNum(sampleNum);
		this.setNodeMap(nodes);
		this.setFit_w(fit_w);
		this.setFit_c(fit_c);
		this.setFlag(flag);
	}

	// 重写equal方法，为了判断超边是否重复
	@Override
	public boolean equals(Object obj) {
		// 如果超边对应的样本编号不同，则直接返回不相等
		if (this.getSampleNum() != ((Hyperedge) obj).getSampleNum()) {
			return false;
		}
		// 获得两个超边连接的节点的编号集合
		Integer[] nodes1 = this.getNodeMap().keySet().toArray(new Integer[0]);
		Integer[] nodes2 = ((Hyperedge) obj).getNodeMap().keySet()
				.toArray(new Integer[0]);
		// 循环比较，一旦发现有不相等的节点编号，则返回不相等
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
		// 如果节点都相等且样本编号也相等，返回相等
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
	 * 加一个顶点到超边连接的顶点集合
	 * 
	 * @param key
	 *            要加入顶点的序号
	 * @param value
	 *            要加入顶点的值
	 * @return 加入是否成功
	 */
	public boolean addNode(int key, int value) {
		return nodeMap.put(key, value) == null;
	}

	/**
	 * 删除一个顶点从顶点集合中
	 * 
	 * @param key
	 *            要删除顶点的序号
	 * @return 删除是否成功
	 */
	public boolean deleteNode(int key) {
		return nodeMap.get(key) == nodeMap.remove(key);
	}

	// 重写toString方法，返回一些基本信息
	@Override
	public String toString() {
		String string = "Flag:" + this.getFlag() + " Fit_w:" + this.getFit_w()
				+ " Fit_c:" + this.getFit_c() + " SampleNum:"
				+ this.getSampleNum();
		return string;
	}

}
