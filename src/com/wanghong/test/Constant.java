package com.wanghong.test;

/**
 * 此类保存一些程序需要用到的常数
 * 
 * @author WangHong
 * 
 */
public class Constant {
	/**
	 * 训练集的样本数
	 */
	public static final int TRAIN_NUM = 38;
	/**
	 * 测试集的样本数
	 */
	public static final int TEST_NUM = 34;
	/**
	 * 原始样本的特征维度
	 */
	public static final int D_NUM_RAW = 7129;
	/**
	 * 经过基因选择之后样本的特征维度
	 */
	public static final int D_NUM = 32;
	/**
	 * 训练集中的每个样本生成的超边数
	 */
	public static final int SAMPLE_HYPERDEGENUM = 100;
	/**
	 * 生成超边的维度
	 */
	public static final int k = 7;
	/**
	 * 分类的类别数
	 */
	public static final int Y = 2;
	/**
	 * 用于在演化学习时控制替代的超边数
	 */
	public static final double w = 0.6;
}
