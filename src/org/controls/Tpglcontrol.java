package org.controls;

import java.io.IOException;

import org.beans.Gt_w_tp;
import org.commons.Fileupload;
import org.commons.Statics;
import org.daos.impls.TpglDao;
import org.msf.web.ControlMapping;
import org.msf.web.ControlMarked;
import org.msf.web.ControlParam;
import org.utils.MyUtil;

/**
 * 图片管理
 * 
 * @author Administrator
 * 
 */
@ControlMarked(path = "/fast/tpgl")
public class Tpglcontrol {
	TpglDao dao = new TpglDao();
	Fileupload upload = new Fileupload();

	@ControlMapping(path = "/list")
	public String list(ControlParam params) {
		try {
			int rpage = Integer.parseInt(MyUtil
					.setEmpty(params.get("rpage"), 1).toString());
			int limit = Integer.parseInt(MyUtil.setEmpty(params.get("limit"),
					Statics.pcount).toString());

			return params.bean2jsonstr(dao.getGridDataModel(Gt_w_tp.class,
					rpage, limit, params.getMap("t.nm"), 1));
		} catch (Exception x) {
			return MyUtil.returnMsg(x.getMessage(), "red");
		}
	}

	@ControlMapping(path = "/insert")
	public String insert(ControlParam params) {
		try {
			if (dao.insert(params.params2bean(Gt_w_tp.class))) {
				return "添加图片成功!";
			} else {
				return MyUtil.returnMsg("添加图片失败!", "red");
			}
		} catch (Exception x) {
			return MyUtil.returnMsg(x.getMessage(), "red");
		}
	}

	@ControlMapping(path = "/update")
	public String update(ControlParam params) {
		try {
			if (dao.update(params.params2bean(Gt_w_tp.class))) {
				if (!MyUtil.isEmpty(params.get("durl"))
						&& !params.get("zsurl").equalsIgnoreCase(
								params.get("src"))) {
					upload.tosmallerpic(params.getRealPathByParam("zsurl"),
							params.getRealPathByParam("durl"), params
									.getRealPathByParam("surl"), null,
							Statics.TP_SY_WIDTH, Statics.TP_SY_HEIGHT, 0.65f);
				}
				return "更新图片成功!";
			} else {
				return MyUtil.returnMsg("更新图片失败!", "red");
			}
		} catch (Exception x) {
			return MyUtil.returnMsg(x.getMessage(), "red");
		}
	}

	@ControlMapping(path = "/getsrc/{sid}")
	public String getcontent(ControlParam params) {
		try {
			return dao.getSrc(params.getPathParam("sid"));
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

	@ControlMapping(path = "/uploadimg")
	public String uploadImg(ControlParam params) {
		try {
			String srcs = upload.defaultProcessFileUpload(params
					.getHttpRequest());
			if ("onerror".equals(params.get("testcase")))
				throw new IOException("io异常!");
			if (srcs == null)
				throw new Exception("上传失败!");
			return srcs;
		} catch (Exception x) {
			return MyUtil.returnMsg("上传失败:" + x.getMessage(), "red");
		}
	}
}
