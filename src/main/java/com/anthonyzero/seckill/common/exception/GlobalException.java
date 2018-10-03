package com.anthonyzero.seckill.common.exception;

import com.anthonyzero.seckill.common.enums.CodeMsgEnum;

/**
 * 自定义全局异常
 */
public class GlobalException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    private CodeMsgEnum codeMsgEnum;

    public GlobalException(CodeMsgEnum codeMsgEnum) {
        super(codeMsgEnum.getMsg());
        this.codeMsgEnum = codeMsgEnum;
    }

    public CodeMsgEnum getCodeMsgEnum() {
        return codeMsgEnum;
    }
}
