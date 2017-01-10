package com.wanghong.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 * ������
 * 
 * @author WangHong
 * 
 */
public class Control {
	// ����һ�����������ڶ�ȡ�ļ�ʱ����
	static int i = 0;

	public static void main(String[] args) {
		// �򿪲��Լ���ѵ�����ļ�
		File train = new File("src/data/AMLALL_train.txt");
		File test = new File("src/data/AMLALL_test.txt");
		// �����򿪼�¼�б����ݻ�ѡ��Ļ����ŵ��ļ�
		File chooseGeneNum = null;
		// ����������
		Scanner in = null;
		try {
			// ������������ֱ��Ų��Լ���ѵ��������
			int[][] arr_train = new int[Constant.TRAIN_NUM][Constant.D_NUM_RAW + 1];
			int[][] arr_test = new int[Constant.TEST_NUM][Constant.D_NUM_RAW + 1];
			// ����ѵ�������ݵ�������
			in = new Scanner(train);
			while (in.hasNext()) {
				String string = (String) in.next();
				arr_train[i / (Constant.D_NUM_RAW + 1)][i
						% (Constant.D_NUM_RAW + 1)] = Integer.valueOf(string);
				i++;
			}
			// ������Լ��ļ���������
			i = 0;
			in = new Scanner(test);
			while (in.hasNext()) {
				String string = (String) in.next();
				arr_test[i / (Constant.D_NUM_RAW + 1)][i
						% (Constant.D_NUM_RAW + 1)] = Integer.valueOf(string);
				i++;
			}
			// ����ԭʼѵ������������
			Sample[] samples_train = new Sample[Constant.TRAIN_NUM];
			for (int i = 0; i < samples_train.length; i++) {
				samples_train[i] = new Sample(i, Arrays.copyOf(arr_train[i],
						Constant.D_NUM_RAW),
						String.valueOf(arr_train[i][Constant.D_NUM_RAW]));
			}
			// �õ���������Ԥ�����ѵ������������
			samples_train = Tools.rawSampleToSample(samples_train);
			// ����ԭʼ���Լ���������
			Sample[] samples_test = new Sample[Constant.TEST_NUM];
			for (int i = 0; i < samples_test.length; i++) {
				samples_test[i] = new Sample(i, Arrays.copyOf(arr_test[i],
						Constant.D_NUM_RAW),
						String.valueOf(arr_test[i][Constant.D_NUM_RAW]));
			}
			// �򿪼�¼�б����ݻ�ѡ��Ļ����ŵ��ļ�
			chooseGeneNum = new File("src/record/choseGeneNum.txt");
			// ����һ�����鱣���ݻ�ѡ��Ļ�����
			int[] chooseGeneNums = new int[Constant.D_NUM];
			// ��ȡ�ݻ�ѡ��Ļ�����
			in = new Scanner(chooseGeneNum);
			i = 0;
			while (in.hasNext()) {
				String string = (String) in.next();
				chooseGeneNums[i] = Integer.valueOf(string);
				i++;
			}
			// �õ���������Ԥ�����ѵ������������
			samples_test = Tools
					.rawSampleToSample(samples_test, chooseGeneNums);
			//���һЩ������Ϣ
			System.out.println("���εĳ���ά��Ϊ��"+Constant.k);
			// ���������һ�����ѵ�����Ͳ��Լ�����
			System.out.println("*************************************************");
			System.out.println("��������ѡ�񡢹�һ���Ͷ�ֵ�����ѵ�������ݣ�");
			System.out.println("*************************************************");
			for (int i = 0; i < samples_test.length; i++) {
				for (int j = 0; j < samples_test[i].getD().length; j++) {
					System.out.print(samples_test[i].getD()[j] + " ");
				}
				System.out.print("  �������:" + samples_test[i].getFlag());
				System.out.println();
			}
			System.out.println("*************************************************");
			System.out.println("��������ѡ�񡢹�һ���Ͷ�ֵ����Ĳ��Լ����ݣ�");
			System.out.println("*************************************************");
			for (int i = 0; i < samples_train.length; i++) {
				for (int j = 0; j < samples_train[i].getD().length; j++) {
					System.out.print(samples_train[i].getD()[j] + " ");
				}
				System.out.print("  �������:" + samples_train[i].getFlag());
				System.out.println();
			}
			// ����һ��ѵ����
			Trainer trainer = new Trainer(samples_train);
			// ��ѵ��������һ������ѵ���ĳ�������
			Hyperedge[] hyperedges = trainer.startTrain();
			// �½�һ���������������ɵĳ��߾������鴫������
			Sorter sorter = new Sorter(hyperedges);
			// �Բ��Լ������ݷ���
			int errorNum=0;
			for (int i = 0; i < samples_test.length; i++) {
				String sortString=sorter.sort_test(samples_test[i]);
				String correctString=samples_test[i].getFlag();
				System.out.println("��������"+sortString
						+ "��ȷ���" + correctString);
				if (!(sortString.equals(correctString))) {
					errorNum++;
				}
			}
			System.out.println("*************************************************");
			System.out.println("���������������"+errorNum);
			System.out.println("*************************************************");
			System.out.println("��ȷ�ʣ�"+String.valueOf((double)(Constant.TEST_NUM-errorNum)/Constant.TEST_NUM));
			System.out.println("*************************************************");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
	}

}
