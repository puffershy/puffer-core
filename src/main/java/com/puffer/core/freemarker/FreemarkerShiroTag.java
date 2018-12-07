package com.puffer.core.freemarker;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * Freemarker标签
 * </p>
 * 用于标记自定义标签，方便扩展
 * 
 * @author buyi
 * @since 1.0.0
 * @date 2017下午5:22:38
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface FreemarkerShiroTag {

	String value();

}
