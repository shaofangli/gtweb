package org.controls;

import org.beans.Gt_w_wz;
import org.commons.Statics;
import org.daos.impls.WzglDao;
import org.msf.web.ControlMapping;
import org.msf.web.ControlMarked;
import org.msf.web.ControlParam;
import org.utils.MyUtil;

/**
 * 文章管理
 * 
 * @author Administrator
 * 
 */
@ControlMarked(path = "/fast/wzgl")
public class Wzglcontrol {
	WzglDao dao = new WzglDao();

	@ControlMapping(path = "/list")
	public String list(ControlParam params) {
		try {
			int rpage = Integer.parseInt(MyUtil
					.setEmpty(params.get("rpage"), 1).toString());
			int limit = Integer.parseInt(MyUtil.setEmpty(params.get("limit"),
					Statics.pcount).toString());

			return params.bean2jsonstr(dao.getGridDataModel(Gt_w_wz.class,
					rpage, limit, params.getMap("bt"), 1));
		} catch (Exception x) {
			return MyUtil.returnMsg(x.getMessage(), "red");
		}
	}

	@ControlMapping(path = "/insert")
	public String insert(ControlParam params) {
		try {
			if (dao.insert(params.params2bean(Gt_w_wz.class))) {
				return "添加文章成功!";
			} else {
				return MyUtil.returnMsg("添加文章失败!", "red");
			}
		} catch (Exception x) {
			return MyUtil.returnMsg(x.getMessage(), "red");
		}
	}

	@ControlMapping(path = "/update")
	public String update(ControlParam params) {
		try {
			if (dao.update(params.params2bean(Gt_w_wz.class))) {
				return "更新文章成功!";
			} else {
				return MyUtil.returnMsg("更新文章失败!", "red");
			}
		} catch (Exception x) {
			return MyUtil.returnMsg(x.getMessage(), "red");
		}
	}

	@ControlMapping(path = "/getcontent/{sid}")
	public String getcontent(ControlParam params) {
		try {
			return dao.getContent(Integer.valueOf(params.getPathParam("sid")));
		} catch (Exception x) {
			return MyUtil.returnMsg("服务响应异常:" + x.getMessage(), "red");
		}
	}

	@ControlMapping(path = "/delete")
	public String delete(ControlParam params) {
		try {
			int dellen = Integer.valueOf(params.get("ids_len"));
			if (dellen == 0)
				return "没有选择!";
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
					return "删除多条记录成功!";
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
