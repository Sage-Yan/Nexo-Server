package com.nexo.auth.domain.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.nexo.auth.interfaces.dto.request.LoginRequest;
import com.nexo.auth.interfaces.dto.request.RegisterRequest;
import com.nexo.auth.interfaces.dto.response.LoginResponse;
import com.nexo.auth.domain.exception.AuthErrorCode;
import com.nexo.auth.domain.exception.AuthException;
import com.nexo.auth.domain.service.AuthService;
import com.nexo.common.api.notification.NotificationFacade;
import com.nexo.common.api.notification.response.NotificationResponse;
import com.nexo.common.api.user.UserFacade;
import com.nexo.common.api.user.request.UserQueryRequest;
import com.nexo.common.api.user.request.UserRegisterRequest;
import com.nexo.common.api.user.request.condition.UserQueryByPhone;
import com.nexo.common.api.user.response.UserQueryResponse;
import com.nexo.common.api.user.response.UserResponse;
import com.nexo.common.api.user.response.data.UserInfo;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.nexo.auth.domain.constant.AuthConstant.SESSION_TIMEOUT;
import static com.nexo.common.api.notification.constant.NotificationConstant.CAPTCHA_KEY_PREFIX;

/**
 * @classname AuthServiceImpl
 * @description 认证相关服务实现类
 * @date 2025/12/01 18:02
 * @created by YanShijie
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @DubboReference(version = "1.0.0")
    private final NotificationFacade notificationFacade;

    private final StringRedisTemplate stringRedisTemplate;

    @DubboReference(version = "1.0.0")
    private final UserFacade userFacade;

    @Override
    public Boolean sendSmsVerifyCode(String phone) {
        // 1. 发送验证码
        NotificationResponse response = notificationFacade.sendSmsVerifyCode(phone);
        // 2. 返回结果
        return response.getSuccess();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 验证码校验
        isValidVerifyCode(request.getPhone(), request.getVerifyCode());
        // 2. 检查用户是否已注册
        UserInfo userInfo = checkUser(request.getPhone(), true);
        // 3. 用户登录
        StpUtil.login(userInfo.getId(),  new SaLoginParameter().setIsLastingCookie(request.getRememberMe()).setTimeout(SESSION_TIMEOUT));
        // 4. 保存用户信息到会话
        StpUtil.getSession().set(userInfo.getId().toString(), userInfo.toString());
        // 5. 封装返回数据
        return new LoginResponse(userInfo.getId(), StpUtil.getTokenValue(), StpUtil.getSessionTimeout());
    }

    @Override
    public Boolean register(RegisterRequest request) {
        // 1. 验证码校验
        isValidVerifyCode(request.getPhone(), request.getVerifyCode());
        // 2. 检查用户是否已注册
        checkUser(request.getPhone(), false);
        // 3. 注册用户
        // 3.1 构造注册请求
        UserRegisterRequest registerRequest = new UserRegisterRequest();
        registerRequest.setPhone(request.getPhone());
        registerRequest.setInviteCode(request.getInviteCode());
        // 3.2 注册用户
        UserResponse register = userFacade.register(registerRequest);
        // 4. 返回结果
        return register.getSuccess();
    }


    /**
     * 校验验证码是否有效
     * @param phone 手机号
     * @param verifyCode 验证码
     */
    private void isValidVerifyCode(String phone, String verifyCode) {
        // 1. 获取验证码
        String code = stringRedisTemplate.opsForValue().get(CAPTCHA_KEY_PREFIX + phone);
        // 2. 判断验证码是否一致
        if (!verifyCode.equals(code)) {
            throw new AuthException(AuthErrorCode.VERIFY_CODE_ERROR);
        }
    }

    /**
     * 检查用户是否存在
     * @param phone 手机号
     * @param isExist 要检查是否存在
     * @return 用户信息
     */
    private UserInfo checkUser(String phone, boolean isExist) {
        // 1. 构造查询请求
        UserQueryRequest queryRequest = new UserQueryRequest();
        queryRequest.setCondition(new UserQueryByPhone(phone));
        // 2. 根据手机号查询用户信息
        UserQueryResponse<UserInfo> queryResponse = userFacade.userQuery(queryRequest);
        // 3. 从查询响应拿到数据
        UserInfo userInfo = queryResponse.getData();
        // 4. 验证用户是否存在
        if (isExist) {
            if (userInfo == null) {
                throw new AuthException(AuthErrorCode.USER_NOT_EXIST);
            }
        } else {
            if (userInfo != null) {
                throw new AuthException(AuthErrorCode.USER_EXIST);
            }
        }
        return userInfo;
    }
}
