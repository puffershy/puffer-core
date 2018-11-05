package com.buyi.core.context.mybatis;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.ReflectionUtils;

/**
 * 分页插件 , 简单的sql可以使用，复杂需要优化性能的sql不建议使用,本质上是在原orginSQL上包装了select count(1) from (orginSQL) tt和 select * from (orginSQL) tt limit m,n
 * 
 * @author maojian
 * @date 2017-08-10 08:49:44
 * @since v1.0.0
 */
@Intercepts(@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }))
public class PageInterceptor implements Interceptor {
	// private Logger logger = LoggerFactory.getLogger(getClass());
	// 数据库方言
	private Dialect dialect;

	/**
	 * 拦截处理
	 * 
	 * @author maojian
	 * @date 2017-08-10 14:14:20
	 * @since v1.0.0
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement) args[0]; // statement
		Object parameter = args[1]; // dao中传递的参数
		BoundSql boundSql = ms.getBoundSql(parameter); // sqlMap中的原SQL语句
		final String originSQL = boundSql.getSql();
		MappedStatement newMappedStatement = null;

		// 获取分页参数，确认是否需要分页
		PageAdapter pageAdapter = getPageAdapter(args);
		if (pageAdapter == null) {
			return invocation.proceed();
		}

		// 查询记录数
		newMappedStatement = makeCountMappedStatement(ms, dialect.getCountSql(originSQL), boundSql);
		Object result = resetInvocationAndProceed(invocation, newMappedStatement, RowBounds.DEFAULT);
		if (result instanceof Collection) {
			Iterator it = ((Collection) result).iterator();
			int totalCount = (Integer) it.next();
			pageAdapter.setTotalRecord(totalCount);
			pageAdapter.buildReturnProps();
		}
		// 查询记录条数
		// 需要分页查询的
		if (pageAdapter.getTotalRecord() > pageAdapter.getOffset()) {
			String sql_list = dialect.getLimitString(originSQL, pageAdapter.getOffset(), pageAdapter.getSize());
			newMappedStatement = makeMappedStatement(ms, sql_list, boundSql);
			Collection listData = (Collection) resetInvocationAndProceed(invocation, newMappedStatement, RowBounds.DEFAULT);
			pageAdapter.addAll(listData);
		}

		return pageAdapter;
	}

	/**
	 * 重新执行sql查询操作
	 * 
	 * @author maojian
	 * @date 2017-11-02 11:37:19
	 * @since v1.0.0
	 * @param invocation
	 * @param ms
	 * @param rowBounds
	 * @return
	 * @throws Throwable
	 */
	private Object resetInvocationAndProceed(Invocation invocation, MappedStatement ms, RowBounds rowBounds) throws Throwable {
		resetInvocationArgs(invocation, ms, rowBounds);
		return invocation.proceed();
	}

	/**
	 * 重设invocation参数
	 * 
	 * @author maojian
	 * @date 2017-11-02 11:37:31
	 * @since v1.0.0
	 * @param invocation
	 * @param ms
	 * @param rowBounds
	 */
	private void resetInvocationArgs(Invocation invocation, MappedStatement ms, RowBounds rowBounds) {
		Object[] args = invocation.getArgs();
		args[0] = ms;
		args[2] = rowBounds;
	}

