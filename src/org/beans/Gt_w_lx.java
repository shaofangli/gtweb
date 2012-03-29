package org.beans;

public class Gt_w_lx {

	private int id;// 编号
	private String nm;// 类型名称
	private String mx;// 类型描述
	private int pid;// ；类型上级ID
	private String lxnm;// 上级类型中文名称
	private int bz;// 是否允许有子节点1:允许0不允许

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNm() {
		return nm;
	}

	public void setNm(String nm) {
		this.nm = nm;
	}

	public String getMx() {
		return mx;
	}

	public void setMx(String mx) {
		this.mx = mx;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getBz() {
		return bz;
	}

	public void setBz(int bz) {
		this.bz = bz;
	}

	public String getLxnm() {
		return lxnm;
	}

	public void setLxnm(String lxnm) {
		this.lxnm = lxnm;
	}

}
