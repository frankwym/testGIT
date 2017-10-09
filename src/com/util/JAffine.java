package com.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * 仿射变换类,从AI坐标到像素坐标
 *
 * @author L J
 * @version 1.0
 */
public class JAffine {
    private Rectangle2D.Double WC;
    private Rectangle2D.Double DC;

    private double scale;

    public JAffine() {
        setWC(new Rectangle2D.Double());
        setDC(new Rectangle2D.Double());
        scale = 1.0; // 默认比例尺为1.0
    }

    /**
     * 带参数构造函数,初始化wc，dc并计算scale
     *
     * @param wc
     * @param dc
     * @since 1.0
     */
    public JAffine(Rectangle2D.Double wc, Rectangle2D.Double dc) { // 构造函数中直接仿射变换
        this.setWC(wc);
        this.setDC(dc);
        double width = getDC().getWidth();
        double height = getDC().getHeight();
        if (Math.abs(width) < 1e-5 || Math.abs(height) < 1e-5) {
            return;
        }
        double scale1 = getWC().getWidth() / width;
        double scale2 = getWC().getHeight() / height;
        scale = scale1 < scale2 ? scale2 : scale1;// scale作为除数，因此取大的

    }

    /**
     * 对点仿射变换
     *
     * @param pt
     * @return Point2D.Double
     * @throws
     * @since 1.0
     */
    public Point2D.Double TransPoint(Point2D.Double pt) { // 自然坐标点－－>设备坐标点转换
        Point2D.Double pt1 = new Point2D.Double();

        double x = (pt.x - getWC().x) / scale;
        double y = (pt.y - getWC().y) / scale;
        x = getDC().x + x + (getDC().width - getWC().width / scale) / 2;
        y = getDC().y + y + (getDC().height - getWC().height / scale) / 2;// 居中
        y = DC.y * 2 + DC.getHeight() - y;// 图形翻转
        pt1.setLocation(x, y);
        return pt1;
    }

    public double getScale() {// 李坚添加
        return this.scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     *
     * @param rect
     *            AI范围
     * @param Geo范围
     * @return
     */

    public Rectangle2D.Double transRect(Rectangle2D.Double rect) {
        double curX, curY, curWidth, curHeight;
        curWidth = rect.width / scale;
        curHeight = rect.height / scale;
        curX = getDC().x + (rect.x - getWC().x) / scale
                + (getDC().width - getWC().width / scale) / 2;
        curY = getDC().y + (rect.y - getWC().y) / scale
                + (getDC().height - getWC().height / scale) / 2;
        Rectangle2D.Double rect2 = new Rectangle2D.Double(curX, curY, curWidth,
                curHeight);
        return rect2;
    }

    public AffineTransform getAffineTransform() {
        double m00, m10 = 0, m01 = 0, m11, m02, m12;
        m00 = 1d / scale;
        m11 = 1d / scale;
        m02 = getDC().x - getWC().x / scale
                + (getDC().width - getWC().width / scale) / 2;
        m12 = getDC().y - getWC().y / scale
                + (getDC().height - getWC().height / scale) / 2;
        AffineTransform affineTransform = new AffineTransform(m00, m10, m01,
                m11, m02, m12);
        // AffineTransform affineTransform = new AffineTransform();
        return affineTransform;
    }

    public Rectangle2D.Double getAffTransDC() {
        double curX, curY, curWidth, curHeight;
        curWidth = getWC().width / scale;
        curHeight = getWC().height / scale;
        curX = getDC().x + (getDC().width - curWidth) / 2;
        curY = getDC().y + (getDC().height - curHeight) / 2;
        Rectangle2D.Double curDC = new Rectangle2D.Double(curX, curY, curWidth,
                curHeight);
        return curDC;
    }

    public void setWC(Rectangle2D.Double wC) {
        WC = wC;
    }

    public Rectangle2D.Double getWC() {
        return WC;
    }

    public void setDC(Rectangle2D.Double dC) {
        DC = dC;
    }

    public Rectangle2D.Double getDC() {
        return DC;
    }
}
