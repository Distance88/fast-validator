package com.zhang.controller.form;

import com.zhang.annotation.NotBlank;
import lombok.Data;

/**
 * @author Yaohang Zhang
 * @ClassName ValidatorForm
 * @description
 * @date 2023/9/4 11:16
 */
@Data
public class ValidatorForm {

    @NotBlank(message = "用户名不能为空")
    private String userName;

    private Integer age;

    private String idCard;

    private String mobile;
}
