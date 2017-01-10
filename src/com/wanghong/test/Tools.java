package com.wanghong.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 工具类，提供一些需要用到的方法
 * 
 * @author WangHong
 * 
 */
public class Tools {
	/**
	 * 信噪比类，包含一些计算基因信噪比的数据和该基因对应的信噪比
	 * 
	 * @author WangHong
	 * 
	 */
	private class SNR {
		/**
		 * 基因编号
		 */
		public int geneNum;
		/**
		 * 基因的信噪比
		 */
		public double snr;
		/**
		 * 包含在类型一样本中的该基因的数据的集合
		 */
		public List<Integer> geneDatas1;
		/**
		 * 包含在类型二样本中的该基因的数据的集合
		 */
		public List<Integer> geneDatas2;
		/**
		 * 基因在类型一中的总和
		 */
		public double geneDatasum1;
		/**
		 * 基因在类型二中的总和
		 */
		public double geneDatasum2;
		/**
		 * 基因在类型1中的平均值
		 */
		public double ave1;
		/**
		 * 基因在类型2中的平均值
		 */
		public double ave2;
		/**
		 * 基因在类型1中的标准差
		 */
		public double sd1;
		/**
		 * 基因在类型2中的标准差
		 */
		public double sd2;

		/**
		 * @param geneNum
		 *            基因编号
		 * @param geneDatas1
		 *            包含在类型一样本中的该基因的数据的数组
		 * @param geneDatas2
		 *            包含在类型二样本中的该基因的数据的数组
		 */
		public SNR(int geneNum, List<Integer> geneDatas1,
				List<Integer> geneDatas2) {
			super();
			this.init();
			this.geneNum = geneNum;
			this.geneDatas1 = geneDatas1;
			this.geneDatas2 = geneDatas2;
		}

		private void init() {
			this.ave1 = 0;
			this.ave2 = 0;
			this.geneDatasum1 = 0;
			this.geneDatasum2 = 0;
			this.sd1 = 0;
			this.sd2 = 0;
			this.snr = 0;
		}

		/**
		 * 计算信噪比
		 */
		public void computeSNR() {
			// 计算geneDatasum1和geneDatasum2
			Iterator<Integer> iterator = (Iterator<Integer>) this.geneDatas1
					.iterator();
			while (iterator.hasNext()) {
				Integer integer = (Integer) iterator.next();
				this.geneDatasum1 += integer.intValue();
			}
			iterator = (Iterator<Integer>) this.geneDatas2.iterator();
			while (iterator.hasNext()) {
				Integer integer = (Integer) iterator.next();
				this.geneDatasum2 += integer.intValue();
			}
			// 计算ave1和ave2
			this.ave1 = this.geneDatasum1 / this.geneDatas1.size();
			this.ave2 = this.geneDatasum2 / this.geneDatas2.size();
			// 计算sd1和sd2
			int sum = 0;
			iterator = (Iterator<Integer>) this.geneDatas1.iterator();
			while (iterator.hasNext()) {
				Integer integer = (Integer) iterator.next();
				sum += (integer.intValue() - this.ave1)
						* (integer.intValue() - this.ave1);
			}
			this.sd1 = Math.sqrt(sum / this.geneDatas1.size());

			sum = 0;
			iterator = (Iterator<Integer>) this.geneDatas2.iterator();
			while (iterator.hasNext()) {
				Integer integer = (Integer) iterator.next();
				sum += (integer.intValue() - this.ave2)
						* (integer.intValue() - this.ave2);
			}
			this.sd2 = Math.sqrt(sum / this.geneDatas2.size());
			// 计算信噪比
			this.snr = (this.ave1 - this.ave2) / (this.sd1 + this.sd2);
		}

		@Override
		public String toString() {
			return "snr:" + this.snr + " ave1:" + this.ave1 + " ave2:"
					+ this.ave2 + " sd1:" + this.sd1 + " sd2:" + this.sd2;
		}
	}

