package com.util;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * 处理排序的类，包括数组、坐标等东西的排序。
 * 
 * @author lmk
 * @version 1.0
 */
public class JSort {

	// 从大到小排序
	public static int[] BigToSmall(int[] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = i + 1; j < a.length; j++)
				if (a[i] < a[j]) {
					int temp = a[j];
					a[j] = a[i];
					a[i] = temp;
				}
		}
		return a;
	}

	public static double[] BigToSmall(double[] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = i + 1; j < a.length; j++)
				if (a[i] < a[j]) {
					double temp = a[j];
					a[j] = a[i];
					a[i] = temp;
				}
		}
		return a;
	}

	// 从小到大排序
	public static int[] SmallToBig(int[] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = i + 1; j < a.length; j++)
				if (a[i] > a[j]) {
					int temp = a[j];
					a[j] = a[i];
					a[i] = temp;
				}
		}
		return a;
	}

	public static double[] SmallToBig(double[] a) {
		for (int i = 0; i < a.length; i++) {
			for (int j = i + 1; j < a.length; j++)
				if (a[i] > a[j]) {
					double temp = a[j];
					a[j] = a[i];
					a[i] = temp;
				}
		}
		return a;
	}

	// 对点进行排序，依据X值， 从小到大
	public static ArrayList<Point2D.Double> SmallToBigByX(
			ArrayList<Point2D.Double> a) {
		for (int i = 0; i < a.size(); i++) {
			for (int j = i; j < a.size(); j++)
				if (a.get(j).x < a.get(i).x) {
					Point2D.Double temPt = a.get(i);
					a.set(i, a.get(j));
					a.set(j, temPt);
				}
		}
		return a;
	}

	// 对点进行排序，依据X值， 从大到小
	public static ArrayList<Point2D.Double> BigToSmallByX(
			ArrayList<Point2D.Double> a) {
		for (int i = 0; i < a.size(); i++) {
			for (int j = i; j < a.size(); j++)
				if (a.get(j).x > a.get(i).x) {
					Point2D.Double temPt = a.get(i);
					a.set(i, a.get(j));
					a.set(j, temPt);
				}
		}
		return a;
	}

	// 对点进行排序，依据X值， 从小到大
	public static ArrayList<Point2D.Double> SmallToBigByY(
			ArrayList<Point2D.Double> a) {
		for (int i = 0; i < a.size(); i++) {
			for (int j = i; j < a.size(); j++)
				if (a.get(j).y < a.get(i).y) {
					Point2D.Double temPt = a.get(i);
					a.set(i, a.get(j));
					a.set(j, temPt);
				}
		}
		return a;
	}

	// 对点进行排序，依据X值， 从大到小
	public static ArrayList<Point2D.Double> BigToSmallByY(
			ArrayList<Point2D.Double> a) {
		for (int i = 0; i < a.size(); i++) {
			for (int j = i; j < a.size(); j++)
				if (a.get(j).y > a.get(i).y) {
					Point2D.Double temPt = a.get(i);
					a.set(i, a.get(j));
					a.set(j, temPt);
				}
		}
		return a;
	}
}
