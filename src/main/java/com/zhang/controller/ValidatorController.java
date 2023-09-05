package com.zhang.controller;

import com.zhang.annotation.Validator;
import com.zhang.controller.form.ValidatorForm;
import com.zhang.controller.vo.ValidatorVO;
import com.zhang.core.CommonRestResult;
import com.zhang.core.RestBusinessTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yaohang Zhang
 * @ClassName ValidatorController
 * @description
 * @date 2023/9/4 11:13
 */
@RestController
@RequestMapping("/validator")
public class ValidatorController {


    @PostMapping("/demo")
    public CommonRestResult<ValidatorVO> demo(@RequestBody @Validator ValidatorForm form) {
        return RestBusinessTemplate.execute(()->{
            ValidatorVO vo = new ValidatorVO();
            vo.setUserName(form.getUserName());
            vo.setAge(form.getAge());
            vo.setIdCard(form.getIdCard());
            vo.setMobile(form.getMobile());
            return vo;
        });
    }



}
