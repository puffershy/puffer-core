package com.buyi.core.common.model;

import com.buyi.core.exception.CommonExceptionCode;
import lombok.Builder;
import lombok.Data;

/**
 * 请求响应参数
 *
 * @author buyi
 * @date 2018年11月06日 09:59:20
 * @since 1.0.0
 */
@Data
public class OpResponse<C> {
    private String code;
    private String message;
    private C content;


    public OpResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <C>  OpResponse<C> newInstance(String code, String message) {
        return new OpResponse(code, message);
    }

    public static<C>  OpResponse<C> success(){
        return new OpResponse(CommonExceptionCode.SUCCESS.getCode(),CommonExceptionCode.SUCCESS.getMessage());
    }
}
