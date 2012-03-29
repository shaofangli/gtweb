package org.controls;

import net.sf.json.JSONArray;

import org.beans.Gt_w_lx;
import org.commons.Statics;
import org.commons.model.Node;
import org.daos.impls.LxglDao;
import org.msf.web.ControlMapping;
import org.msf.web.ControlMarked;
import org.msf.web.ControlParam;
import org.utils.MyUtil;

/**
 * 类型管理
 * 
 * @author lsf
 * 
 */
@ControlMarked(path = "/fast/lxgl")
public class Lxglcontrol {
	LxglDao dao = new LxglDao();

	@ControlMapping(path = "/list")
	public String list(ControlParam params) {
		try {
			int rpage = Integer.parseInt(MyUtil
					.setEmpty(params.get("rpage"), 1).toString());
			int limit = Integer.parseInt(MyUtil.setEmpty(params.get("limit"),
					Statics.pcount).toString());
			params.set("plimit", Statics.pcount);
			return params.bean2jsonstr(dao.getGridDataModel(Gt_w_lx.class,
					rpage, limit, params.getMap("l.nm"),1));
		} catch (Exception x) {
			return MyUtil.returnMsg(x.getMessage(), "red");
		}
	}

	@ControlMapping(path = "/tree")
	public String tree(ControlParam params) {
		try {
			Node pnode = new Node();
			dao.treedata(Integer.valueOf(Statics.DB_TB_MAP.get(
					params.get("pid")).toString()), pnode);
			return JSONArray.fromObject(pnode.getChildren()).toString()
					.replaceAll("\"children\":\\[\\],", "");
		} catch (Exception x) {
			return MyUtil.returnMsg(x.getMessage(), "red");
		}
	}

	@ControlMapping(path = "/insert")
	public String insert(ControlParam params) {
		try {
			if (!dao.caninsert(Integer.valueOf(params.get("pid")))) {
				return MyUtil.returnMsg("目标父节点不支持添加子节点!", "red");
			}
			if (dao.insert(params.params2bean(Gt_w_lx.class))) {
				return "添加类型成功!";
			} else {
				return MyUtil.returnMsg("添加类型失败!", "red");
			}
		} catch (Exception x) {
			return MyUtil.returnMsg(x.getMessage(), "red");
		}
	}

	@ControlMapping(path = "/update")
	public String update(ControlParam params) {
		try {
			// 不能修改默认的系统类型父节点
			if (Statics.isSysLx(params.get("id"))) {
				return MyUtil.returnMsg("不能修改系统默认的类型!", "red");
			}
			if (params.get("pid").equals(params.get("id"))) {
				return MyUtil.returnMsg("不能将父节点设置为自己本身!", "red");
			}
			if (!dao.caninsert(Integer.valueOf(params.get("pid")))) {
				return MyUtil.returnMsg("目标父节点不支持添加子节点!", "red");
			}
			if (dao.update(params.params2bean(Gt_w_lx.class))) {
				return "更新类型成功!";
			} else {
				return MyUtil.returnMsg("更新类型失败!", "red");
			}
		} catch (Exception x) {
			return MyUtil.returnMsg(x.getMessage(), "red");
		}
	}

	@ControlMapping(path = "/delete")
	public String delete(ControlParam params) {
		try {
			int dellen = Integer.valueOf(params.get("ids_len"));
			if (dellen == 0)
				return "没有选择!";
			// 不能删除默认的系统类型
			if (Statics.isSysLx(params.get("ids").split(","))) {
				return MyUtil.returnMsg("不能删除系统默认的类型!", "red");
			}

			if (dellen == 1) {
				if (dao.delete(params.get("ids"))) {
					return "删除一条记录成功!";
				} else {
					return MyUtil.returnMsg("删除一条记录失败!", "red");
				}
			} else if (dellen > 1) {
				String[] delids = params.get("ids").split(",");
				Object[][] o_delids = new Object[delids.length][1];
				for (int i = 0; i < delids.length; i++) {
					o_delids[i][0] = delids[i];
				}
				if (dao.delete(o_delids)) {
					return "删除多条记录成功!(不包含系统默认)</B>";
				} else {
					return MyUtil.returnMsg("删除多条记录失败!", "red");
				}
			}
		} catch (Exception x) {
			return MyUtil.returnMsg(x.getMessage(), "red");
		}
		return null;
	}
}
