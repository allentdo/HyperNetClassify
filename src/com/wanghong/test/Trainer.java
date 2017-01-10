package com.wanghong.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * ѵ������ͨ��ѵ��������һ������ϵͳ
 * 
 * @author WangHong
 * 
 */
public class Trainer {
	/**
	 * ѵ����
	 */
	private Sample[] AMLALL_train;
	/**
	 * ������������ѧϰ�ݻ�ʱʹ��
	 */
	private Sorter sorter;
	/**
	 * ���ڿ���������ߵ���Ŀ
	 */
	private double w;
	/**
	 * ���಻��ȷ�ı���
	 */
	private double r;
	/**
	 * ��ŷ�����ȷ������
	 */
	private Set<Sample> X_C;
	/**
	 * ��ŷ�����������
	 */
	private Set<Sample> X_W;
	/**
	 * ÿ�ε��������б��滻����󳬱���
	 */
	private int maxSubstCnt;
	/**
	 * ÿ�ε�����Ҫ���滻�ĳ�����
	 */
	private int substCnt;
	/**
	 * ���߿�
	 */
	private Set<Hyperedge> L;
	/**
	 * ��������α�����
	 */
	private Random random;

	/**
	 * ����Ϊѵ��������һ��ѵ����
	 * 
	 * @param aMLALL_train
	 *            ����ѵ����ѵ����
	 */
	public Trainer(Sample[] aMLALL_train) {
		super();
		AMLALL_train = aMLALL_train;
	}

	/**
	 * ִ�г�ʼ����һЩ����
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
	 * ����ѵ��������ѵ��������һ������ѵ���㷨���ɵĳ��������ģ��
	 * 
	 * @return һ���ɳ��߹��ɵ����飬�����ݻ�ѧϰ��ĳ�����
	 */
	public Hyperedge[] startTrain() {
		this.init();
		//��¼ѧϰ����
		int i=0;
		while (this.getSubstCnt() != 0) {
			this.train();
			i++;
		}
		System.out.println("*************************************************");
		System.out.println("�����ݻ�ѧѧϰ�ĳ����������Ϊ��"+i);
		System.out.println("*************************************************");
		return this.L.toArray(new Hyperedge[0]);
	}

	/**
	 * �ó��߿�L��ѵ���������࣬ Ȼ�󽫷�����ȷ���������뵽����X_C�У� ���಻��ȷ���������뵽����X_W�У�
	 * ������಻��ȷ�ı���r=|X_W|/|X|
	 */
	private void sort() {
		// ���÷�����Ϊ���µĳ��߼������ɵķ�����
		this.setSorter(new Sorter(L.toArray(new Hyperedge[0])));
		// ͨ����������������Ϊ������ȷ�ͷ���������࣬���ҷֱ���������ȷ�ͷ������ĳ��߼���
		for (int i = 0; i < this.AMLALL_train.length; i++) {
			if (this.getSorter().sort_train(this.AMLALL_train[i])
					.equals(this.AMLALL_train[i].getFlag())) {
				this.getX_C().add(this.AMLALL_train[i]);
			} else {
				this.getX_W().add(this.AMLALL_train[i]);
			}
		}
		// ���¼�����಻��ȷ�ı���
		this.setR(this.getX_W().size() * 1.0
				/ (this.getX_C().size() + this.getX_W().size()));
	}

	/**
	 * �ж�����ĳ����ܷ���ȷ�������������
	 * 
	 * @param hyperedge
	 *            ����
	 * @param sample
	 *            ����
	 * @return true��������ȷ���� false:������ȷ����
	 */
	private boolean isTrueSort(Hyperedge hyperedge, Sample sample) {
		// ��ȷ�����������ƥ�䲢�ұ�־��ͬ
		return (this.sorter.match(hyperedge, sample))
				&& (hyperedge.getFlag().equals(sample.getFlag())&&(hyperedge.getSampleNum()!=sample.getIndex()));
	}

