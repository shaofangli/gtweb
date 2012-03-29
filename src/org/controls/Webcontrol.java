package org.controls;

import java.util.HashMap;

import org.beans.Gt_w_lx;
import org.beans.Gt_w_wz;
import org.commons.Statics;
import org.daos.impls.LxglDao;
import org.daos.impls.TpglDao;
import org.daos.impls.WzglDao;
import org.ext.dbutil.DbFactory;
import org.msf.web.ControlMapping;
import org.msf.web.ControlMarked;
import org.msf.web.ControlParam;
import org.msf.web.ControlRender;

/**
 * web管理
 * 
 * @author lsf
 * 
 */
@ControlMarked(path = "/fast/web")
public class Webcontrol {
	TpglDao tpdao = new TpglDao();
	WzglDao wzdao = new WzglDao();
	LxglDao lxdao = new LxglDao();

	@ControlMapping(isload = true)
	public void load() {
		System.out.println("-->启动时执行开启数据库");
		DbFactory.start();
	}

	@ControlMapping(isdestroy = true)
	public void destroy() {
		System.out.println("-->销毁时执行销毁数据库");
		DbFactory.destroy();
	}

	/**
	 * 跳转到首页
	 * 
	 * @param params
	 * @return
	 */
	@ControlMapping(path = "/index")
	public ControlRender index(ControlParam params) {
		try {
			params.set("tps", tpdao.getTpByLx(Statics.DB_TB_MAP.get(
					"LX_TP_SY_ID").toString()));
		} catch (Exception x) {
			x.printStackTrace();
		}
		return new ControlRender("/pages/web/index.vm");
	}

	/**
	 * 跳转到新闻
	 * 
	 * @param params
	 * @return
	 */
	@ControlMapping(path = "/news")
	public ControlRender news(ControlParam params) {
		try {
			HashMap<String, Object> wheremap = new HashMap<String, Object>();
			wheremap.put("l.pid",Statics.DB_TB_MAP.get("LX_NEWS_ID"));
			
			params.set("wzid", wzdao.getSWzByLxId(
					Statics.DB_TB_MAP.get("LX_NEWS_ID")).getId());
			// 类型列表;
			params.set("lxs", lxdao.getGridDataModel(Gt_w_lx.class, 1, 10,
					wheremap, 1));
		} catch (Exception x) {
			x.printStackTrace();
		}
		return new ControlRender("/pages/web/news.vm");
	}

	/**
	 * 跳转到关于我们
	 * 
	 * @param params
	 * @return
	 */
	@ControlMapping(path = "/about")
	public ControlRender about(ControlParam params) {
		try {
			params.set("wzid", wzdao.getSWzByLxId(
					Statics.DB_TB_MAP.get("LX_GYWM_ID")).getId());
		} catch (Exception x) {
			x.printStackTrace();
		}
		return new ControlRender("/pages/web/about.vm");
	}

	/**
	 * 跳转到联系我们
	 * 
	 * @param params
	 * @return
	 */
	@ControlMapping(path = "/contact")
	public ControlRender contact(ControlParam params) {
		try {
		} catch (Exception x) {
			x.printStackTrace();
		}
		return new ControlRender("/pages/web/contact.vm");
	}

	/**
	 * 跳转到公司资质
	 * 
	 * @param params
	 * @return
	 */
	@ControlMapping(path = "/qualification")
	public ControlRender qualification(ControlParam params) {
		try {
			params.set("wzid", wzdao.getSWzByLxId(
					Statics.DB_TB_MAP.get("LX_GSZZ_ID")).getId());
		} catch (Exception x) {
			x.printStackTrace();
		}
		return new ControlRender("/pages/web/qualification.vm");
	}

	/**
	 * 跳转到解决方案
	 * 
	 * @param params
	 * @return
	 */
	@ControlMapping(path = "/solution")
	public ControlRender solution(ControlParam params) {
		try {
			params.set("wzid", wzdao.getSWzByLxId(
					Statics.DB_TB_MAP.get("LX_JJFA_ID")).getId());
		} catch (Exception x) {
			x.printStackTrace();
		}
		return new ControlRender("/pages/web/solution.vm");
	}

	/**
	 * 跳转到文章列表
	 * 
	 * @param params
	 * @return
	 */
	@ControlMapping(path = "/news/{news_lx}/list")
	public ControlRender news_list(ControlParam params) {
		try {
			// 新闻列表;
			wzdao.initsql(WzglDao.sql2, WzglDao.csql2);
			params.set("xws", wzdao.getGridDataModel(Gt_w_wz.class, 1,
					Statics.pcount, null, params.getPathParam("news_lx"),params.getPathParam("news_lx")));
		} catch (Exception x) {
			x.printStackTrace();
		}
		return new ControlRender("/pages/web/sub/xwlist.vm");
	}
}
