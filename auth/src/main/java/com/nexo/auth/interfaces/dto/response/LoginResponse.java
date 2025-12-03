package com.nexo.auth.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @classname LoginResponse
 * @description 登录响应
 * @date 2025/12/01 20:48
 * @created by YanShijie
 */
@Data
@AllArgsConstructor
public class LoginResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录Token
     */
    private String token;

    /**
     * token 过期时间
     */
    private Long expireTime;

}
