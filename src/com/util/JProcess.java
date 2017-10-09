package com.util;

import java.util.ArrayList;

/**
 * 处理数组之间的运算的类，包括数组加减交并等
 * 
 * @author lmk
 * @version 1.0
 */
public class JProcess {
	// private static double EPS_DISTANCE = 1e-7;
	private static double DISTINCT_EXP = 0.005;

	// 返回差的数组（前 - 后）
	public static int[] Minus1(int[] a) {
		int[] b = a.clone();
		int c[] = new int[b.length - 1];
		for (int i = 0; i < c.length; i++) {
			c[i] = b[i] - b[i + 1];
		}
		return c;
	}

	public static double[] Minus1(double[] a) {
		double[] b = a.clone();
		double c[] = new double[b.length - 1];
		for (int i = 0; i < c.length; i++) {
			c[i] = b[i] - b[i + 1];
		}
		return c;
	}

	// 返回差的数组（后 - 前）
	public static int[] Minus2(int[] a) {
		int[] b = a.clone();
		int c[] = new int[b.length - 1];
		for (int i = 0; i < c.length; i++) {
			c[i] = b[i + 1] - b[i];
		}
		return c;
	}

	public static double[] Minus2(double[] a) {
		double[] b = a.clone();
		double c[] = new double[b.length - 1];
		for (int i = 0; i < c.length; i++) {
			c[i] = b[i + 1] - b[i];
		}
		return c;
	}

	// 返回商的数组（前 / 后）
	public static double[] Divide1(double[] a) {
		double[] b = a.clone();
		double c[] = new double[b.length - 1];
		for (int i = 0; i < c.length; i++) {
			c[i] = b[i] / b[i + 1];
		}
		return c;
	}

	public static double[] Divide1(int[] a) {
		int[] b = a.clone();
		double[] c = new double[b.length - 1];
		for (int i = 0; i < c.length; i++) {
			c[i] = (double) b[i] / b[i + 1];
		}
		return c;
	}

	// 返回商的数组（后 / 前）
	public static double[] Divide2(double[] a) {
		double[] b = a.clone();
		double c[] = new double[b.length - 1];
		for (int i = 0; i < c.length; i++) {
			c[i] = b[i + 1] / b[i];
		}
		return c;
	}

	public static double[] Divide2(int[] a) {
		int[] b = a.clone();
		double[] c = new double[b.length - 1];
		for (int i = 0; i < c.length; i++) {
			c[i] = (double) b[i + 1] / b[i];
		}
		return c;
	}

	// 对数据进行处理，相近的数字仅保留一个，返回被剔除数值
	public static double[] Distinct1(double[] a) {
		double[] b = a.clone();
		ArrayList<Double> list = new ArrayList<Double>();
		for (int i = 1; i < b.length; i++) {
			double temp = (b[i] - b[i - 1]) / b[i - 1];
			if (temp < DISTINCT_EXP)
				list.add(b[i]);
		}
		list.trimToSize();
		Double[] c = (Double[]) list.toArray(new Double[list.size()]);
		double[] d = new double[c.length];
		for (int i = 0; i < c.length; i++)
			d[i] = c[i].doubleValue();
		return d;
	}

