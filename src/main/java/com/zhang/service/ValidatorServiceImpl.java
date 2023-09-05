package com.zhang.service;

import com.zhang.core.BizCoreException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Yaohang Zhang
 * @ClassName ValidatorImpl
 * @description
 * @date 2023/9/4 13:36
 */
@Slf4j
public class ValidatorServiceImpl implements ValidatorService {

    /**
     * 构建验证器
     *
     * @return
     */
    public static ValidatorServiceImpl getInstance() {
        return new ValidatorServiceImpl();
    }


    @Override
    public void notBlank(Object target, String message) {

        if (null == target) {
            throw new BizCoreException(message);
        }
        if (target instanceof String) {
            String tar = (String) target;
            if (StringUtils.isBlank(tar)) {
                throw new BizCoreException(message);
            }
        } else {
            throw new BizCoreException("不支持该类型");
        }
    }

}
