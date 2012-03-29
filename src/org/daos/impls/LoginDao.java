package org.daos.impls;

import org.beans.Gt_m_yh;
import org.commons.Statics;
import org.ext.dbutil.QueryHelper;

public class LoginDao {
	private static final String sql = "select count(*) from gt_m_yh where zh=? and mm=?";

	/**
	 * 判断是否存在该用户
	 * 
	 * @param zh
	 * @param mm
	 * @return
	 */
	public boolean islogin(String zh, String mm) {
		if (QueryHelper.total(Statics.dbNm, sql, zh, mm) == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 更新最后登陆时间
	 * 
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public boolean update(Gt_m_yh bean) throws Exception {
		int rs = QueryHelper.update(Statics.dbNm,
				"update gt_m_yh set zhdl=now() where zh=? and mm=?", bean
						.getZh(), bean.getMm());
		if (rs > 0)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}
}
