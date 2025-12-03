package com.nexo.auth.domain.exception;

import com.nexo.common.base.exception.code.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AuthErrorCode implements ErrorCode {

    VERIFY_CODE_ERROR("100001", "验证码错误"),

    USER_NOT_EXIST("1000012", "用户不存在"),

    USER_EXIST("100003", "用户已存在");

    private final String code;

    private final String message;
}
