package com.wanghong.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

/**
 * 控制器
 * 
 * @author WangHong
 * 
 */
public class Control {
	// 定义一个变量用来在读取文件时计数
	static int i = 0;

	public static void main(String[] args) {
		// 打开测试集和训练集文件
		File train = new File("src/data/AMLALL_train.txt");
		File test = new File("src/data/AMLALL_test.txt");
		// 用来打开记录有本次演化选择的基因编号的文件
		File chooseGeneNum = null;
		// 定义输入流
		Scanner in = null;
		try {
			// 定义两个数组分别存放测试集和训练集数据
			int[][] arr_train = new int[Constant.TRAIN_NUM][Constant.D_NUM_RAW + 1];
			int[][] arr_test = new int[Constant.TEST_NUM][Constant.D_NUM_RAW + 1];
			// 读入训练集数据到数组中
			in = new Scanner(train);
			while (in.hasNext()) {
				String string = (String) in.next();
				arr_train[i / (Constant.D_NUM_RAW + 1)][i
						% (Constant.D_NUM_RAW + 1)] = Integer.valueOf(string);
				i++;
			}
			// 读入测试集文件到数组中
			i = 0;
			in = new Scanner(test);
			while (in.hasNext()) {
				String string = (String) in.next();
				arr_test[i / (Constant.D_NUM_RAW + 1)][i
						% (Constant.D_NUM_RAW + 1)] = Integer.valueOf(string);
				i++;
			}
			// 生成原始训练集样本数组
			Sample[] samples_train = new Sample[Constant.TRAIN_NUM];
			for (int i = 0; i < samples_train.length; i++) {
				samples_train[i] = new Sample(i, Arrays.copyOf(arr_train[i],
						Constant.D_NUM_RAW),
						String.valueOf(arr_train[i][Constant.D_NUM_RAW]));
			}
			// 得到经过数据预处理的训练集样本数组
			samples_train = Tools.rawSampleToSample(samples_train);
			// 生成原始测试集样本数组
			Sample[] samples_test = new Sample[Constant.TEST_NUM];
			for (int i = 0; i < samples_test.length; i++) {
				samples_test[i] = new Sample(i, Arrays.copyOf(arr_test[i],
						Constant.D_NUM_RAW),
						String.valueOf(arr_test[i][Constant.D_NUM_RAW]));
			}
			// 打开记录有本次演化选择的基因编号的文件
			chooseGeneNum = new File("src/record/choseGeneNum.txt");
			// 定义一个数组保存演化选择的基因编号
			int[] chooseGeneNums = new int[Constant.D_NUM];
			// 读取演化选择的基因编号
			in = new Scanner(chooseGeneNum);
			i = 0;
			while (in.hasNext()) {
				String string = (String) in.next();
				chooseGeneNums[i] = Integer.valueOf(string);
				i++;
			}
			// 得到经过数据预处理的训练集样本数组
			samples_test = Tools
					.rawSampleToSample(samples_test, chooseGeneNums);
			//输出一些基本信息
			System.out.println("本次的超边维度为："+Constant.k);
			// 输出经过归一化后的训练集和测试集数据
			System.out.println("*************************************************");
			System.out.println("经过基因选择、归一化和二值化后的训练集数据：");
			System.out.println("*************************************************");
			for (int i = 0; i < samples_test.length; i++) {
				for (int j = 0; j < samples_test[i].getD().length; j++) {
					System.out.print(samples_test[i].getD()[j] + " ");
				}
				System.out.print("  样本类别:" + samples_test[i].getFlag());
				System.out.println();
			}
			System.out.println("*************************************************");
			System.out.println("经过基因选择、归一化和二值化后的测试集数据：");
			System.out.println("*************************************************");
			for (int i = 0; i < samples_train.length; i++) {
				for (int j = 0; j < samples_train[i].getD().length; j++) {
					System.out.print(samples_train[i].getD()[j] + " ");
				}
				System.out.print("  样本类别:" + samples_train[i].getFlag());
				System.out.println();
			}
			// 定义一个训练器
			Trainer trainer = new Trainer(samples_train);
			// 用训练器生成一个经过训练的超边数组
			Hyperedge[] hyperedges = trainer.startTrain();
			// 新建一个分类器，将生成的超边决策数组传入其中
			Sorter sorter = new Sorter(hyperedges);
			// 对测试集的数据分类
			int errorNum=0;
			for (int i = 0; i < samples_test.length; i++) {
				String sortString=sorter.sort_test(samples_test[i]);
				String correctString=samples_test[i].getFlag();
				System.out.println("分类结果："+sortString
						+ "正确结果" + correctString);
				if (!(sortString.equals(correctString))) {
					errorNum++;
				}
			}
			System.out.println("*************************************************");
			System.out.println("分类错误样本数："+errorNum);
			System.out.println("*************************************************");
			System.out.println("正确率："+String.valueOf((double)(Constant.TEST_NUM-errorNum)/Constant.TEST_NUM));
			System.out.println("*************************************************");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			in.close();
		}
	}

}