	/**
	 * 传入一个原始样本数组，经过基因选择、归一化和二值化后返回一个样本数组
	 * 
	 * @param rawSamples
	 *            传入的原始样本数组
	 * @return 经过处理的样本数组
	 */
	public static Sample[] rawSampleToSample(Sample[] rawSamples) {
		return uniformAndBinaryzation(chooseGene(rawSamples));
	}

	/**
	 * 传入一个原始样本数组，经过基因选择、归一化和二值化后返回一个样本数组
	 * 
	 * @param rawSamples
	 *            传入的原始样本数组
	 * @param chooseGeneNums
	 *            保存有指定选择的基因编号的数组
	 * @return 经过处理的样本数组
	 */
	public static Sample[] rawSampleToSample(Sample[] rawSamples,
			int[] chooseGeneNums) {
		return uniformAndBinaryzation(chooseGene(rawSamples, chooseGeneNums));
	}

	/**
	 * 将传入的原始样本数组经过指定的基因选择，返回一个只包含选择的样本基因的的样本数组
	 * 
	 * @param rawSamples
	 *            原始样本数组
	 * @param chooseGeneNums
	 *            指定的基因选择的基因序号数组
	 * @return 只包含选择的样本基因的的样本数组
	 */
	private static Sample[] chooseGene(Sample[] rawSamples, int[] chooseGeneNums) {
		// 建立一个新的样本数组用于返回
		Sample[] newSamples = new Sample[rawSamples.length];
		// 将原始样本中的选定基因提取出来，使新的样本只保存指定基因
		for (int i = 0; i < rawSamples.length; i++) {
			int[] arr = new int[Constant.D_NUM];
			for (int j = 0; j < arr.length; j++) {
				arr[j] = rawSamples[i].getD()[chooseGeneNums[j]];
			}
			newSamples[i] = new Sample(rawSamples[i].getIndex(), arr,
					rawSamples[i].getFlag());
		}
		return newSamples;
	}

