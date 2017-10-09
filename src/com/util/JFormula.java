package com.util;

/**
 * 解方程的类，用于等比分级时确定公比等
 * 
 * @author lmk
 * @version 1.0
 */
public class JFormula {
	private static double EXP = 1e-6;

	// 解方程r + r^2 + …… + r^n = a , 返回解r
	public static double EquationR(int num, double a) {
		double x, x1, x2;
		double det;
		int n = num;
		x1 = 0;
		x2 = a;
		x = a / 2;
		while (x2 - x1 >= EXP) {
			x = (x1 + x2) / 2;
			det = PolynomialsR(n, x) - a;
			if (Math.abs(det) < EXP) {
				break;
			} else if (det > 0) {
				x2 = x;
			} else {
				x1 = x;
			}
		}
		return x;
	}

	// 计算多项式P(a) = a + a^2 + …… + a^n 的值
	public static double PolynomialsR(int num, double a) {
		double b = 0;
		for (int i = 1; i <= num; i++) {
			b = b + Math.pow(a, i);
		}
		return b;
	}

}
