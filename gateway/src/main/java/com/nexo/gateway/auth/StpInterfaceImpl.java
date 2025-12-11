package com.nexo.gateway.auth;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.nexo.common.api.user.response.data.UserInfo;

import java.util.List;

/**
 * @classname StpInterfaceImpl
 * @description Sa-Token 权限验证接口实现类
 * @date 2025/12/08 20:03
 * @created by YanShijie
 */
public class StpInterfaceImpl implements StpInterface {

    /**
     *
     * @param loginId  账号id
     * @param loginType 账号类型
     * @return 权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        UserInfo userInfo = (UserInfo) StpUtil.getSessionByLoginId(loginId).get(loginId.toString());

        return List.of();
    }

    /**
     *
     * @param loginId  账号id
     * @param loginType 账号类型
     * @return 角色列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return List.of();
    }
}