	/**
	 * 将传入的原始样本数组经过基因选择，返回一个只包含选择的样本基因的的样本数组
	 * 
	 * @param rawSamples
	 *            原始样本数组
	 * @return 只包含选择的样本基因的的样本数组
	 */
	private static Sample[] chooseGene(Sample[] rawSamples) {
		// 定义一个SNR类数组用来计算每个基因的信噪比
		SNR[] snrs = new SNR[Constant.D_NUM_RAW];
		// 定义一个整型数组用来存放选择的基因编号
		int[] chooseNum = new int[Constant.D_NUM];
		// 定义两个样本集合用来存放两个类别的样本
		Set<Sample> samples0 = new HashSet<>();
		Set<Sample> samples1 = new HashSet<>();
		// 扫描原始样本数组，将两种类型的样本添加入两种集合
		for (int i = 0; i < rawSamples.length; i++) {
			if (rawSamples[i].getFlag().equals("0")) {
				samples0.add(rawSamples[i]);
			} else if (rawSamples[i].getFlag().equals("1")) {
				samples1.add(rawSamples[i]);
			} else {
				// 调试时使用
				System.out.println("chooseGene error 1");
			}
		}
		// 初始化SNR数组
		for (int i = 0; i < snrs.length; i++) {
			snrs[i] = new Tools().new SNR(i, new ArrayList<Integer>(),
					new ArrayList<Integer>());
		}
		// 分别遍历samples0和samples1，将为SNR数组的两个基因的数据的集合填充数据
		Iterator<Sample> iterator = (Iterator<Sample>) samples0.iterator();
		while (iterator.hasNext()) {
			Sample sample = (Sample) iterator.next();
			for (int i = 0; i < Constant.D_NUM_RAW; i++) {
				if (snrs[i].geneDatas1.add(sample.getD()[i])) {

				} else {
					System.out.println("chooseGene error 2");
				}
			}
		}
		iterator = (Iterator<Sample>) samples1.iterator();
		while (iterator.hasNext()) {
			Sample sample = (Sample) iterator.next();
			for (int i = 0; i < Constant.D_NUM_RAW; i++) {
				if (snrs[i].geneDatas2.add(sample.getD()[i])) {

				} else {
					System.out.println("chooseGene error 3");
				}
			}
		}
		// 调用SNR对象的computeSNR方法，计算每个基因的信噪比
		for (int i = 0; i < snrs.length; i++) {
			snrs[i].computeSNR();
		}
		// 按信噪比从大到小对SNR数组进行排序
		for (int i = 0; i < snrs.length - 1; i++) {
			for (int j = i + 1; j < snrs.length; j++) {
				if (snrs[i].snr < snrs[j].snr) {
					SNR temp = snrs[i];
					snrs[i] = snrs[j];
					snrs[j] = temp;
				}
			}
		}
		// 从正值和负值中分别选择绝对值较高的n/2个基因
		for (int i = 0; i < Constant.D_NUM / 2; i++) {
			chooseNum[i] = snrs[i].geneNum;
			chooseNum[Constant.D_NUM - 1 - i] = snrs[Constant.D_NUM_RAW - 1 - i].geneNum;
		}
		// 建立一个新的样本数组用于返回
		Sample[] newSamples = new Sample[rawSamples.length];
		// 将原始样本中的选定基因提取出来，使新的样本只保存指定基因
		for (int i = 0; i < rawSamples.length; i++) {
			int[] arr = new int[Constant.D_NUM];
			for (int j = 0; j < arr.length; j++) {
				arr[j] = rawSamples[i].getD()[chooseNum[j]];
			}
			newSamples[i] = new Sample(rawSamples[i].getIndex(), arr,
					rawSamples[i].getFlag());
		}
		// 将选择的特征基因序号记录输出

		FileWriter choseGeneNum = null;
		try {
			choseGeneNum = new FileWriter("src/record/choseGeneNum.txt");
			for (int i = 0; i < chooseNum.length; i++) {
				choseGeneNum.write(chooseNum[i] + " ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				choseGeneNum.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return newSamples;
	}

	/**
	 * 将传入的样本数组的样本基因归一化和二值化，返回经过归一化和二值化的样本数组
	 * 
	 * @param samples
	 *            需要归一化和二值化的样本数组
	 * @return 经过归一化和二值化的样本数组
	 */
	private static Sample[] uniformAndBinaryzation(Sample[] samples) {
		// 定义一个二维数组用来保存每个基因在不同样本中的数据
		int[][] allGeneData = new int[samples.length][Constant.D_NUM];
		// 读取全部样本基因数据
		for (int i = 0; i < samples.length; i++) {
			allGeneData[i] = samples[i % Constant.D_NUM].getD();
		}
		// 定义一个数组用来保存每个基因的平均值
		double[] aves = new double[Constant.D_NUM];
		// 初始化aves
		for (int i = 0; i < aves.length; i++) {
			aves[i] = 0;
		}
		// 计算全部样本基因的平均值
		for (int i = 0; i < samples.length; i++) {
			for (int j = 0; j < aves.length; j++) {
				aves[j] += allGeneData[i][j];
			}
		}
		for (int i = 0; i < aves.length; i++) {
			aves[i] /= samples.length;
		}
		// 定义一个数组用来保存每个基因的标准差
		double[] sds = new double[Constant.D_NUM];
		// 初始化sds
		for (int i = 0; i < sds.length; i++) {
			sds[i] = 0;
		}
		// 计算全部样本基因的标准差
		for (int i = 0; i < samples.length; i++) {
			for (int j = 0; j < sds.length; j++) {
				sds[j] = sds[j] + (allGeneData[i][j] - aves[j])*(allGeneData[i][j] - aves[j]);
			}
		}
		for (int i = 0; i < sds.length; i++) {
			sds[i] = Math.sqrt(sds[i] / samples.length);
		}
		// 对选取的每个样本基因进行归一化处理
		for (int i = 0; i < samples.length; i++) {
			for (int j = 0; j < samples[i].getD().length; j++) {
				samples[i].getD()[j] = ((samples[i].getD()[j] - aves[j]) / sds[j]) >= 0 ? 1
						: 0;
			}
		}
		return samples;
	}

}
