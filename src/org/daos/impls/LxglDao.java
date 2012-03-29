package org.daos.impls;

import java.util.List;

import org.beans.Gt_w_lx;
import org.commons.Statics;
import org.commons.model.Node;
import org.daos.AbstractDao;
import org.ext.dbutil.QueryHelper;

public class LxglDao extends AbstractDao {
	// 列出所有类型
	private static final String _sql = "select l.id,l.nm,l.mx,l.pid,l.bz,l2.nm lxnm from gt_m_lx l left join gt_m_lx l2 on l.pid = l2.id where 1=?";

	// 求出类型的总数
	private static final String _csql = "select count(l.id) from gt_m_lx l where 1=?";

	/**
	 * 递归方法获得循环树
	 * 
	 * @param pid
	 *            父节点id
	 * @param pnode
	 *            传入一个父节点Node
	 */
	public void treedata(int pid, Node pnode) {
		List<Gt_w_lx> data = QueryHelper.query(Statics.dbNm, Gt_w_lx.class,
				"select * from gt_m_lx where pid=?", pid);

		for (Gt_w_lx lx : data) {
			Node node = new Node();
			node.setExpanded(Boolean.TRUE);
			node.setText(lx.getNm());
			node.setValue(String.valueOf(lx.getId()));

			if (QueryHelper.total(Statics.dbNm,
					_csql+" and l.pid=?",1, lx.getId()) > 0) {
				treedata(lx.getId(), node);
			}
			pnode.getChildren().add(node);
		}
	}

	/**
	 * 判断传入的id类型是否可以插入子节点
	 * 
	 * @param bean
	 * @return
	 * @throws Exception
	 */
	public boolean caninsert(int id) throws Exception {
		Gt_w_lx lx = QueryHelper.query(Statics.dbNm, Gt_w_lx.class,
				"SELECT bz FROM gt_m_lx where id=?", id).get(0);
		if (lx.getBz() == 0) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * 新增数据
	 * 
	 * @param bean
	 * @return
	 */
	public boolean insert(Gt_w_lx bean) throws Exception {
		int rs = QueryHelper.update(Statics.dbNm,
				"insert into gt_m_lx(nm,mx,pid,bz) values(?,?,?,?)", bean.getNm(),
				bean.getMx(), bean.getPid(),bean.getBz());
		if (rs > 0)
			return Boolean.TRUE;
		return Boolean.FALSE;
	}

	/**
	 * 更新数据
	 * 
	 * @param bean
	 * @return
	 */
	public boolean update(Gt_w_lx bean) throws Exception {
		boolean bol = Boolean.FALSE;
		int rs = QueryHelper.update(Statics.dbNm,
				"update gt_m_lx set nm=?,mx=?,pid=?,bz=? where id=?", bean.getNm(),
				bean.getMx(), bean.getPid(), bean.getBz(),bean.getId());
		if (rs > 0)
			return Boolean.TRUE;
		return bol;
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
				"delete from gt_m_lx where id=?", delid);
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
				"delete from gt_m_lx where id=?", delids);
		if (rs.length == delids.length)
			return Boolean.TRUE;
		return bol;
	}

	public String get_sql() {
		return _sql;
	}

	public String get_csql() {
		return _csql;
	}
}
