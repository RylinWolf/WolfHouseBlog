USE wolfBlog;

CREATE TABLE IF NOT EXISTS admins
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    auth_id BIGINT NOT NULL COMMENT '权限 ID'
) COMMENT '管理员表';
ALTER TABLE admins
    AUTO_INCREMENT 10000;



CREATE TABLE IF NOT EXISTS authority
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限 ID',
    admin_id  BIGINT       NOT NULL COMMENT '管理员 ID',
    authority VARCHAR(255) NOT NULL COMMENT '管理员权限\n user - 用户\n blog - 文章\n all - 全部'
) COMMENT '权限表';
ALTER TABLE authority
    AUTO_INCREMENT 0;


CREATE TABLE IF NOT EXISTS `users`
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
    username        VARCHAR(255) NOT NULL COMMENT '用户名',
    `account`       VARCHAR(255) NOT NULL COMMENT '账号',
    avatar          TEXT COMMENT '头像 URL',
    personal_status VARCHAR(255) COMMENT '个性签名',
    phone           VARCHAR(15) COMMENT '手机号',
    email           VARCHAR(255) NOT NULL COMMENT '邮箱',
    birth           DATE COMMENT '生日',
    register_date   DATE         NOT NULL DEFAULT (CURRENT_DATE) COMMENT '注册时间'
) COMMENT '用户表';
ALTER TABLE `users`
    AUTO_INCREMENT 10000;


CREATE TABLE IF NOT EXISTS subscribes
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关注 ID',
    from_user BIGINT NOT NULL COMMENT '关注用户 ID',
    to_user   BIGINT NOT NULL COMMENT '被关注用户 ID'
) COMMENT '关注表';
ALTER TABLE subscribes
    AUTO_INCREMENT 10000000;


CREATE TABLE IF NOT EXISTS articles
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文章 ID',
    `primary`    VARCHAR(255) COMMENT '摘要',
    author_id    BIGINT   NOT NULL COMMENT '作者用户 ID',
    content      TEXT     NOT NULL COMMENT '内容',
    post_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布日期',
    edit_time    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编辑日期',
    visibility   TINYINT  NOT NULL DEFAULT 0 COMMENT '可见权限\n0 - 公开\n1 - 私人',
    partition_id BIGINT COMMENT '分区 ID',
    tags         VARCHAR(255) COMMENT '文章标签，以 :\\ 分隔',
    com_use_tags VARCHAR(255) COMMENT '常用文章标签，以 :\\ 分隔'
) COMMENT '文章表';
ALTER TABLE articles
    AUTO_INCREMENT 100000000;



CREATE TABLE IF NOT EXISTS `partitions`
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分区 ID',
    `name`     VARCHAR(255) NOT NULL COMMENT '分区名',
    parent_id  BIGINT COMMENT '父分区 ID',
    visibility TINYINT      NOT NULL DEFAULT 0 COMMENT '可见权限\n0 - 公开\n1 - 私人'
) COMMENT '分区表';
ALTER TABLE `partitions`
    AUTO_INCREMENT 100000;



CREATE TABLE IF NOT EXISTS tags
(
    id     BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签 ID',
    `name` VARCHAR(255) NOT NULL COMMENT '标签名称'
) COMMENT '常用标签表';
ALTER TABLE tags
    AUTO_INCREMENT 10000000;



CREATE TABLE IF NOT EXISTS user_tags
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户-常用标签 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    tag_id  BIGINT NOT NULL COMMENT '标签 ID'
) COMMENT '用户-常用标签表';
ALTER TABLE user_tags
    AUTO_INCREMENT 10000000;




