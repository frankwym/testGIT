package com.util;


import java.awt.geom.Rectangle2D;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import com.dbconn.ConnOrcl;

public class JUtil {
	public final static String CONTENT_TYPE_IMAGE_JPEG = "image/png;charset=utf-8";
	public final static double EPS_CLOSEINTEGER = 1.0e-3;

	public static Rectangle2D.Double StringToRect(String str) {
		// System.out.println(str);
		String[] WCStrings = str.split(",");
		double[] a = new double[4];
		for (int i = 0; i < a.length; i++) {
			a[i] = Double.parseDouble(WCStrings[i]);
		}
		Rectangle2D.Double rect = new Rectangle2D.Double(a[0], a[1], a[2]
				- a[0], a[3] - a[1]);
		return rect;
	}

	// 对数据进行处理，返回数值对应的位置
	public static int Indexof(double[] a, double b) {
		int index = -1;
		for (int i = 0; i < a.length; i++) {
			if (a[i] == b)
				index = i;
		}
		return index;
	}

	/**
	 * com.map用 色彩转换方法,转换成rgb数组模式
	 * 
	 * @param rgb
	 * @return int[]
	 * @throws
	 * @since 1.0
	 */
	public static int[] GetRGB(int rgb) {
		int[] retval = new int[3];
		int R = (rgb & 0xff0000) >> 16;
		if (R > 256) {
			System.out.println("Wrong color value R: " + R);
			R = 256;
		}
		retval[0] = R;
		// int g = (color % (256*256))/256;
		int G = (rgb & 0xff00) >> 8;
		if (G > 256) {
			System.out.println("Wrong color value G:" + G);
			G = 256;
		}
		retval[1] = G;
		// int r = color %(256*256)%256;
		int B = (rgb & 0xff);
		if (B > 256) {
			System.out.println("Wrong color value B: " + B);
			B = 256;
		}
		retval[2] = B;
		return retval;
	}

	/**
	 * 获取WebRoot所在路径
	 * 
	 * @return
	 */
	public static String GetWebInfPath() {
		String path = new JUtil().getClass().getClassLoader().getResource("").getPath();
		return path;
	}
	/**
	 * 获取图片所在路径
	 * 
	 * @return
	 */
	public static String GetImgPath() {
		String path = new JUtil().getClass().getClassLoader().getResource("")
				.getPath();
		path = path.replace("WEB-INF/classes/", "flex/assets/");
		path = path.replace("%20", " ");// 除空格
		return path;
	}
	/**
	 * 判断该主题区域底图是否有详细程度区别 <br>
	 * 直辖市、地级市有3到4级的详细程度，省、全国没有详细程度区别 <br>
	 * 有详细程度区别为true，没有详细程度区分为false
	 * 
	 * @param tofPath
	 * @return
	 */
	public static boolean IsScale(String tofPath) {
		String region = tofPath.split("/")[tofPath.split("/").length - 1];
		boolean flag1 = region.indexOf("0000") >= 0;
		boolean flag2 = region.indexOf("110000") >= 0 || region.indexOf("120000") >= 0
				|| region.indexOf("310000") >= 0 || region.indexOf("500000") >= 0;// 判断是否是直辖市
		boolean flag = !flag1 || flag2;
		return flag;
	}
	/**
	 * 通过取值判断该指标是否应该用整数表示
	 * @param values
	 * @return
	 */
	public static boolean IsIntegerOnly(double[] values)
	{
		double sum = 0;
		for (int i = 0; i < values.length; i++)
			sum = sum + Math.abs(values[i] - (int)(values[i] + 0.5));
		boolean flag = sum < EPS_CLOSEINTEGER;
		return flag;
	}
	/**
	 * 通过取值判断该数是否应该用整数表示，应该返回true，不应该返回false
	 * @param value
	 * @return
	 */
	public static boolean IsInteger(double value)
	{
		double eps = Math.abs(value - (int)(value + 0.5));
		boolean flag = eps < EPS_CLOSEINTEGER;
		return flag;
	}
	/**
	 * 返回舍入处理后的值字符串（20150306修改）
	 * <br>整数直接存为整数
	 * <br>大于0.1的小数存为小数，小数位保留一位
	 * <br>大于0.01且小于0.1的小数精确到百分位
	 * @param tipValue 需要进行舍入处理的tip值
	 * @return
	 */
	public static String GetDecimalTipValueStr(double tipValue)
	{
		boolean flag = tipValue < 0;
		tipValue = Math.abs(tipValue);
		double value = tipValue >= 0.1 ? (int)(tipValue * 10 + 0.5) / 10d : (int)(tipValue * 100 + 0.5) / 100d;//转成仅1位小数
		String str = value + "";
		if(IsInteger(value))
			str = (int)(value+0.5) + "";
		str = flag ? "-" + str : str;
		return str;
	}
	/**
	 * 返回舍入处理后的值，当大于4位数字时，取4位数字
	 * @param tipValue 需要进行舍入处理的tip值
	 * @return
	 */
	public static double GetDecimalTipValue(double tipValue) {
		boolean flag = false;
		double value = tipValue;
		if (value < 0) {
			value = -value;
			flag = true;
		}
		double a = 0;
		if (value >= 1000)
			a = (int) (value + 0.5);
		else if (value >= 100)
			a = (int) (value * 10 + 0.5) / 10d;
		else if (value >= 10)
			a = (int) (value * 100 + 0.5) / 100d;
		else if (value >= 1)
			a = (int) (value * 1000 + 0.5) / 1000d;
		else
			a = (int) (value * 10000 + 0.5) / 10000d;
		if (flag) {
			a = -a;
		}
		return a;
	}
	/**
	 * 查询是否有表table
	 * @param stmt
	 * @param table
	 */
	public static boolean IsExist(String table)
	{
		ConnOrcl co = new ConnOrcl();
		Statement stmt = co.getStmt();
		String sql = "SELECT COUNT(*)\n" +
		"FROM USER_TABLES\n" +
		"WHERE TABLE_NAME = '" + table + "'";
		boolean exist = false;
		try {
			ResultSet rs = stmt.executeQuery(sql);//是否存在表
			while(rs.next())
				exist = (rs.getInt(1) >= 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		co.close();
		return exist;
	}
	
	/**
	 * 删除Region后边的0
	 * @param region
	 * @return
	 */
	public static String CutZero(String region)
	{
		String str = "";
		int temp = Integer.parseInt(region);
		if(temp % 1000000 == 0)
			str = "%";
		else if (temp % 10000 == 0)
			str = temp / 10000 + "%";
		else if (temp % 100 == 0)
			str = temp / 100 + "%";
		else
			str = region;
		return str;
	}
	/**
	 * 字符串为空，返回true
	 * 
	 * @param str
	 * @return
	 */
	public static boolean IsNull(String str) {
		return str == null || str.length() <= 0 || str.equalsIgnoreCase("null");
	}	
}