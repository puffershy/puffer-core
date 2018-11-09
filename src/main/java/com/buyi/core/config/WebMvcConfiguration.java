package com.buyi.core.config;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import com.buyi.core.exception.GlobalHandlerExceptionResolver;
import com.google.common.base.Charsets;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * spring mvc配置
 *
 * @author buyi
 * @date 2017下午3:40:05
 * @since 1.0.0
 */
@Configuration
@ConditionalOnWebApplication
public class WebMvcConfiguration extends WebMvcConfigurerAdapter implements WebMvcConfigurer {

    /**
     * fastjson规则配置
     *
     * @return
     * @author buyi
     * @date 2017下午3:53:39
     * @since 1.0.0
     */
    private FastJsonConfig fastJsonConfig() {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 在serializerFeatureList中添加转换规则
        List<SerializerFeature> serializerFeatureList = new ArrayList<SerializerFeature>();
        serializerFeatureList.add(SerializerFeature.PrettyFormat);
        serializerFeatureList.add(SerializerFeature.WriteMapNullValue);
        serializerFeatureList.add(SerializerFeature.WriteNullStringAsEmpty);
        serializerFeatureList.add(SerializerFeature.WriteNullListAsEmpty);
        serializerFeatureList.add(SerializerFeature.DisableCircularReferenceDetect);
        SerializerFeature[] serializerFeatures = serializerFeatureList.toArray(new SerializerFeature[serializerFeatureList.size()]);
        fastJsonConfig.setSerializerFeatures(serializerFeatures);

        fastJsonConfig.setCharset(Charsets.UTF_8);
        fastJsonConfig.setFeatures(Feature.IgnoreNotMatch);
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");

        return fastJsonConfig;
    }

    /**
     * fastJson信息转换器
     *
     * @return
     * @author buyi
     * @date 2017下午3:44:59
     * @since 1.0.0
     */
    @Bean
    @Order(1)
    public FastJsonHttpMessageConverter4 fastJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter4 converter = new FastJsonHttpMessageConverter4();

        List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        supportedMediaTypes.add(MediaType.parseMediaType("text/html;charset=UTF-8"));
        // supportedMediaTypes.add(MediaType.parseMediaType("application/json;charset=UTF-8"));
        converter.setSupportedMediaTypes(supportedMediaTypes);

        converter.setFastJsonConfig(fastJsonConfig());

        return converter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverter());
        //		super.configureMessageConverters(converters);
    }

    /**
     * 声明统一异常处理
     *
     * @return
     * @author buyi
     * @date 2018年6月18日下午8:59:47
     * @since 1.0.0
     */
    @Bean
    public GlobalHandlerExceptionResolver globalHandlerExceptionResolver() {
        return new GlobalHandlerExceptionResolver();
    }

    /**
     * 注入统一异常处理器<br>
     * 如果不注入，则会走默认的处理器，源码见{@linkplain  WebMvcConfigurationSupport#handlerExceptionResolver()}
     */
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(globalHandlerExceptionResolver());
    }

    // /**
    // * 添加拦截器
    // */
    // @Override
    // public void addInterceptors(InterceptorRegistry registry) {
    // super.addInterceptors(registry);
    // }
    //
}
