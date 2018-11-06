package com.buyi.core.log;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 日志输出类<br>
 * 统一格式化日志输出，方便后期对日志已经脱敏等操作
 *
 * @author buyi
 * @date 2018年11月06日 16:22:11
 * @since 1.0.0
 */
@Data
public class Log {
    private String operation;
    private String message;
    private Map<String, String> params = new HashMap<>();

    private Log(String operation, String message) {
        this.operation = operation;
        this.message = message;
    }

    public static Log newInstance(String op, String message) {
        return new Log(op, message);
    }

    public Log kv(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        String toStr = String.format("%s|%s|%s", this.message, this.params.toString(), this.operation);
        return toStr;
    }
}
