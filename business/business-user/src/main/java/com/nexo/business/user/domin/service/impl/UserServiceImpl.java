package com.nexo.business.user.domin.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nexo.business.user.domin.entity.User;
import com.nexo.business.user.domin.service.UserService;
import com.nexo.business.user.infrastructure.exception.UserErrorCode;
import com.nexo.business.user.infrastructure.exception.UserException;
import com.nexo.business.user.infrastructure.mapper.UserMapper;
import com.nexo.business.user.infrastructure.mapstruct.UserConverter;
import com.nexo.common.api.user.constant.UserState;
import com.nexo.common.api.user.response.data.UserInfo;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.stereotype.Service;

/**
 * @classname UserServiceImpl
 * @description 用户服务实现类
 * @date 2025/12/02 09:09
 * @created by YanShijie
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private static final String DEFAULT_NICK_NAME_PREFIX = "C_";

    private final UserConverter userConverter;

    @Override
    public void register(String phone, String inviteCode) {
        // 2. 生成默认昵称
        // TODO 后续加入布隆过滤器，优化用户名重复问题
        String random = RandomUtil.randomString(6);
        String defaultNickName = DEFAULT_NICK_NAME_PREFIX + random + phone.substring(7, 11);
        // 3. 查询邀请人
        Long invitorId = null;
        // 3.1 判断是否有邀请人
        if (inviteCode != null) {
            // 3.2 获取邀请人信息
            User invitor = this.getOne(new LambdaQueryWrapper<User>().eq(User::getInviteCode, inviteCode).eq(User::getDeleted, 0));
            if (invitor != null) {
                invitorId = invitor.getId();
            }
        }
        // 4. 保存用户
        User user = new User();
        user.setNickName(defaultNickName);
        user.setPhone(phone);
        user.setState(UserState.UNVERIFIED);
        // TODO 后续加入布隆过滤器，优化邀请码重复问题
        user.setInviteCode(RandomUtil.randomString(6));
        user.setInviterId(invitorId);
        this.save(user);
    }

    @Override
    public UserInfo queryUserByPhone(String phone) {
        // 1. 根据手机号查询用户信息
        User user = this.getOne(new LambdaQueryWrapper<User>().eq(User::getPhone, phone).eq(User::getDeleted, 0));
        // 2. 实体转换为DTO
        return userConverter.userToUserInfo(user);
    }

}
