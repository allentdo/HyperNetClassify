package com.wanghong.test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * �����࣬�ṩһЩ��Ҫ�õ��ķ���
 * 
 * @author WangHong
 * 
 */
public class Tools {
	/**
	 * ������࣬����һЩ�����������ȵ����ݺ͸û����Ӧ�������
	 * 
	 * @author WangHong
	 * 
	 */
	private class SNR {
		/**
		 * ������
		 */
		public int geneNum;
		/**
		 * ����������
		 */
		public double snr;
		/**
		 * ����������һ�����еĸû�������ݵļ���
		 */
		public List<Integer> geneDatas1;
		/**
		 * ���������Ͷ������еĸû�������ݵļ���
		 */
		public List<Integer> geneDatas2;
		/**
		 * ����������һ�е��ܺ�
		 */
		public double geneDatasum1;
		/**
		 * ���������Ͷ��е��ܺ�
		 */
		public double geneDatasum2;
		/**
		 * ����������1�е�ƽ��ֵ
		 */
		public double ave1;
		/**
		 * ����������2�е�ƽ��ֵ
		 */
		public double ave2;
		/**
		 * ����������1�еı�׼��
		 */
		public double sd1;
		/**
		 * ����������2�еı�׼��
		 */
		public double sd2;

		/**
		 * @param geneNum
		 *            ������
		 * @param geneDatas1
		 *            ����������һ�����еĸû�������ݵ�����
		 * @param geneDatas2
		 *            ���������Ͷ������еĸû�������ݵ�����
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
		 * ���������
		 */
		public void computeSNR() {
			// ����geneDatasum1��geneDatasum2
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
			// ����ave1��ave2
			this.ave1 = this.geneDatasum1 / this.geneDatas1.size();
			this.ave2 = this.geneDatasum2 / this.geneDatas2.size();
			// ����sd1��sd2
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
			// ���������
			this.snr = (this.ave1 - this.ave2) / (this.sd1 + this.sd2);
		}

		@Override
		public String toString() {
			return "snr:" + this.snr + " ave1:" + this.ave1 + " ave2:"
					+ this.ave2 + " sd1:" + this.sd1 + " sd2:" + this.sd2;
		}
	}

	/**
	 * ����һ��ԭʼ�������飬��������ѡ�񡢹�һ���Ͷ�ֵ���󷵻�һ����������
	 * 
	 * @param rawSamples
	 *            �����ԭʼ��������
	 * @return �����������������
	 */
	public static Sample[] rawSampleToSample(Sample[] rawSamples) {
		return uniformAndBinaryzation(chooseGene(rawSamples));
	}

	/**
	 * ����һ��ԭʼ�������飬��������ѡ�񡢹�һ���Ͷ�ֵ���󷵻�һ����������
	 * 
	 * @param rawSamples
	 *            �����ԭʼ��������
	 * @param chooseGeneNums
	 *            ������ָ��ѡ��Ļ����ŵ�����
	 * @return �����������������
	 */
	public static Sample[] rawSampleToSample(Sample[] rawSamples,
			int[] chooseGeneNums) {
		return uniformAndBinaryzation(chooseGene(rawSamples, chooseGeneNums));
	}