	public static int[] Distinct1(int[] a) {
		int[] b = a.clone();
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i < b.length; i++) {
			double temp = (double) (b[i] - b[i - 1]) / b[i - 1];
			if (temp < DISTINCT_EXP)
				list.add(b[i]);
		}
		list.trimToSize();
		Integer[] c = (Integer[]) list.toArray(new Integer[list.size()]);
		int[] d = new int[c.length];
		for (int i = 0; i < c.length; i++)
			d[i] = c[i].intValue();
		return d;
	}

	// 对数据进行处理，相近的数字仅保留一个，返回剩余数值
	public static double[] Distinct2(double[] a) {
		double[] b = a.clone();
		ArrayList<Double> list = new ArrayList<Double>();
		list.add(b[0]);
		for (int i = 1; i < b.length; i++) {
			double temp = (b[i] - b[i - 1]) / b[i - 1];
			if (temp >= DISTINCT_EXP)
				list.add(b[i]);
		}
		list.trimToSize();
		Double[] c = (Double[]) list.toArray(new Double[list.size()]);
		double[] d = new double[c.length];
		for (int i = 0; i < c.length; i++)
			d[i] = c[i].doubleValue();
		return d;
	}

	public static int[] Distinct2(int[] a) {
		int[] b = a.clone();
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i = 1; i < b.length; i++) {
			double temp = (double) (b[i] - b[i - 1]) / b[i - 1];
			if (temp >= DISTINCT_EXP)
				list.add(b[i]);
		}
		list.trimToSize();
		Integer[] c = (Integer[]) list.toArray(new Integer[list.size()]);
		int[] d = new int[c.length];
		for (int i = 0; i < c.length; i++)
			d[i] = c[i].intValue();
		return d;
	}

	// 计算数组差
	public static int[] ExceptInt(int[] a1, int[] a2) {
		int[] b1 = a1.clone();
		int[] b2 = a2.clone();
		ArrayList<Integer> list = new ArrayList<Integer>();
		JSort.SmallToBig(b1);
		JSort.SmallToBig(b2);
		int b2min = JStatistics.Min(b2);
		int b2max = JStatistics.Max(b2);
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] < b2min || b1[i] > b2max)
				list.add(b1[i]);
			else {
				boolean contain = false;
				for (int j = 0; j < b2.length; j++) {
					if (b1[i] == b2[j])
						contain = true;
				}
				if (!contain)
					list.add(b1[i]);
			}
		}
		list.trimToSize();
		Integer[] b = (Integer[]) list.toArray(new Integer[list.size()]);
		int[] c = new int[b.length];
		for (int i = 0; i < b.length; i++) {
			c[i] = b[i].intValue();
		}
		return c;
	}

	public static double[] ExceptDouble(double[] a1, double[] a2) {
		double[] b1 = a1.clone();
		double[] b2 = a2.clone();
		ArrayList<Double> list = new ArrayList<Double>();
		JSort.SmallToBig(b1);
		JSort.SmallToBig(b2);
		double b2min = JStatistics.Min(b2);
		double b2max = JStatistics.Max(b2);
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] < b2min || b1[i] > b2max)
				list.add(b1[i]);
			else {
				boolean contain = false;
				for (int j = 0; j < b2.length; j++) {
					if (b1[i] == b2[j])
						contain = true;
				}
				if (!contain)
					list.add(b1[i]);
			}
		}
		list.trimToSize();
		Double[] b = (Double[]) list.toArray(new Double[list.size()]);
		double[] c = new double[b.length];
		for (int i = 0; i < c.length; i++)
			c[i] = b[i].doubleValue();
		return c;
	}

	// 计算数组和
	public static int[] PlusInt(int[] a1, int[] a2) {
		int[] b1 = a1.clone();
		int[] b2 = a2.clone();
		int[] b = new int[b1.length + b2.length];
		for (int i = 0; i < b.length; i++) {
			if (i < b1.length)
				b[i] = b1[i];
			else
				b[i] = b2[i - b1.length];
		}
		return b;
	}

	public static double[] PlusDouble(double[] a1, double[] a2) {
		double[] b1 = a1.clone();
		double[] b2 = a2.clone();
		double[] b = new double[b1.length + b2.length];
		for (int i = 0; i < b.length; i++) {
			if (i < b1.length)
				b[i] = b1[i];
			else
				b[i] = b2[i - b1.length];
		}
		return b;
	}

	// 计算数组交
	public static int[] IntersectInt(int[] a1, int[] a2) {
		int[] b1 = a1.clone();
		int[] b2 = a2.clone();
		ArrayList<Integer> list = new ArrayList<Integer>();
		JSort.SmallToBig(b1);
		JSort.SmallToBig(b2);
		int b2min = JStatistics.Min(b2);
		int b2max = JStatistics.Max(b2);
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] > b2min && b1[i] < b2max) {
				boolean contain = false;
				for (int j = 0; j < b2.length; j++) {
					if (b1[i] == b2[j])
						contain = true;
				}
				if (contain)
					list.add(b1[i]);
			}
		}
		list.trimToSize();
		Integer[] b = (Integer[]) list.toArray(new Integer[list.size()]);
		int[] c = new int[b.length];
		for (int i = 0; i < b.length; i++) {
			c[i] = b[i].intValue();
		}
		return c;
	}

	public static double[] IntersectDouble(double[] a1, double[] a2) {
		double[] b1 = a1.clone();
		double[] b2 = a2.clone();
		ArrayList<Double> list = new ArrayList<Double>();
		JSort.SmallToBig(b1);
		JSort.SmallToBig(b2);
		double b2min = JStatistics.Min(b2);
		double b2max = JStatistics.Max(b2);
		for (int i = 0; i < b1.length; i++) {
			if (b1[i] > b2min && b1[i] < b2max) {
				boolean contain = false;
				for (int j = 0; j < b2.length; j++) {
					if (b1[i] == b2[j])
						contain = true;
				}
				if (contain)
					list.add(b1[i]);
			}
		}
		list.trimToSize();
		Double[] b = (Double[]) list.toArray(new Double[list.size()]);
		double[] c = new double[b.length];
		for (int i = 0; i < c.length; i++)
			c[i] = b[i].doubleValue();
		return c;
	}

	// 计算数组并
	public static int[] UnionInt(int[] a1, int[] a2) {
		int[] b1 = a1.clone();
		int[] b2 = a2.clone();
		int[] b3 = ExceptInt(b1, b2);
		int[] b = PlusInt(b3, b2);
		return b;
	}

	public static double[] UnionDouble(double[] a1, double[] a2) {
		double[] b1 = a1.clone();
		double[] b2 = a2.clone();
		double[] b3 = ExceptDouble(b1, b2);
		double[] b = PlusDouble(b3, b2);
		return b;
	}

	// 把double[] 转为 ArrayList<Double>
	public static ArrayList<Double> Double2List(double[] a) {
		ArrayList<Double> list = new ArrayList<Double>();
		for (int i = 0; i < a.length; i++) {
			list.add(a[i]);
		}
		return list;
	}

	// 取数组前面num个数值组成另外一个数组
	public static double[] CutPrevious(double[] a, int num) {
		double[] b = new double[num];
		for (int i = 0; i < num; i++)
			b[i] = a[i];
		return b;
	}

	// 取数组后面num个数值组成另外一个数组
	public static double[] CutBehind(double[] a, int num) {
		double[] b = new double[num];
		for (int i = 0; i < num; i++)
			b[i] = a[a.length - i - 1];
		return b;
	}

	/**
	 * //对已排序数组进行删重处理 public static ArrayList<Point2D.Double>
	 * RemoveSamePoint(ArrayList<Point2D.Double> a) { ArrayList<Point2D.Double>
	 * b = new ArrayList<Point2D.Double>(); for(int i = 1 ; i < a.size(); i++) {
	 * double dx = a.get(i).x - a.get(i - 1).x; double dy = a.get(i).y - a.get(i
	 * - 1).y; double d = Math.sqrt(dx * dx + dy * dy); if(d < EPS_DISTANCE) {
	 * b.add(a.get(i)); } } for(int i = 0; i < b.size(); i++) {
	 * a.remove(b.get(i)); } b.clear(); return a; }
	 */
}
