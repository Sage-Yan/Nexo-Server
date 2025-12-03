package com.nexo.business.user.domin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nexo.business.user.domin.constant.UserState;
import com.nexo.common.datasource.domain.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @classname User
 * @description 用户实体类
 * @date 2025/12/02 09:08
 * @created by YanShijie
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("users")
public class User extends BaseEntity {

    @TableField("nick_name")
    private String nickName;

    @TableField("password")
    private String password;

    @TableField("state")
    private UserState state;

    @TableField("invite_code")
    private String inviteCode;

    @TableField("phone")
    private String phone;

    @TableField("email")
    private String email;

    @TableField("inviter_id")
    private Long inviterId;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("login_time")
    private LocalDateTime loginTime;

    @TableField("role")
    private String role;

}
