package org.daos;

import java.util.Map;

import org.commons.Statics;
import org.commons.model.GridDataModel;
import org.ext.dbutil.QueryHelper;
import org.utils.MyUtil;

public abstract class AbstractDao implements Dao {
	private final StringBuilder psql = new StringBuilder();
	private final StringBuilder csql = new StringBuilder();

	/**
	 * 分页查询
	 * 
	 * @param <T>
	 * @param beanClass
	 *            封装为bean
	 * @param page
	 *            请求第几页
	 * @param pcount
	 *            每页显示多少条
	 * @param wheremap
	 *            封装sql的where语句 key value 相当于 where key=value
	 * @param args
	 *            参数列表(不包括wheremap里的)
	 * @return
	 */
	public <T> GridDataModel<T> getGridDataModel(Class<T> beanClass, int page,
			int pcount, Map<String, Object> wheremap, Object... args) {
		Object[] parms = initPreSql(wheremap, get_sql(), get_csql(), args);
		GridDataModel<T> localGridDataModel = new GridDataModel<T>();
		if (!MyUtil.isEmpty(parms)) {
			localGridDataModel.setRows(QueryHelper.query_slice(Statics.dbNm,
					beanClass, psql.toString(), page, pcount, parms));
			localGridDataModel.setTotal(QueryHelper.total(Statics.dbNm, csql
					.toString(), parms));
		} else {
			localGridDataModel.setRows(QueryHelper.query_slice(Statics.dbNm,
					beanClass, psql.toString(), page, pcount));
			localGridDataModel.setTotal(QueryHelper.total(Statics.dbNm, csql
					.toString()));
		}
		return localGridDataModel;
	}

	/**
	 * 
	 * @param querymap
	 *            封装sql的where语句 key value 相当于 where key=value
	 * @param _sql
	 *            查出列表的sql
	 * @param _csql
	 *            查出列表总数的sql
	 * @param args
	 *            参数列表(不包括wheremap里的)
	 * @return
	 */
	private Object[] initPreSql(Map<String, Object> querymap, String _sql,
			String _csql, Object... args) {
		Object[] parms = null;
		psql.delete(0, psql.length());
		psql.append(_sql);
		csql.delete(0, csql.length());
		csql.append(_csql);
		int i = 0;
		if (MyUtil.isEmpty(args) && MyUtil.isEmpty(querymap))
			return null;
		if (!MyUtil.isEmpty(args) && MyUtil.isEmpty(querymap))
			return args;

		if (MyUtil.isEmpty(args)) {
			parms = new Object[querymap.size()];
		} else {
			parms = new Object[querymap.size() + args.length];
			for (Object arg : args) {
				parms[i++] = arg;
			}
		}
		for (String key : querymap.keySet()) {
			psql.append(" and ");
			psql.append(key);
			psql.append(" like ? ");

			csql.append(" and ");
			csql.append(key);
			csql.append(" like ? ");

			parms[i++] = "%" + String.valueOf(querymap.get(key)) + "%";
		}
		return parms;
	}

	/**
	 * 获得分页列表的sql
	 */
	public abstract String get_sql();

	/**
	 * 获得分页列表总数的sql
	 * 
	 * @return
	 */
	public abstract String get_csql();
}
