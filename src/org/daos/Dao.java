package org.daos;

import java.util.Map;

import org.commons.model.GridDataModel;

public interface Dao {
	/**
	 * 分页查询
	 * 
	 * @param <T>
	 * @param beanClass
	 * @param page
	 * @param pcount
	 * @param querymap
	 * @param args
	 * @return
	 */
	public <T> GridDataModel<T> getGridDataModel(Class<T> beanClass, int page,
			int pcount, Map<String, Object> querymap, Object... args);
}