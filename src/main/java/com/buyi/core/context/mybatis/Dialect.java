package com.buyi.core.context.mybatis;

/**
 * 数据库方言
 * 
 * @author maojian
 * @date 2017-08-10 08:49:11
 * @since v1.0.0
 */
public abstract class Dialect {

	/**
	 * 获取LIMIT分页查询语句
	 * 
	 * @param sql 原SQL语句
	 * @param limit 获取记录数
	 * @param offset 开始记录偏移量
	 * @return String
	 */
	public abstract String getLimitString(String sql, int offset, int limit);

	/**
	 * 获取count
	 * 
	 * @author maojian
	 * @date 2017-08-10 08:49:08
	 * @since v1.0.0
	 * @param sql
	 * @return
	 */
	public abstract String getCountSql(String sql);
}
