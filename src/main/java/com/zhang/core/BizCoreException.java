package com.zhang.core;

/**
 *
 * @author yanlv
 * @version 0.1 : BizCoreException v0.1 2016/11/25 下午9:48 yanlv Exp $
 */

public class BizCoreException extends RuntimeException {

    /**  */
    private static final long serialVersionUID = -5586792174208604173L;

    private ErrorCode         code             = ErrorCode.SYSTEM_EXCEPTION;

    /**
     * 构造方法
     */
    public BizCoreException(String message) {
        super(message);
    }

    /**
     * 构造方法
     */
    public BizCoreException(String message, ErrorCode code) {
        super(message);
        this.code = code;
    }


    /**
     * 构造方法
     */
    public BizCoreException(ErrorCode code) {
        super(code.getDescription());
        this.code = code;
    }

    /**
     * 构造方法
     */
    public BizCoreException(String message, Throwable e) {
        super(message, e);
    }

    /**
     * 构造方法
     */
    public BizCoreException(String message, ErrorCode code, Throwable e) {
        super(message, e);
        this.code = code;
    }

    /**
     * 构造方法
     */
    public BizCoreException(Throwable e) {
        super(e);
    }

    /**
     * 构造方法
     */
    public BizCoreException(ErrorCode code, Throwable e) {
        super(e);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }

    public void setCode(ErrorCode code) {
        this.code = code;
    }
}
