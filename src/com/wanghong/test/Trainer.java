package com.wanghong.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * 训练器：通过训练集生成一个决策系统
 * 
 * @author WangHong
 * 
 */
public class Trainer {
	/**
	 * 训练集
	 */
	private Sample[] AMLALL_train;
	/**
	 * 分类器，用于学习演化时使用
	 */
	private Sorter sorter;
	/**
	 * 用于控制替代超边的数目
	 */
	private double w;
	/**
	 * 分类不正确的比例
	 */
	private double r;
	/**
	 * 存放分类正确的样本
	 */
	private Set<Sample> X_C;
	/**
	 * 存放分类错误的样本
	 */
	private Set<Sample> X_W;
	/**
	 * 每次迭代过程中被替换的最大超边数
	 */
	private int maxSubstCnt;
	/**
	 * 每次迭代中要被替换的超边数
	 */
	private int substCnt;
	/**
	 * 超边库
	 */
	private Set<Hyperedge> L;
	/**
	 * 用于生成伪随机数
	 */
	private Random random;

	/**
	 * 必须为训练器输入一个训练集
	 * 
	 * @param aMLALL_train
	 *            用作训练的训练集
	 */
	public Trainer(Sample[] aMLALL_train) {
		super();
		AMLALL_train = aMLALL_train;
	}

	/**
	 * 执行初始化的一些工作
	 * 
	 */
	private void init() {
		this.setW(Constant.w);
		this.setX_C(new HashSet<Sample>());
		this.setX_W(new HashSet<Sample>());
		this.setMaxSubstCnt(500);
		this.setSubstCnt(500);
		this.setL(new HashSet<Hyperedge>());
		this.random = new Random();
		for (int i = 0; i < this.AMLALL_train.length; i++) {
			this.getL()
					.addAll(this.getHyperedgesBySample(AMLALL_train[i], 100));
		}

	}

	/**
	 * 根据训练集启动训练，返回一个经过训练算法生成的超网络决策模型
	 * 
	 * @return 一个由超边构成的数组，代表经演化学习后的超网络
	 */
	public Hyperedge[] startTrain() {
		this.init();
		//记录学习次数
		int i=0;
		while (this.getSubstCnt() != 0) {
			this.train();
			i++;
		}
		System.out.println("*************************************************");
		System.out.println("本次演化学学习的超边替代次数为："+i);
		System.out.println("*************************************************");
		return this.L.toArray(new Hyperedge[0]);
	}

	/**
	 * 用超边库L对训练样本分类， 然后将分类正确的样本加入到集合X_C中， 分类不正确的样本加入到集合X_W中，
	 * 计算分类不正确的比例r=|X_W|/|X|
	 */
	private void sort() {
		// 设置分类器为最新的超边集合生成的分类器
		this.setSorter(new Sorter(L.toArray(new Hyperedge[0])));
		// 通过分类器将样本分为分类正确和分类错误两类，并且分别加入分类正确和分类错误的超边集合
		for (int i = 0; i < this.AMLALL_train.length; i++) {
			if (this.getSorter().sort_train(this.AMLALL_train[i])
					.equals(this.AMLALL_train[i].getFlag())) {
				this.getX_C().add(this.AMLALL_train[i]);
			} else {
				this.getX_W().add(this.AMLALL_train[i]);
			}
		}
		// 重新计算分类不正确的比例
		this.setR(this.getX_W().size() * 1.0
				/ (this.getX_C().size() + this.getX_W().size()));
	}

	/**
	 * 判断输入的超边能否正确分类输入的样本
	 * 
	 * @param hyperedge
	 *            超边
	 * @param sample
	 *            样本
	 * @return true：可以正确分类 false:不能正确分类
	 */
	private boolean isTrueSort(Hyperedge hyperedge, Sample sample) {
		// 正确分类的条件是匹配并且标志相同
		return (this.sorter.match(hyperedge, sample))
				&& (hyperedge.getFlag().equals(sample.getFlag())&&(hyperedge.getSampleNum()!=sample.getIndex()));
	}

