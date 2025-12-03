package com.nexo.common.api.user.response.data;

import lombok.Data;

/**
 * @classname UserInfo
 * @description 用户登录返回信息
 * @date 2025/12/03 12:58
 * @created by YanShijie
 */
@Data
public class UserInfo {

    private Long id;

    private String nickName;

    private String avatarUrl;

}
