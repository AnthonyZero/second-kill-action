package com.anthonyzero.seckill.common.core;

import com.alibaba.fastjson.JSON;
import com.anthonyzero.seckill.common.enums.CodeMsgEnum;
import lombok.*;

/**
 * 统一接口响应
 */
@Getter
public class Result<T> {
    private static final int DEFAULT_SUCCESS_CODE = 0;
    private static final String DEFAULT_SUCCESS_MESSAGE = "success";
    private int code;
    private String msg;
    private T data;

    private Result() {
        this.code = DEFAULT_SUCCESS_CODE;
        this.msg = DEFAULT_SUCCESS_MESSAGE;
    }

    private Result(T data) {
        this.code = DEFAULT_SUCCESS_CODE;
        this.msg = DEFAULT_SUCCESS_MESSAGE;
        this.data = data;
    }

    private Result(CodeMsgEnum codeMsgEnum) {
        if (codeMsgEnum == null){
            return;
        }
        this.code = codeMsgEnum.getCode();
        this.msg = codeMsgEnum.getMsg();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    /**
     * 默认成功的时候调用
     * @param <T>
     * @return
     */
    public static <T> Result<T> success() {
        return new Result<T>();
    }

    /**
     * 成功时候调用
     * @param data
     * @param <T>
     * @return
     */
    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    /**
     * 失败时候调用
     * @param codeMsgEnum
     * @param <T>
     * @return
     */
    public static <T> Result<T> error(CodeMsgEnum codeMsgEnum){
        return new Result<T>(codeMsgEnum);
    }
}
