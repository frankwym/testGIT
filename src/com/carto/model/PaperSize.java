package com.carto.model;

public class PaperSize {

	public static int a3w = 420;//A4纸横向宽297mm
	public static int a3h = 297;//A4纸横向高210mm
	
	public static int a4w = 297;//A4纸横向宽297mm
	public static int a4h = 210;//A4纸横向高210mm
	
	private static double rate = 0.03937;//1mm等于0.03937inch
	
	private int width = 0;
	private int height = 0;
	
	public PaperSize(String size, int dpi,int type){
		if(size.equals("A3")){
			switch (type) {
			case 0://横向
				this.setWidth((int)(a3w*rate*dpi));
				this.setHeight((int)(a3h*rate*dpi));
				break;
			case 1://纵向
				this.setWidth((int)(a3h*rate*dpi));
				this.setHeight((int)(a3w*rate*dpi));
				break;
			default:
				break;
			}
		}else if(size.equals("A4")){
			switch (type) {
			case 0://横向
				this.setWidth((int)(a4w*rate*dpi));
				this.setHeight((int)(a4h*rate*dpi));
				break;
			case 1://纵向
				this.setWidth((int)(a4h*rate*dpi));
				this.setHeight((int)(a4w*rate*dpi));
				break;
			default:
				break;
			}
		}else{
			switch (type) {
			case 0://横向
				this.setWidth((int)(a4w*rate*dpi));
				this.setHeight((int)(a4h*rate*dpi));
				break;
			case 1://纵向
				this.setWidth((int)(a4h*rate*dpi));
				this.setHeight((int)(a4w*rate*dpi));
				break;
			default:
				break;
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
	
	public static void main(String[] args){
		PaperSize ps = new PaperSize("A3", 300, 0);
		System.out.println(ps.getHeight()+";"+ps.getWidth());
	}
}
