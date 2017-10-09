package com.util;

import java.util.HashMap;
import java.util.Iterator;

/**
 * 统计学上量的计算，如平均数、极值、组合数、排列数、全排列、离差等
 * 
 * @author lmk
 * @version 1.0
 */
public class JStatistics {
	// 计算离差
	public static double Deviation(double[] a) {
		double aver = Average(a);
		double devi = 0;
		for (int i = 0; i < a.length; i++) {
			devi = devi + (a[i] - aver) * (a[i] - aver);
		}
		return devi;
	}

	public static double Deviation(int[] a) {
		double aver = Average(a);
		double devi = 0;
		for (int i = 0; i < a.length; i++) {
			devi = devi + (double) (a[i] - aver) * (a[i] - aver);
		}
		return devi;
	}

	// 计算平均数
	public static double Average(double[] a) {
		int length = a.length;
		double aver = 0.0;
		for (int i = 0; i < length; i++)
			aver = aver + a[i] / length;
		return aver;
	}

	public static double Average(int[] a) {
		int length = a.length;
		double aver = 0.0;
		for (int i = 0; i < length; i++)
			aver = aver + (double) a[i] / length;
		return aver;
	}

	// 计算和
	public static int Sum(int[] a) {
		int sum = 0;
		for (int i = 0; i < a.length; i++)
			sum = sum + a[i];
		return sum;
	}

	public static double Sum(double[] a) {
		double sum = 0;
		for (int i = 0; i < a.length; i++)
			sum = sum + a[i];
		return sum;
	}

	// 计算最大值
	public static double Max(double[] a) {
		double max = a[0];
		for (int i = 0; i < a.length; i++) {
			if (max < a[i])
				max = a[i];
		}
		return max;
	}

	public static int Max(int[] a) {
		int max = a[0];
		for (int i = 0; i < a.length; i++) {
			if (max < a[i])
				max = a[i];
		}
		return max;
	}

	// 计算最小值
	public static double Min(double[] a) {
		double min = a[0];
		for (int i = 0; i < a.length; i++) {
			if (min > a[i])
				min = a[i];
		}
		return min;
	}

	public static int Min(int[] a) {
		int min = a[0];
		for (int i = 0; i < a.length; i++) {
			if (min > a[i])
				min = a[i];
		}
		return min;
	}
	
	public static double MinPositive(double[] a) {
		double min = Max(a);
		for (int i = 0; i < a.length; i++) {
			if (min > a[i] && a[i] > 0)
				min = a[i];
		}
		min = min > 0 ? min : 1.0e-2;
		return min;
	}
	
	public static int MinPositive(int[] a) {
		int min = Max(a);
		for (int i = 0; i < a.length; i++) {
			if (min > a[i] && a[i] > 0)
				min = a[i];
		}
		min = min > 0 ? min : 1;
		return min;
	}
	
	public static double MinNoneNegative(double[] a) {
		double min = Max(a);
		for (int i = 0; i < a.length; i++) {
			if (min > a[i] && a[i] >= 0)
				min = a[i];
		}
		min = min > 0 ? min : 0;
		return min;
	}

	public static int MinNoneNegative(int[] a) {
		int min = Max(a);
		for (int i = 0; i < a.length; i++) {
			if (min > a[i] && a[i] >= 0)
				min = a[i];
		}
		min = min > 0 ? min : 0;
		return min;
	}
	
	// 计算组合数
	public static int Comb(int a, int b) {
		int a1 = a, b1 = b;
		int c = 1;
		for (int i = 1; i <= b1; i++)
			c = c * (a1 - i + 1) / i;
		return (int) c;
	}

	// 计算排列数
	public static int Arra(int a, int b) {
		int a1 = a, b1 = b;
		int c = 1;
		int i = 0;
		while (i < b1) {
			c = c * a1;
			a1--;
			i++;
		}
		return c;
	}

	// 计算全排列
	public static int Perm(int a) {
		int b = 1;
		for (int i = 1; i <= a; i++) {
			b = b * i;
		}
		return b;
	}

	/**
	 * @param dataMap
	 * @return 返回供ChartDataStyle使用的MaxSumValue
	 */
	public static double MaxSum(HashMap<String, double[]> dataMap) {
		double maxSum = 0;
		Iterator<String> iterator = dataMap.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			double curSum = Sum(dataMap.get(iterator.next()));
			if (i == 0)
				maxSum = curSum;
			else if (curSum > maxSum)
				maxSum = curSum;
			i++;
		}
		return maxSum;
	}

	/**
	 * @param dataMap
	 * @return 返回供ChartDataStyle使用的MinSumValue
	 */
	public static double MinSum(HashMap<String, double[]> dataMap) {
		double minSum = 0;
		Iterator<String> iterator = dataMap.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			double curSum = Sum(dataMap.get(iterator.next()));
			if (i == 0)
				minSum = curSum;
			else if (curSum < minSum)
				minSum = curSum;
			i++;
		}
		return minSum;
	}

	/**
	 * @param dataMap
	 * @return 返回供ChartDataStyle使用的MaxValue
	 */
	public static double Max(HashMap<String, double[]> dataMap) {
		double max = 0;
		Iterator<String> iterator = dataMap.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			double curMax = Max(dataMap.get(iterator.next()));
			if (i == 0)
				max = curMax;
			else if (curMax > max)
				max = curMax;
			i++;
		}
		return max;
	}

	/**
	 * @param dataMap
	 * @return 返回供ChartDataStyle使用的MinValue
	 */
	public static double Min(HashMap<String, double[]> dataMap) {
		double min = 0;
		Iterator<String> iterator = dataMap.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			double curMin = Min(dataMap.get(iterator.next()));
			if (i == 0)
				min = curMin;
			else if (curMin < min)
				min = curMin;
			i++;
		}
		return min;
	}

	/**
	 * @param dataMap
	 * @return 返回供ChartDataStyle使用的AverageValue
	 */
	public static double Average(HashMap<String, double[]> dataMap) {
		double aver = 0;
		Iterator<String> iterator = dataMap.keySet().iterator();
		int i = 0;
		double sum = 0;
		while (iterator.hasNext()) {
			double curAver = Average(dataMap.get(iterator.next()));
			sum = sum + curAver;
			i++;
		}
		aver = sum / i;
		return aver;
	}

	/**
	 * @param str
	 * @return 字符串转为数值数组
	 */
	public static double[] StringToDoubles(String str) {
		double[] values = new double[str.split(",").length];
		for (int i = 0; i < values.length; i++) {
			values[i] = Double.parseDouble(str.split(",")[i]);
		}
		return values;
	}
}