	/**
	 * 初始化数据库方言
	 * 
	 * @author maojian
	 * @date 2017-08-10 14:14:35
	 * @since v1.0.0
	 * @param dialectClassName
	 */
	private void initDialect(String dialectClassName) {
		try {
			this.dialect = (Dialect) Class.forName(dialectClassName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			throw new IllegalArgumentException("ERROR:配置项[dialectClassName=" + dialectClassName + "]错误", e);
		}
	}

	@SuppressWarnings("rawtypes")
	private PageAdapter getPageAdapter(Object[] args) {
		return getPageFromArgs(args[1]);
	}

	/**
	 * 初始化查询参数，并返回PageAdapter对象
	 * 
	 * @author maojian
	 * @date 2017-08-10 14:14:49
	 * @since v1.0.0
	 * @param params
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private PageAdapter getPageFromArgs(Object params) {
		if (params == null) { // 不传参数，直接返回
			return null;
		}

		// 是否是分页查询 ：通过判断是否有page and size参数
		int isPageQry = 0;

		PageAdapter pageAdapter = new PageAdapter();
		MetaObject metaObject = SystemMetaObject.forObject(params);
		for (String name : metaObject.getGetterNames()) {
			pageAdapter.getQryParams().put(name, metaObject.getValue(name));
			if (PageAdapter.PAGE.equals(name) || PageAdapter.SIZE.equals(name)) {
				isPageQry++;
			}
		}

		if (isPageQry != 2) {// 非分页查询
			return null;
		}

		int index = metaObject.getValue(PageAdapter.PAGE) == null ? 1 : (int) metaObject.getValue(PageAdapter.PAGE); // 页码（Note:页码从1开始）
		int size = metaObject.getValue(PageAdapter.SIZE) == null ? 10 : (int) metaObject.getValue(PageAdapter.SIZE); // 每页条数

		return pageAdapter.prepareQryParams(index, size);
	}

	/**
	 * 插件包装
	 * 
	 * @author maojian
	 * @date 2017-08-10 14:15:33
	 * @since v1.0.0
	 * @param target
	 * @return
	 */
	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	/**
	 * 创建SQL语句对象
	 * 
	 * @author maojian
	 * @date 2017-08-10 14:15:46
	 * @since v1.0.0
	 * @param configuration
	 * @param sql
	 * @param boundSql
	 * @return
	 */
	private BoundSql createBoundSql(Configuration configuration, String sql, BoundSql boundSql) {
		return new BoundSql(configuration, sql, boundSql.getParameterMappings(), boundSql.getParameterObject());
	}

	/**
	 * 创建SQL语句映射对象
	 * 
	 * @author maojian
	 * @date 2017-11-02 11:39:11
	 * @since v1.0.0
	 * @param ms
	 * @param sql
	 * @param boundSql
	 * @return
	 */
	private MappedStatement makeMappedStatement(MappedStatement ms, String sql, BoundSql boundSql) {
		return createMappedStatement(ms, sql, boundSql, false);
	}

	/**
	 * 创建Count语句映射对象
	 * 
	 * @author maojian
	 * @date 2017-11-02 11:38:47
	 * @since v1.0.0
	 * @param ms
	 * @param sql
	 * @param boundSql
	 * @return
	 */
	private MappedStatement makeCountMappedStatement(MappedStatement ms, String sql, BoundSql boundSql) {
		return createMappedStatement(ms, sql, boundSql, true);
	}

	/**
	 * 创建SQL语句映射对象
	 * 
	 * @author maojian
	 * @date 2017-08-10 14:17:11
	 * @since v1.0.0
	 * @param ms
	 * @param sql
	 * @param boundSql
	 * @param isCountResultMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private MappedStatement createMappedStatement(MappedStatement ms, String sql, BoundSql boundSql, boolean isCountResultMap) {
		BoundSql newBoundSql = createBoundSql(ms.getConfiguration(), sql, boundSql);
		try {
			// additionalParameters,metaParameters里面记录了临时变量和实际传参的对应关系
			Field additionalParametersField = BoundSql.class.getDeclaredField("additionalParameters");
			additionalParametersField.setAccessible(true);
			Map<String, Object> additionalParameters = (Map<String, Object>) ReflectionUtils.getField(additionalParametersField, boundSql);
			ReflectionUtils.setField(additionalParametersField, newBoundSql, additionalParameters);

			Field metaParametersField = BoundSql.class.getDeclaredField("metaParameters");
			metaParametersField.setAccessible(true);
			MetaObject metaParameters = (MetaObject) ReflectionUtils.getField(metaParametersField, boundSql);
			ReflectionUtils.setField(metaParametersField, newBoundSql, metaParameters);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return createMappedStatement(ms, new SqlSource() {

			private BoundSql boundSql;

			@Override
			public BoundSql getBoundSql(Object parameterObject) {
				return boundSql;
			}

			public SqlSource setBoundSql(BoundSql boundSql) {
				this.boundSql = boundSql;
				return this;
			}
		}.setBoundSql(newBoundSql), isCountResultMap);
	}

	/**
	 * 创建SQL语句映射对象
	 * 
	 * @author maojian
	 * @date 2017-08-10 14:17:28
	 * @since v1.0.0
	 * @param ms 原MappedStatement
	 * @param newSqlSource 新SqlSource
	 * @param isCountResultMap 是否是Count结果映射
	 * @return
	 */
	private MappedStatement createMappedStatement(MappedStatement ms, SqlSource newSqlSource, boolean isCountResultMap) {
		String msId = ms.getId();
		if (isCountResultMap) {
			msId = ms.getId() + "_COUNT_";
		}
		Builder builder = new Builder(ms.getConfiguration(), msId, newSqlSource, ms.getSqlCommandType());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		builder.timeout(ms.getTimeout());
		builder.parameterMap(ms.getParameterMap());
		builder.resultSetType(ms.getResultSetType());
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());
		// 结果映射
		if (!isCountResultMap) {
			builder.resultMaps(ms.getResultMaps());
		} else {
			ResultMap resultMap = createCountResultMap(ms);
			List<ResultMap> resultMaps = new ArrayList<ResultMap>();
			resultMaps.add(resultMap);
			builder.resultMaps(resultMaps);
		}
		// 属性
		if (ms.getKeyProperties() != null && ms.getKeyProperties().length >= 1) {
			StringBuilder build = new StringBuilder();
			for (String key : ms.getKeyProperties()) {
				build.append(key);
				build.append(",");
			}
			if (build.length() > 0) {
				build.deleteCharAt(build.length() - 1);
			}
			builder.keyProperty(build.toString());
		}
		return builder.build();
	}

	/**
	 * 创建count映射结果
	 * 
	 * @author maojian
	 * @date 2017-08-10 14:18:07
	 * @since v1.0.0
	 * @param ms
	 * @return
	 */
	public ResultMap createCountResultMap(MappedStatement ms) {
		List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();
		ResultMap resultMap = new ResultMap.Builder(ms.getConfiguration(), ms.getId(), int.class, resultMappings).build();
		return resultMap;
	}

	@Override
	public void setProperties(Properties properties) {
		if (this.dialect == null) {
			String dialectClassName = properties.getProperty("dialectClassName"); // 取mybatis-conifig.xml中的配置
			initDialect(dialectClassName);
		}
	}

}
