package org.beans;

import java.io.UnsupportedEncodingException;

import org.msf.web.RestProcess;

public class Gt_w_wz {

	private int id;// 文章编号
	private String bt;// 文章标题
	private int lx;// 文章所属类型ID
	private String lxnm;// 类型中文名称
	private String gxrq;// 更新日期
	private String czr;// 操作人
	private Object nr;// 文章内容

	public Object getNr() throws UnsupportedEncodingException {
		if (this.nr instanceof byte[]) {
			return new String(((byte[]) this.nr), RestProcess
					.getDefaultCharSet());
		} else {
			return this.nr;
		}
	}

	public void setNr(Object nr) {
		this.nr = nr;
	}

	public String getBt() {
		return bt;
	}

	public void setBt(String bt) {
		this.bt = bt;
	}

	public String getCzr() {
		return czr;
	}

	public void setCzr(String czr) {
		this.czr = czr;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLx() {
		return lx;
	}

	public void setLx(int lx) {
		this.lx = lx;
	}

	public String getGxrq() {
		return gxrq;
	}

	public void setGxrq(String gxrq) {
		this.gxrq = gxrq;
	}

	public String getLxnm() {
		return lxnm;
	}

	public void setLxnm(String lxnm) {
		this.lxnm = lxnm;
	}

}
