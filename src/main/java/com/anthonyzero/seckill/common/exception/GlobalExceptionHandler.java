package com.anthonyzero.seckill.common.exception;

import com.anthonyzero.seckill.common.core.Result;
import com.anthonyzero.seckill.common.enums.CodeMsgEnum;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局异常处理
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    /**
     * 处理所有异常
     * @param ex
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public Result<String> handlerException(HttpServletRequest request, Exception ex) {
        if(ex instanceof BindException) {
            //参数校验失败
            BindException e = (BindException) ex;
            List<ObjectError> allErrors = e.getAllErrors();
            ObjectError objectError = allErrors.get(0);
            String errorMsg = objectError.getDefaultMessage();
            return Result.error(CodeMsgEnum.BIND_ERROR.fillArgs(errorMsg));
        } else if(ex instanceof GlobalException) {
            //自定义全局异常 业务抛出
            GlobalException globalException = (GlobalException) ex;
            return Result.error(globalException.getCodeMsgEnum());
        } else {
            ex.printStackTrace();
            return Result.error(CodeMsgEnum.SERVER_ERROR);
        }
    }
}
