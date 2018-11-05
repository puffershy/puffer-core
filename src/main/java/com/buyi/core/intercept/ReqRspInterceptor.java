package com.buyi.core.intercept;

import javax.servlet.http.HttpServletRequest;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;

/**
 * 请求响应拦截器
 * 
 * @author buyi
 * @since 1.0.0
 * @date 2017年7月4日上午11:17:44
 *
 */
@Component
public class ReqRspInterceptor implements MethodInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(ReqRspInterceptor.class);

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if (invocation.getMethod().getAnnotation(RequestMapping.class) == null) {
			return invocation.proceed();
		}

		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes sra = (ServletRequestAttributes) ra;
		HttpServletRequest request = sra.getRequest();
		Object[] arguments = invocation.getArguments();

		logger.info("【HTTP请求{}】{} : {} ,参数：{}", Thread.currentThread().getId(), request.getMethod(), request.getRequestURI(),JSONObject.toJSONString(arguments));
		Object result = invocation.proceed();// result的值就是被拦截方法的返回值
		logger.info("【HTTP响应{}】 {} ", Thread.currentThread().getId(), arguments);
		return result;
	}

}
