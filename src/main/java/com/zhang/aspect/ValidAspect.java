package com.zhang.aspect;

import com.zhang.annotation.NotBlank;
import com.zhang.annotation.Validator;
import com.zhang.handler.BaseHandler;
import com.zhang.handler.NotBlankHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Yaohang Zhang
 * @ClassName ValidAspect
 * @description
 * @date 2023/9/4 11:48
 */
@Slf4j
@Aspect
@Component
public class ValidAspect {

    private final Map<Class<? extends Annotation>, BaseHandler> handlerMap = new HashMap<>();

    {
        handlerMap.put(NotBlank.class, new NotBlankHandler());
    }

    @Pointcut(value = "@within(org.springframework.web.bind.annotation.RestController)" +
            "|| @within(org.springframework.stereotype.Controller)")
    public void pointcut() {
    }

    /**
     * @param joinPoint
     * @return
     */
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        log.info("args=[{}], signature={}, method={}, parameters=[{}]", args, signature, method, parameters);

        for (int i = 0; i < parameters.length; i++) {
            boolean present = parameters[i].isAnnotationPresent(Validator.class);
            if (!present) {
                continue;
            }
            Object targetObj = args[i];
            Field[] fields = targetObj.getClass().getDeclaredFields();
            //遍历所有的属性
            for (Field field : fields) {
                Annotation[] annotations = field.getDeclaredAnnotations();
                if (annotations.length == 0) {
                    continue;
                }
                //遍历属性的所有注解去处理
                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> type = annotation.annotationType();
                    BaseHandler baseHandler = handlerMap.get(type);
                    if (!Objects.isNull(baseHandler)) {
                        baseHandler.handle(annotation, field, targetObj);
                    }

                }
            }
        }

        return joinPoint.proceed();
    }

}