	/**
	 * 对于超网络的每条超边计算它对于X_W和X_C中的样本的适应值
	 */
	private void computeFitValue() {
		// 获得超边库的迭代器，遍历所有超边
		Iterator<Hyperedge> iterator = this.getL().iterator();
		while (iterator.hasNext()) {
			Hyperedge hyperedge = (Hyperedge) iterator.next();
			// 获得分类正确样本集合和分类错误样本集合的迭代器
			Iterator<Sample> iterator_C = this.getX_C().iterator();
			Iterator<Sample> iterator_W = this.getX_W().iterator();
			// 让超边分类分类错误的样本，计算每个超边的分类错误适应值
			while (iterator_W.hasNext()) {
				Sample sample = (Sample) iterator_W.next();
				if (this.isTrueSort(hyperedge, sample)) {
					hyperedge.setFit_w(hyperedge.getFit_w() + Constant.Y - 1);
				} else {
					hyperedge.setFit_w(hyperedge.getFit_w() - 1);
				}
			}
			// 让超边分类分类正确的样本，计算每个超边的分类正确适应值
			while (iterator_C.hasNext()) {
				Sample sample = (Sample) iterator_C.next();
				if (this.isTrueSort(hyperedge, sample)) {
					hyperedge.setFit_c(hyperedge.getFit_c() + Constant.Y - 1);
				} else {
					hyperedge.setFit_c(hyperedge.getFit_c() - 1);
				}
			}
		}
	}

	/**
	 * 判断两个传入的超边的适应值大小
	 * 
	 * @param h1
	 *            超边1
	 * @param h2
	 *            超边2
	 * @return 1>2：正数 1<2：负数 1==2：零
	 */
	public static int compareHyperedgeByFit(Hyperedge h1, Hyperedge h2) {
		// 计算第一个超边减去第二个超边的错误分类适应值之差
		int fit_w = h1.getFit_w() - h2.getFit_w();
		// 计算第一个超边减去第二个超边的正确分类适应值之差
		int fit_c = h1.getFit_c() - h2.getFit_c();
		// 返回结果
		return fit_w == 0 ? fit_c : fit_w;
	}

