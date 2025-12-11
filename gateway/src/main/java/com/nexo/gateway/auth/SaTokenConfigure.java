package com.nexo.gateway.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @classname SaTokenConfigure
 * @description SaToken配置类
 * @date 2025/12/08 19:55
 * @created by YanShijie
 */
@Configuration
@Slf4j
public class SaTokenConfigure {
    // 注册 Sa-Token全局过滤器
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")    /* 拦截全部path */
                // 开放地址
                .addExclude("/favicon.ico")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 登录校验 -- 拦截所有路由，并排除/user/doLogin 用于开放登录
                    SaRouter.match("/**", "/user/doLogin", r -> StpUtil.checkLogin());

                    // 权限认证 -- 不同模块, 校验不同权限
                    SaRouter.match("/**", r -> StpUtil.checkRoleOr(UserRole.GOD.getCode(), UserRole.ROOT.getCode()));

                    SaRouter.match("/admin/**", r -> StpUtil.checkRole(UserRole.ADMIN.getCode()));
                    // TODO 后续添加权限认证

                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(this::getSaResult);
    }

    private SaResult getSaResult(Throwable throwable) {
        return switch (throwable) {
            case NotLoginException _ -> {
                log.error("未登录");
                yield SaResult.error("未登录");
            }
            case NotRoleException e -> {
                if (UserRole.ADMIN.getCode().equals(e.getRole()) || UserRole.ROOT.getCode().equals(e.getRole()) || UserRole.GOD.getCode().equals(e.getRole())) {
                    log.error("请勿越权使用");
                    yield SaResult.error("请勿越权使用");
                }
                log.error("您无权限进行此操作");
                yield SaResult.error("您无权限进行此操作");
            }
            case NotPermissionException e -> {
                if (UserPermission.AUTHENTICATE.getCode().equals(e.getPermission())) {
                    log.error("请先进行实名认证");
                    yield SaResult.error("请先进行实名认证");
                }
                log.error("无权限");
                yield SaResult.error("无权限");
            }
            default -> SaResult.error(throwable.getMessage());
        };
    }
}