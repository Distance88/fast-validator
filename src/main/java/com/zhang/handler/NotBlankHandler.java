package com.zhang.handler;

import com.zhang.annotation.NotBlank;
import com.zhang.service.ValidatorServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Yaohang Zhang
 * @ClassName NotBlankHandler
 * @description
 * @date 2023/9/4 11:51
 */
@Slf4j
public class NotBlankHandler implements BaseHandler{

    @Override
    public void handle(Annotation annotation, Field field, Object target) throws IllegalAccessException {
        NotBlank notBlank = (NotBlank) annotation;
        field.setAccessible(true);
        Object o1 = field.get(target);
        ValidatorServiceImpl.getInstance().notBlank(o1, notBlank.message());
    }
}
