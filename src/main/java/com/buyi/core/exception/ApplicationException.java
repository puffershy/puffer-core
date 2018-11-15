package com.buyi.core.exception;

/**
 * 应用异常
 *
 * @author buyi
 * @date 2018年11月06日 09:57:23
 * @since 1.0.0
 */
public class ApplicationException extends RuntimeException {
    private String code;

    private ApplicationException(String code, String message) {
        super(message);
        this.code = code;
    }

    private ApplicationException(String code, String message, Throwable throwable) {
        super(message, throwable);
        this.code = code;
    }

    public static ApplicationException newInstance(String code, String message) {
        return new ApplicationException(code, message);
    }

    public static ApplicationException newInstance(String code, String message, Throwable throwable) {
        return new ApplicationException(code, message, throwable);
    }

    public String getCode() {
        return code;
    }
}
