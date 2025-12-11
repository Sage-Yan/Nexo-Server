create database nexo;

use nexo;

DROP TABLE IF EXISTS `notification`;

CREATE TABLE `notification`
(
    id           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    title        varchar(512) charset utf8mb3 null comment '通知标题',
    content      text charset utf8mb3         null comment '通知内容',
    notify_type  varchar(128) charset utf8mb3 null comment '通知类型',
    target       varchar(256) charset utf8mb3 null comment '接收地址',
    state        varchar(128) charset utf8mb3 null comment '状态',
    success_time datetime                     null comment '发送成功时间',
    fail_message text charset utf8mb3         null comment '失败信息',
    created_at   DATETIME(3)                  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    updated_at   DATETIME(3)                  NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    deleted      TINYINT(1)                   NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=正常，1=已删除',
    version      INT UNSIGNED                 NOT NULL DEFAULT 1 COMMENT '乐观锁版本号'
)
    COMMENT ='通知表';

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users`
(
    `id`          BIGINT UNSIGNED                      NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
    `nick_name`    VARCHAR(255)                        UNIQUE COMMENT '昵称/显示名',
    `password`    VARCHAR(255)                         NOT NULL COMMENT '密码哈希（bcrypt/argon2）',
    `state`      ENUM ('ACTIVE','FROZEN','SUSPENDED') NOT NULL DEFAULT 'ACTIVE' COMMENT '用户状态',
    `invite_code` VARCHAR(64)                                   DEFAULT NULL COMMENT '邀请码（可唯一）',
    `phone`       VARCHAR(32)                                   DEFAULT NULL COMMENT '手机号（国际码）',
    `email`       VARCHAR(255)                                  DEFAULT NULL COMMENT '邮箱',
    `inviter_id`  BIGINT UNSIGNED                               DEFAULT NULL COMMENT '邀请人 user_id',
    `avatar_url`  VARCHAR(1024)                                 DEFAULT NULL COMMENT '头像 URL',
    `login_time`  DATETIME                                      DEFAULT NULL COMMENT '最后登录时间',
    `role`        varchar(128)                                  DEFAULT NULL COMMENT '用户角色',
    `created_at`  DATETIME(3)                          NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updated_at`  DATETIME(3)                          NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `deleted`     TINYINT(1)                           NOT NULL DEFAULT 0 COMMENT '逻辑删除 0=正常 1=已删除',
    `version`     INT UNSIGNED                         NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_invite_code` (`invite_code`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT ='用户主表';

CREATE TABLE `user_kyc`
(
    `id`                BIGINT UNSIGNED                                        NOT NULL AUTO_INCREMENT,
    `user_id`           BIGINT UNSIGNED                                        NOT NULL COMMENT '关联 users.id',
    `kyc_status`        ENUM ('NOT_SUBMITTED','PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'NOT_SUBMITTED' COMMENT 'KYC 状态',
    `real_name`         VARCHAR(255)                                                    DEFAULT NULL COMMENT '真实姓名（敏感，建议加密）',
    `id_card_no`        VARCHAR(128)                                                    DEFAULT NULL COMMENT '证件号（敏感，建议加密且脱敏存储）',
    `id_card_type`      VARCHAR(32)                                                     DEFAULT 'ID_CARD' COMMENT '证件类型（ID_CARD/PASSPORT/DRIVER_LICENSE）',
    `id_card_front_url` VARCHAR(1024)                                                   DEFAULT NULL COMMENT '证件照-正面（建议存对象存储 URL）',
    `id_card_back_url`  VARCHAR(1024)                                                   DEFAULT NULL COMMENT '证件照-反面（建议存对象存储 URL）',
    `face_photo_url`    VARCHAR(1024)                                                   DEFAULT NULL COMMENT '用户拍照人像（或活体照）',
    `submit_time`       DATETIME(3)                                                     DEFAULT NULL COMMENT '提交时间',
    `review_time`       DATETIME(3)                                                     DEFAULT NULL COMMENT '审核时间',
    `reviewer`          VARCHAR(128)                                                    DEFAULT NULL COMMENT '审核人标识',
    `reject_reason`     VARCHAR(1024)                                                   DEFAULT NULL COMMENT '拒绝原因',
    `created_at`        DATETIME(3)                                            NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updated_at`        DATETIME(3)                                            NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `deleted`           TINYINT(1)                                             NOT NULL DEFAULT 0 COMMENT '逻辑删除 0=正常 1=已删除',
    `version`           INT UNSIGNED                                           NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_kyc_user` (`user_id`),
    CONSTRAINT `fk_kyc_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT ='用户 KYC 实名认证表（敏感字段请加密）';

CREATE TABLE `user_chain_accounts`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `user_id`      BIGINT UNSIGNED NOT NULL,
    `blockchain`   VARCHAR(64)     NOT NULL COMMENT '链名称或标识（ETH,SOL,BSC 等）',
    `address`      VARCHAR(255)    NOT NULL COMMENT '链上地址/钱包地址',
    `address_type` ENUM ('EOA','CONTRACT','MNEMONIC','OTHER') DEFAULT 'EOA' COMMENT '地址类型',
    `primary`      TINYINT(1)      NOT NULL                   DEFAULT 0 COMMENT '是否主地址',
    `verified`     TINYINT(1)      NOT NULL                   DEFAULT 0 COMMENT '是否已链上/签名校验',
    `meta`         JSON                                       DEFAULT NULL COMMENT '扩展信息（可存签名、公钥等）',
    `created_at`   DATETIME(3)     NOT NULL                   DEFAULT CURRENT_TIMESTAMP(3),
    `updated_at`   DATETIME(3)     NOT NULL                   DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `deleted`      TINYINT(1)      NOT NULL                   DEFAULT 0 COMMENT '逻辑删除 0=正常 1=已删除',
    `version`      INT UNSIGNED    NOT NULL                   DEFAULT 1 COMMENT '乐观锁版本号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_blockchain_address` (`blockchain`, `address`),
    CONSTRAINT `fk_chain_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
    COMMENT ='用户链上账户/钱包地址表';