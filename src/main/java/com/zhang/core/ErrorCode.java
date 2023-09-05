package com.zhang.core;

/**
 *
 * @author yanlv
 * @version 0.1 : ErrorCode v0.1 2017/11/24 下午2:26 yanlv Exp $
 */

public enum ErrorCode {

    SUCCESS("000000", "success"),
    SYSTEM_EXCEPTION("999999", "system error"),

    BAD_REQUEST("400", "请求错误"),
	UNAUTHORIZED("401", "未授权"),
    FORBIDDEN("403", "没有权限"),
    METHOD_NOT_ALLOWED("405", "不支持当前请求方法"),
    UNSUPPORTED_MEDIA_TYPE("415", "不支持当前媒体类型"),
    
    SERVER_ERROR("500", "服务器异常"),

    ;

    private String code;

    private String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ErrorCode findByCode(String code) {

        for (ErrorCode type : ErrorCode.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }

        return null;
    }
}
