package org.daos.impls;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.beans.Gt_w_wz;
import org.commons.Statics;
import org.daos.AbstractDao;
import org.ext.cache.MyCacheManager;
import org.ext.dbutil.QueryHelper;

public class WzglDao extends AbstractDao {
	// 根据ID查找文章内容
	public static final String _fcbid = "select nr from gt_w_wz where id=?";
	// 列出所有文章
	public static final String sql1 = "select w.id,w.bt,w.lx,w.gxrq,w.czr,l.nm lxnm from gt_w_wz w inner join gt_m_lx l on w.lx = l.id where 1=?";
	// 求出文章的总数
	public static final String csql1 = "select count(id) from gt_w_wz where 1=?";
	// 查出某个树节点下的所有文章
	public static final String sql2 = "select * from gt_w_wz where lx in(SELECT id FROM gt_m_lx where pid=? or id = ?)";
	// 求和查出某个树节点下的所有文章
	public static final String csql2 = "select count(*) from gt_w_wz where lx in(SELECT id FROM gt_m_lx where pid=? or id = ?)";

	private String _sql = null;
	private String _csql = null;

	/**
	 * 新增数据
	 * 
	 * @param bean
	 * @return
	 */
	public boolean insert(Gt_w_wz bean) throws Exception {
		boolean bol = Boolean.FALSE;
		int rs = QueryHelper
				.update(
						Statics.dbNm,
						"insert into gt_w_wz(bt,lx,nr,gxrq,czr) values(?,?,?,now(),'admin')",
						bean.getBt(), bean.getLx(), bean.getNr());
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
	public boolean update(Gt_w_wz bean) throws Exception {
		boolean bol = Boolean.FALSE;
		int rs = QueryHelper
				.update(
						Statics.dbNm,
						"update gt_w_wz set bt=?,lx=?,nr=?,gxrq=now(),czr='admin' where id=?",
						bean.getBt(), bean.getLx(), bean.getNr(), bean.getId());
		if (rs > 0) {
			clearWzCache(bean.getId());
			return Boolean.TRUE;
		}
		return bol;
	}

	/**
	 * 根据ID获取文章内容;
	 * 
	 * @param sid
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getContent(int sid) throws UnsupportedEncodingException {
		return QueryHelper.queryList_cache(Statics.dbNm, Gt_w_wz.class,
				"WZ_CACHE", "KEY_WEB_WZ_" + sid, _fcbid, sid).get(0).getNr()
				.toString();
	}

	/**
	 * 根据类型ID获得单个文章ID
	 * 
	 * @param lxid
	 * @return
	 */
	public Gt_w_wz getSWzByLxId(int lxid) {
		List<Gt_w_wz> wzs = QueryHelper.queryList_cache(Statics.dbNm,
				Gt_w_wz.class, "WZ_CACHE", "KEY_WEB_WZLX_" + lxid,
				"SELECT * FROM gt_w_wz g where lx = ? limit 1", lxid);
		if (!wzs.isEmpty())
			return wzs.get(0);
		return null;
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
				"delete from gt_w_wz where id=?", delid);
		if (rs > 0) {
			clearWzCache(Integer.valueOf(delid));
			return Boolean.TRUE;
		}
		return bol;
	}

	/**
	 * 删除多条记录
	 * 
	 * @param delids
	 * @return
	 */
	public boolean delete(Object[][] delids) throws Exception {
		int[] rs = QueryHelper.batch(Statics.dbNm,
				"delete from gt_w_wz where id=?", delids);
		if (rs.length == delids.length)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}

	public String get_sql() {
		return _sql == null ? sql1 : _sql;
	}

	public String get_csql() {
		return _csql == null ? csql1 : _csql;
	}

	public void initsql(String _sql, String _csql) {
		this._sql = _sql;
		this._csql = _csql;
	}

	/**
	 * 清除文章缓存
	 */
	private void clearWzCache(int sid) {
		MyCacheManager.clear("WZ_CACHE", "KEY_WEB_WZ_" + sid);
	}
}
