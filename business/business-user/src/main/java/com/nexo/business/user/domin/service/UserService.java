package com.nexo.business.user.domin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nexo.business.user.domin.entity.User;
import com.nexo.common.api.user.response.data.UserInfo;

public interface UserService extends IService<User> {

    /**
     * 新用户注册
     * @param phone 手机号
     * @param inviteCode 邀请码（可选）
     */
    void register(String phone, String inviteCode);


    /**
     * 根据手机号查询用户信息
     * @param phone 手机号
     * @return 用户信息
     */
    UserInfo queryUserByPhone(String phone);
}
