package com.buyi.core.exception;

import com.alibaba.fastjson.JSON;
import com.buyi.core.common.model.OpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 全局异常处理
 *
 * @author buyi
 * @date 2017下午4:58:24
 * @since 1.0.0
 */
public class GlobalHandlerExceptionResolver extends AbstractHandlerExceptionResolver {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${buyi.framework.exception.log-swith:true}")
    private boolean logSwith = true;

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        printLog(ex);

        OpResponse<String> opResponse = OpResponse.newInstance(CommonExceptionCode.SYS_ERROR.getCode(), CommonExceptionCode.SYS_ERROR.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        try {

            if (ex instanceof ApplicationException) {
                // 如果是自定义业务异常
                opResponse = resolveApplicationException(response, ex);
            } else if (ex instanceof MethodArgumentNotValidException || ex instanceof MissingServletRequestParameterException || ex instanceof HttpMessageNotReadableException) {
                // 如果是参数异常
                opResponse = resolveParameterException(response, ex);
            }

            response.getWriter().write(JSON.toJSONString(opResponse));
        } catch (IOException e) {
            logger.error("统一异常处理异常", e);
        }

        return new ModelAndView();
    }

    /**
     * 参数异常解析
     *
     * @param response
     * @param ex
     * @return
     * @author buyi
     * @date 2018年6月18日下午9:21:08
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    private OpResponse<String> resolveParameterException(HttpServletResponse response, Exception ex) {
        if (ex instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException validException = (MethodArgumentNotValidException) ex;
            BindingResult bindingResult = validException.getBindingResult();
            String message = CommonExceptionCode.BAD_PARAMETER.getMessage();
            FieldError fieldError = bindingResult.getFieldError();
            if (fieldError != null) {
                message = fieldError.getDefaultMessage();
            }

            return OpResponse.newInstance(CommonExceptionCode.BAD_PARAMETER.getCode(), message);
        }

        return OpResponse.newInstance(CommonExceptionCode.BAD_PARAMETER.getCode(), CommonExceptionCode.BAD_PARAMETER.getMessage());
    }

    /**
     * 业务异常解析
     *
     * @param response
     * @param ex
     * @return
     * @author buyi
     * @date 2018年6月18日下午9:17:50
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    private OpResponse<String> resolveApplicationException(HttpServletResponse response, Exception ex) {

        ApplicationException applicationException = (ApplicationException) ex;
        return OpResponse.newInstance(applicationException.getCode(), applicationException.getMessage());
    }

    /**
     * 日志打印
     *
     * @param ex
     * @author buyi
     * @date 2018年6月18日下午9:44:14
     * @since 1.0.0
     */
    private void printLog(Exception ex) {
        if (logSwith && ex instanceof ApplicationException) {
            logger.error("", ex);
            return;
        }

        logger.error("", ex);
    }

}
