package com.puffer.core.context.mybatis;

/**
 * MySQL数据库方言
 * 
 * @author maojian
 * @date 2017-08-10 09:57:59
 * @since v1.0.0
 */
public class MySQLDialect extends Dialect {

	/**
	 * 获取LIMIT分页查询语句
	 * 
	 * @author maojian
	 * @date 2017-08-10 14:13:14
	 * @since v1.0.0
	 * @param sql
	 * @param offset
	 * @param limit
	 * @return
	 */
	@Override
	public String getLimitString(String sql, int offset, int limit) {
		StringBuffer buf = new StringBuffer(sql.length() + 20);
		buf.append("SELECT * from (" + sql + ") tt");
		if (limit > 0) {
			buf.append(" limit ");
			buf.append(offset);
			buf.append(" , ");
			buf.append(limit);
		} else {
			buf.append(" limit ");
			buf.append(offset);
		}
		return buf.toString();
	}

	/**
	 * 生成select count语句
	 * 
	 * @author maojian
	 * @date 2017-08-10 14:13:29
	 * @since v1.0.0
	 * @param sql
	 * @return
	 */
	@Override
	public String getCountSql(String sql) {
		StringBuffer countSQL = new StringBuffer("SELECT COUNT(1) from ");

		return countSQL.append("(" + sql + ") tt").toString();
	}

}
