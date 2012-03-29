package org.beans;

import org.utils.MyUtil;

public class Gt_w_tp {

	private int id;// 图片编号
	private String nm;// 图片标题
	private String src;// 图片地址
	private String gxrq;// 更新日期
	private int lx;// 图片所属类型
	private String lxnm;// 类型中文名称
	private int sx;// 图片组的顺序
	private String czr;// 操作人
	private String href;// 点击图片的超链接地址

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSrc() {
		return MyUtil.isEmpty(src)?null:src.trim();
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getGxrq() {
		return gxrq;
	}

	public void setGxrq(String gxrq) {
		this.gxrq = gxrq;
	}

	public int getLx() {
		return lx;
	}

	public void setLx(int lx) {
		this.lx = lx;
	}

	public int getSx() {
		return sx;
	}

	public void setSx(int sx) {
		this.sx = sx;
	}

	public String getCzr() {
		return czr;
	}

	public void setCzr(String czr) {
		this.czr = czr;
	}

	public String getNm() {
		return nm;
	}

	public void setNm(String nm) {
		this.nm = nm;
	}

	public String getLxnm() {
		return lxnm;
	}

	public void setLxnm(String lxnm) {
		this.lxnm = lxnm;
	}

	public String getHref() {
		return MyUtil.isEmpty(href)?null:href.trim();
	}

	public void setHref(String href) {
		this.href = href;
	}

}
