package org.controls;

import org.beans.Gt_m_yh;
import org.daos.impls.LoginDao;
import org.msf.web.ControlMapping;
import org.msf.web.ControlMarked;
import org.msf.web.ControlParam;

@ControlMarked(path = "/fast/login")
public class Dlglcontrol {
	LoginDao dao = new LoginDao();

	/**
	 * 登陆
	 * 
	 * @param params
	 * @return
	 */
	@ControlMapping(path = "/login")
	public String login(ControlParam params) {
		try {
			if (dao.islogin(params.get("zh"), params.get("mm"))) {
				// 登陆信息存放session或者cookie
				params.getHttpRequest().getSession(true).setAttribute(
						"islogin", true);
				update(params.params2bean(Gt_m_yh.class));
				return "true";
			}
		} catch (Exception x) {
			return "登陆失败:" + x.getMessage();
		}
		return "账号或密码错误!";
	}

	/**
	 * 注销
	 * 
	 * @param params
	 */
	@ControlMapping(path = "/loginout")
	public void loginout(ControlParam params) {
		try {
			params.getHttpRequest().getSession(false)
					.removeAttribute("islogin");
			params.getHttpResponse().sendRedirect("/gtweb/pages/login.vm");
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	private boolean update(Gt_m_yh bean) throws Exception {
		if (dao.update(bean)) {
			return true;
		} else {
			return false;
		}
	}

}
