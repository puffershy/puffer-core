package com.buyi.core.exception;

/**
 * 公用异常码
 *
 * @author buyi
 * @date 2018年11月06日 10:06:26
 * @since 1.0.0
 */
public enum CommonExceptionCode {
    SUCCESS("0", "成功"),
    SYS_ERROR("000001", "系统异常"),
    BAD_REQUEST("000002", "无效请求"),
    BAD_PARAMETER("000003", "参数异常"),
    UNAUTHORIZED("000004","权限不足");



    private String code;
    private String message;

    CommonExceptionCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public ApplicationException exception(){
        return ApplicationException.newInstance(getCode(),getMessage());
    }
}