	/**
	 * �������ԭʼ�������龭��ָ���Ļ���ѡ�񣬷���һ��ֻ����ѡ�����������ĵ���������
	 * 
	 * @param rawSamples
	 *            ԭʼ��������
	 * @param chooseGeneNums
	 *            ָ���Ļ���ѡ��Ļ����������
	 * @return ֻ����ѡ�����������ĵ���������
	 */
	private static Sample[] chooseGene(Sample[] rawSamples, int[] chooseGeneNums) {
		// ����һ���µ������������ڷ���
		Sample[] newSamples = new Sample[rawSamples.length];
		// ��ԭʼ�����е�ѡ��������ȡ������ʹ�µ�����ֻ����ָ������
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
	 * �������ԭʼ�������龭������ѡ�񣬷���һ��ֻ����ѡ�����������ĵ���������
	 * 
	 * @param rawSamples
	 *            ԭʼ��������
	 * @return ֻ����ѡ�����������ĵ���������
	 */
	private static Sample[] chooseGene(Sample[] rawSamples) {
		// ����һ��SNR��������������ÿ������������
		SNR[] snrs = new SNR[Constant.D_NUM_RAW];
		// ����һ�����������������ѡ��Ļ�����
		int[] chooseNum = new int[Constant.D_NUM];
		// �����������������������������������
		Set<Sample> samples0 = new HashSet<>();
		Set<Sample> samples1 = new HashSet<>();
		// ɨ��ԭʼ�������飬���������͵�������������ּ���
		for (int i = 0; i < rawSamples.length; i++) {
			if (rawSamples[i].getFlag().equals("0")) {
				samples0.add(rawSamples[i]);
			} else if (rawSamples[i].getFlag().equals("1")) {
				samples1.add(rawSamples[i]);
			} else {
				// ����ʱʹ��
				System.out.println("chooseGene error 1");
			}
		}
		// ��ʼ��SNR����
		for (int i = 0; i < snrs.length; i++) {
			snrs[i] = new Tools().new SNR(i, new ArrayList<Integer>(),
					new ArrayList<Integer>());
		}
		// �ֱ����samples0��samples1����ΪSNR�����������������ݵļ����������
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
		// ����SNR�����computeSNR����������ÿ������������
		for (int i = 0; i < snrs.length; i++) {
			snrs[i].computeSNR();
		}
		// ������ȴӴ�С��SNR�����������
		for (int i = 0; i < snrs.length - 1; i++) {
			for (int j = i + 1; j < snrs.length; j++) {
				if (snrs[i].snr < snrs[j].snr) {
					SNR temp = snrs[i];
					snrs[i] = snrs[j];
					snrs[j] = temp;
				}
			}
		}
		// ����ֵ�͸�ֵ�зֱ�ѡ�����ֵ�ϸߵ�n/2������
		for (int i = 0; i < Constant.D_NUM / 2; i++) {
			chooseNum[i] = snrs[i].geneNum;
			chooseNum[Constant.D_NUM - 1 - i] = snrs[Constant.D_NUM_RAW - 1 - i].geneNum;
		}
		// ����һ���µ������������ڷ���
		Sample[] newSamples = new Sample[rawSamples.length];
		// ��ԭʼ�����е�ѡ��������ȡ������ʹ�µ�����ֻ����ָ������
		for (int i = 0; i < rawSamples.length; i++) {
			int[] arr = new int[Constant.D_NUM];
			for (int j = 0; j < arr.length; j++) {
				arr[j] = rawSamples[i].getD()[chooseNum[j]];
			}
			newSamples[i] = new Sample(rawSamples[i].getIndex(), arr,
					rawSamples[i].getFlag());
		}
		// ��ѡ�������������ż�¼���

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
	 * �������������������������һ���Ͷ�ֵ�������ؾ�����һ���Ͷ�ֵ������������
	 * 
	 * @param samples
	 *            ��Ҫ��һ���Ͷ�ֵ������������
	 * @return ������һ���Ͷ�ֵ������������
	 */
	private static Sample[] uniformAndBinaryzation(Sample[] samples) {
		// ����һ����ά������������ÿ�������ڲ�ͬ�����е�����
		int[][] allGeneData = new int[samples.length][Constant.D_NUM];
		// ��ȡȫ��������������
		for (int i = 0; i < samples.length; i++) {
			allGeneData[i] = samples[i % Constant.D_NUM].getD();
		}
		// ����һ��������������ÿ�������ƽ��ֵ
		double[] aves = new double[Constant.D_NUM];
		// ��ʼ��aves
		for (int i = 0; i < aves.length; i++) {
			aves[i] = 0;
		}
		// ����ȫ�����������ƽ��ֵ
		for (int i = 0; i < samples.length; i++) {
			for (int j = 0; j < aves.length; j++) {
				aves[j] += allGeneData[i][j];
			}
		}
		for (int i = 0; i < aves.length; i++) {
			aves[i] /= samples.length;
		}
		// ����һ��������������ÿ������ı�׼��
		double[] sds = new double[Constant.D_NUM];
		// ��ʼ��sds
		for (int i = 0; i < sds.length; i++) {
			sds[i] = 0;
		}
		// ����ȫ����������ı�׼��
		for (int i = 0; i < samples.length; i++) {
			for (int j = 0; j < sds.length; j++) {
				sds[j] = sds[j] + (allGeneData[i][j] - aves[j])*(allGeneData[i][j] - aves[j]);
			}
		}
		for (int i = 0; i < sds.length; i++) {
			sds[i] = Math.sqrt(sds[i] / samples.length);
		}
		// ��ѡȡ��ÿ������������й�һ������
		for (int i = 0; i < samples.length; i++) {
			for (int j = 0; j < samples[i].getD().length; j++) {
				samples[i].getD()[j] = ((samples[i].getD()[j] - aves[j]) / sds[j]) >= 0 ? 1
						: 0;
			}
		}
		return samples;
	}

}
