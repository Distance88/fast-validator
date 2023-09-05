package com.zhang.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Yaohang Zhang
 * @ClassName BaseHandler
 * @description
 * @date 2023/9/4 11:50
 */
public interface BaseHandler {

    void handle(Annotation annotation, Field field, Object target) throws IllegalAccessException;
}
