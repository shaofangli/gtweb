package org.daos.impls;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.beans.Gt_w_tp;
import org.commons.Statics;
import org.daos.AbstractDao;
import org.ext.dbutil.QueryHelper;

public class TpglDao extends AbstractDao {
	// 根据ID查找图片地址
	private static final String _ftbid = "select src from gt_w_tp where id=?";
	// 列出所有图片
	private static final String _sql = "select t.id,t.nm,t.src,t.gxrq,t.czr,t.lx,t.href,l.nm lxnm from gt_w_tp t inner join gt_m_lx l on t.lx = l.id where 1=?";
	// 求出图片的总数
	private static final String _csql = "select count(id) from gt_w_tp t where 1=?";

	/**
	 * 根据图片类型查找图片
	 * 
	 * @param lx
	 * @return
	 */
	public List<Gt_w_tp> getTpByLx(String lx) {
		return QueryHelper.query(Statics.dbNm, Gt_w_tp.class,
				"select id,nm,src,gxrq,czr,lx,href from gt_w_tp where 1=? and lx=? limit "
						+ Statics.csyimg, 1, lx);
	}

	/**
	 * 新增数据
	 * 
	 * @param bean
	 * @return
	 */
	public boolean insert(Gt_w_tp bean) throws Exception {
		boolean bol = Boolean.FALSE;
		int rs = QueryHelper
				.update(
						Statics.dbNm,
						"insert into gt_w_tp(nm,src,lx,gxrq,czr,href) values(?,?,?,now(),'admin',?)",
						bean.getNm(), bean.getSrc(), bean.getLx(), bean
								.getHref());
		if (rs > 0)
			return Boolean.TRUE;
		return bol;
	}

	/**
	 * 更新数据
	 * 
	 * @param bean
	 * @return
	 */
	public boolean update(Gt_w_tp bean) throws Exception {
		boolean bol = Boolean.FALSE;
		int rs = QueryHelper
				.update(
						Statics.dbNm,
						"update gt_w_tp set nm=?,src=?,lx=?,gxrq=now(),czr='admin',href=? where id=?",
						bean.getNm(), bean.getSrc(), bean.getLx(), bean
								.getHref(), bean.getId());
		if (rs > 0)
			return Boolean.TRUE;
		return bol;
	}

	/**
	 * 根据ID获取图片地址;
	 * 
	 * @param sid
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getSrc(String sid) throws UnsupportedEncodingException {
		return QueryHelper.query(Statics.dbNm, Gt_w_tp.class, _ftbid, sid).get(
				0).getSrc();
	}

	/**
	 * 删除一条记录
	 * 
	 * @param delid
	 * @return
	 */
	public boolean delete(String delid) throws Exception {
		boolean bol = Boolean.FALSE;
		int rs = QueryHelper.update(Statics.dbNm,
				"delete from gt_w_tp where id=?", delid);
		if (rs > 0)
			return Boolean.TRUE;
		return bol;
	}

	/**
	 * 删除多条记录
	 * 
	 * @param delids
	 * @return
	 */
	public boolean delete(Object[][] delids) throws Exception {
		boolean bol = Boolean.FALSE;
		int[] rs = QueryHelper.batch(Statics.dbNm,
				"delete from gt_w_tp where id=?", delids);
		if (rs.length == delids.length)
			return Boolean.TRUE;
		return bol;
	}

	@Override
	public String get_csql() {
		return _csql;
	}

	@Override
	public String get_sql() {
		return _sql;
	}
}