	/**
	 * 对所有超边按照fit_w和fit_c降序排序,并执行替换
	 */
	private void rankAndReplace() {
		// 得到超边集合的一个引用
		Set<Hyperedge> LCopy = this.getL();
		// 定义一个超边数组并将超边集合中的所有超边导入数组中，方便排序
		Hyperedge[] hyperedges = new Hyperedge[this.getL().size()];
		Iterator<Hyperedge> iterator = LCopy.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Hyperedge hyperedge = (Hyperedge) iterator.next();
			hyperedges[i] = hyperedge;
			i++;
		}
		// 将数组中的超边按适应值排序排序
		for (int j = 0; j < hyperedges.length - 1; j++) {
			for (int j2 = j + 1; j2 < hyperedges.length; j2++) {
				if (Trainer.compareHyperedgeByFit(hyperedges[j], hyperedges[j2]) < 0) {
					Hyperedge temp = hyperedges[j];
					hyperedges[j] = hyperedges[j2];
					hyperedges[j2] = temp;
				}
			}
		}
		// 计算替换的边数
		this.setSubstCnt((int) (this.getW() * this.getR() * this.getL().size()));
		if (this.getSubstCnt() > this.getMaxSubstCnt()) {
			this.setSubstCnt(this.getMaxSubstCnt());
			this.setMaxSubstCnt((int) (this.getSubstCnt() * 0.8));
		}
		// 替换
		for (int j = hyperedges.length - this.getSubstCnt(); j < hyperedges.length; j++) {
			Hyperedge newHyp = this.randomReturnAHyperedge();
			if (this.getAMLALL_train()[hyperedges[j].getSampleNum()].getFlag()
					.equals(newHyp.getFlag())) {
				if (LCopy.add(newHyp)) {
					LCopy.remove(hyperedges[j]);

				} else {
					j--;
				}
			} else {
				j--;
			}

		}
	}

	/**
	 * 随机返回一个超边
	 */
	private Hyperedge randomReturnAHyperedge() {
		// 随机选的一个样本
		int num = Math.abs(random.nextInt() % this.getAMLALL_train().length);
		return this.getAHyperedgeBySample(this.getAMLALL_train()[num]);
	}

	/**
	 * 执行训练算法
	 */
	private void train() {
		this.setX_C(new HashSet<Sample>());
		this.setX_W(new HashSet<Sample>());
		this.sort();
		this.computeFitValue();
		this.rankAndReplace();
	}

	/**
	 * 生成对于指定样本的指定数目的超边集合
	 * 
	 * @param sample
	 *            指定的样本
	 * @param num
	 *            指定的生成数目
	 * @return 包含生成的超边的数组
	 */
	private Set<Hyperedge> getHyperedgesBySample(Sample sample, int num) {
		// 定义一个超边集合
		Set<Hyperedge> hyperedges = new HashSet<Hyperedge>();
		// 循环调用方法getAHyperedgeBySample得到一个超边并加入超边集合，如果得到的超边和集合中的超边相同则重新生成一个超边并加入
		for (int i = 0; i < num; i++) {
			if (hyperedges.add(this.getAHyperedgeBySample(sample))) {
				continue;
			}
			i--;
		}
		// 返回该超边集合
		return hyperedges;
	}

	/**
	 * 由输入的样本返回该样本生成的一个超边
	 * 
	 * @param sample
	 *            指定的样本
	 * @return 生成的超边
	 */
	private Hyperedge getAHyperedgeBySample(Sample sample) {
		// 生成一个新的超边
		Hyperedge hyperedge = new Hyperedge(sample.getIndex());
		// 初始化该超边的适应值为0
		hyperedge.setFit_c(0);
		hyperedge.setFit_w(0);
		// 设置该超边的标志和生成该超边的样本标志一致
		hyperedge.setFlag(sample.getFlag());
		// 定义一个整型数组记录超边的点序号集合
		int[] nodes = new int[Constant.k];
		// 随机为超边生成k个不重复的点序号，k为选的的超边维度
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = Math.abs(random.nextInt() % Constant.D_NUM);
			for (int j = 0; j < i; j++) {
				if (nodes[j] == nodes[i]) {
					i--;
					break;
				}
			}
		}
		// 将生成的点序号和对应的值加入超边
		for (int i = 0; i < nodes.length; i++) {
			hyperedge.addNode(nodes[i], sample.getD()[nodes[i]]);
		}
		// 返回该超边
		return hyperedge;

	}

	// get and set method
	public Sample[] getAMLALL_train() {
		return AMLALL_train;
	}

	public double getW() {
		return w;
	}

	public void setW(double w) {
		this.w = w;
	}

	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
	}

	public Sorter getSorter() {
		return sorter;
	}

	public void setSorter(Sorter sorter) {
		this.sorter = sorter;
	}

	public Set<Sample> getX_C() {
		return X_C;
	}

	public void setX_C(Set<Sample> x_C) {
		X_C = x_C;
	}

	public Set<Sample> getX_W() {
		return X_W;
	}

	public void setX_W(Set<Sample> x_W) {
		X_W = x_W;
	}

	public void setAMLALL_train(Sample[] aMLALL_train) {
		AMLALL_train = aMLALL_train;
	}

	public int getMaxSubstCnt() {
		return maxSubstCnt;
	}

	public void setMaxSubstCnt(int maxSubstCnt) {
		this.maxSubstCnt = maxSubstCnt;
	}

	public int getSubstCnt() {
		return substCnt;
	}

	public void setSubstCnt(int substCnt) {
		this.substCnt = substCnt;
	}

	public Set<Hyperedge> getL() {
		return L;
	}

	public void setL(Set<Hyperedge> l) {
		L = l;
	}

}