	/**
	 * ���ڳ������ÿ�����߼���������X_W��X_C�е���������Ӧֵ
	 */
	private void computeFitValue() {
		// ��ó��߿�ĵ��������������г���
		Iterator<Hyperedge> iterator = this.getL().iterator();
		while (iterator.hasNext()) {
			Hyperedge hyperedge = (Hyperedge) iterator.next();
			// ��÷�����ȷ�������Ϻͷ�������������ϵĵ�����
			Iterator<Sample> iterator_C = this.getX_C().iterator();
			Iterator<Sample> iterator_W = this.getX_W().iterator();
			// �ó��߷��������������������ÿ�����ߵķ��������Ӧֵ
			while (iterator_W.hasNext()) {
				Sample sample = (Sample) iterator_W.next();
				if (this.isTrueSort(hyperedge, sample)) {
					hyperedge.setFit_w(hyperedge.getFit_w() + Constant.Y - 1);
				} else {
					hyperedge.setFit_w(hyperedge.getFit_w() - 1);
				}
			}
			// �ó��߷��������ȷ������������ÿ�����ߵķ�����ȷ��Ӧֵ
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
	 * �ж���������ĳ��ߵ���Ӧֵ��С
	 * 
	 * @param h1
	 *            ����1
	 * @param h2
	 *            ����2
	 * @return 1>2������ 1<2������ 1==2����
	 */
	public static int compareHyperedgeByFit(Hyperedge h1, Hyperedge h2) {
		// �����һ�����߼�ȥ�ڶ������ߵĴ��������Ӧֵ֮��
		int fit_w = h1.getFit_w() - h2.getFit_w();
		// �����һ�����߼�ȥ�ڶ������ߵ���ȷ������Ӧֵ֮��
		int fit_c = h1.getFit_c() - h2.getFit_c();
		// ���ؽ��
		return fit_w == 0 ? fit_c : fit_w;
	}

	/**
	 * �����г��߰���fit_w��fit_c��������,��ִ���滻
	 */
	private void rankAndReplace() {
		// �õ����߼��ϵ�һ������
		Set<Hyperedge> LCopy = this.getL();
		// ����һ���������鲢�����߼����е����г��ߵ��������У���������
		Hyperedge[] hyperedges = new Hyperedge[this.getL().size()];
		Iterator<Hyperedge> iterator = LCopy.iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Hyperedge hyperedge = (Hyperedge) iterator.next();
			hyperedges[i] = hyperedge;
			i++;
		}
		// �������еĳ��߰���Ӧֵ��������
		for (int j = 0; j < hyperedges.length - 1; j++) {
			for (int j2 = j + 1; j2 < hyperedges.length; j2++) {
				if (Trainer.compareHyperedgeByFit(hyperedges[j], hyperedges[j2]) < 0) {
					Hyperedge temp = hyperedges[j];
					hyperedges[j] = hyperedges[j2];
					hyperedges[j2] = temp;
				}
			}
		}
		// �����滻�ı���
		this.setSubstCnt((int) (this.getW() * this.getR() * this.getL().size()));
		if (this.getSubstCnt() > this.getMaxSubstCnt()) {
			this.setSubstCnt(this.getMaxSubstCnt());
			this.setMaxSubstCnt((int) (this.getSubstCnt() * 0.8));
		}
		// �滻
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
	 * �������һ������
	 */
	private Hyperedge randomReturnAHyperedge() {
		// ���ѡ��һ������
		int num = Math.abs(random.nextInt() % this.getAMLALL_train().length);
		return this.getAHyperedgeBySample(this.getAMLALL_train()[num]);
	}

	/**
	 * ִ��ѵ���㷨
	 */
	private void train() {
		this.setX_C(new HashSet<Sample>());
		this.setX_W(new HashSet<Sample>());
		this.sort();
		this.computeFitValue();
		this.rankAndReplace();
	}

	/**
	 * ���ɶ���ָ��������ָ����Ŀ�ĳ��߼���
	 * 
	 * @param sample
	 *            ָ��������
	 * @param num
	 *            ָ����������Ŀ
	 * @return �������ɵĳ��ߵ�����
	 */
	private Set<Hyperedge> getHyperedgesBySample(Sample sample, int num) {
		// ����һ�����߼���
		Set<Hyperedge> hyperedges = new HashSet<Hyperedge>();
		// ѭ�����÷���getAHyperedgeBySample�õ�һ�����߲����볬�߼��ϣ�����õ��ĳ��ߺͼ����еĳ�����ͬ����������һ�����߲�����
		for (int i = 0; i < num; i++) {
			if (hyperedges.add(this.getAHyperedgeBySample(sample))) {
				continue;
			}
			i--;
		}
		// ���ظó��߼���
		return hyperedges;
	}

	/**
	 * ��������������ظ��������ɵ�һ������
	 * 
	 * @param sample
	 *            ָ��������
	 * @return ���ɵĳ���
	 */
	private Hyperedge getAHyperedgeBySample(Sample sample) {
		// ����һ���µĳ���
		Hyperedge hyperedge = new Hyperedge(sample.getIndex());
		// ��ʼ���ó��ߵ���ӦֵΪ0
		hyperedge.setFit_c(0);
		hyperedge.setFit_w(0);
		// ���øó��ߵı�־�����ɸó��ߵ�������־һ��
		hyperedge.setFlag(sample.getFlag());
		// ����һ�����������¼���ߵĵ���ż���
		int[] nodes = new int[Constant.k];
		// ���Ϊ��������k�����ظ��ĵ���ţ�kΪѡ�ĵĳ���ά��
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = Math.abs(random.nextInt() % Constant.D_NUM);
			for (int j = 0; j < i; j++) {
				if (nodes[j] == nodes[i]) {
					i--;
					break;
				}
			}
		}
		// �����ɵĵ���źͶ�Ӧ��ֵ���볬��
		for (int i = 0; i < nodes.length; i++) {
			hyperedge.addNode(nodes[i], sample.getD()[nodes[i]]);
		}
		// ���ظó���
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
