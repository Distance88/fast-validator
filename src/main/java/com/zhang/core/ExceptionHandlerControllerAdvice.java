package com.zhang.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author Yaohang Zhang
 * @ClassName ExceptionHandlerControllerAdvice
 * @description
 * @date 2023/9/4 17:14
 */
@Slf4j
@ControllerAdvice("com.zhang.controller")
public class ExceptionHandlerControllerAdvice extends ResponseEntityExceptionHandler {


    // 自定义异常
    @ExceptionHandler(BizCoreException.class)
    public ResponseEntity<Object> handlerException(BizCoreException ex){
        CommonRestResult<Object> commonRestResult = new CommonRestResult<>();
        commonRestResult.setCode(ex.getCode().getCode());
        commonRestResult.setStatus(CommonRestResult.FAIL);
        commonRestResult.setMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(commonRestResult);
    }
}
