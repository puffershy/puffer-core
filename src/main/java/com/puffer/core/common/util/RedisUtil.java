package com.puffer.core.common.util;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * redis工具类
 * 
 * @author buyi
 * @param <T>
 * @date 2018年6月12日下午10:36:44
 * @since 1.0.0
 */
public class RedisUtil {

	// @Resource
	// private StringRedisTemplate stringRedisTemplate;

	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	// 在构造器中通过redisTemplate的工厂方法实例化操作对象
	// private HashOperations<String, HK, T> hashOperations;
	//
	// private ListOperations<String, T> listOperations;
	//
	// private ZSetOperations<String, T> zSetOperations;
	//
	// private SetOperations<String, T> setOperations;

	private ValueOperations<String, Object> valueOperations;

	@PostConstruct
	public void init() {
		// valueOperations = redisTemplate.opsForValue();
		valueOperations = redisTemplate.opsForValue();
	}

	/**
	 * 添加元素
	 * 
	 * @author buyi
	 * @date 2018年6月12日下午10:54:57
	 * @since 1.0.0
	 * @param key
	 * @param value
	 */
	public void set(String key, Object value) {
		valueOperations.set(key, value);
	}

	/**
	 * 添加元素，并设置过期时间<br>
	 * 默认过期时间单位：秒
	 * 
	 * @author buyi
	 * @date 2018年6月12日下午11:00:34
	 * @since 1.0.0
	 * @param key
	 * @param value
	 * @param timeout
	 *            过期时间
	 */
	public void set(String key, Object value, long timeout) {
		set(key, value, timeout, TimeUnit.SECONDS);
	}

	/**
	 * 添加元素，并设置过期时间<br>
	 *
	 * @author buyi
	 * @date 2018年6月12日下午11:01:22
	 * @since 1.0.0
	 * @param key
	 * @param value
	 * @param timeout
	 *            过期时间
	 * @param timeUnit
	 *            过期时间单位
	 */
	public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
		valueOperations.set(key, value, timeout, timeUnit);
	}

	/**
	 * 分布式添加元素<br>
	 * 如果key存在，则返回false;如果key不存在，则添加元素到redis,并返回true
	 * 
	 * @author buyi
	 * @date 2018年6月12日下午10:56:31
	 * @since 1.0.0
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setAtom(String key, Object value) {
		return valueOperations.setIfAbsent(key, value);
	}

	/**
	 * 获取指定key的值
	 *
	 * @author buyi
	 * @date 2018年6月12日下午10:52:36
	 * @since 1.0.0
	 * @param key
	 * @return
	 */
	// public Object get(String key) {
	// return valueOperations.get(key);
	// }

	/**
	 * 获取指定key的值，并转换成指定类型
	 * 
	 * @author buyi
	 * @since 1.0.0
	 * @date 2018上午11:06:40
	 * @param key
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) valueOperations.get(key);
	}

	/**
	 * 获取指定key的值，并转换成字符类型
	 * 
	 * @author buyi
	 * @since 1.0.0
	 * @date 2018上午11:04:34
	 * @param key
	 * @return
	 */
	// public String getStr(String key) {
	// return String.valueOf(get(key));
	// }

	/**
	 * 清楚指定的key
	 *
	 * @author buyi
	 * @date 2018年6月12日下午11:03:21
	 * @since 1.0.0
	 * @param key
	 */
	public void remove(String key) {
		redisTemplate.delete(key);
	}

	/**
	 * 设置过期时间
	 *
	 * @author buyi
	 * @date 2018年6月12日下午11:04:35
	 * @since 1.0.0
	 * @param key
	 * @param timeout
	 * @param timeUnit
	 * @return
	 */
	public boolean expire(String key, long timeout, TimeUnit timeUnit) {
		return redisTemplate.expire(key, timeout, timeUnit);
	}

}
