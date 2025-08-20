DROP DATABASE IF EXISTS wolfBlog;

CREATE DATABASE wolfBlog;

USE wolfBlog;


CREATE TABLE IF NOT EXISTS `admin`
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员 ID',
    user_id BIGINT       NOT NULL COMMENT '用户 ID',
    `name`  VARCHAR(255) NOT NULL COMMENT '管理员名称'
) AUTO_INCREMENT 10000 COMMENT '管理员表';



CREATE TABLE IF NOT EXISTS admin_authority
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限 ID',
    admin_id     BIGINT NOT NULL COMMENT '管理员 ID',
    authority_id INT    NOT NULL COMMENT '权限 ID'
) AUTO_INCREMENT 1 COMMENT '管理员权限表';


CREATE TABLE IF NOT EXISTS authority
(
    id              INT PRIMARY KEY AUTO_INCREMENT COMMENT '权限 ID',
    permission_code VARCHAR(255) NOT NULL COMMENT '权限代号',
    permission_name VARCHAR(255) NOT NULL COMMENT '权限名称'
) AUTO_INCREMENT 1 COMMENT '权限表';



CREATE TABLE IF NOT EXISTS `user`
(
    id              BIGINT PRIMARY KEY COMMENT '用户 ID',
    username        VARCHAR(255) NOT NULL COMMENT '用户名',
    `account`       VARCHAR(255) NOT NULL COMMENT '账号',
    avatar          TEXT COMMENT '头像 URL',
    personal_status VARCHAR(255) COMMENT '个性签名',
    phone           VARCHAR(255) COMMENT '手机号',
    email           VARCHAR(255) NOT NULL COMMENT '邮箱',
    birth           DATE COMMENT '生日',
    nickname        VARCHAR(255) COMMENT '昵称',
    register_date   DATE         NOT NULL DEFAULT (CURRENT_DATE) COMMENT '注册时间'
) COMMENT '用户表';


CREATE TABLE IF NOT EXISTS user_auth
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
    `password` VARCHAR(255) NOT NULL COMMENT '用户密码',
    is_enabled TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用\n0 - 否\n1 - 是',
    is_deleted TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除\n0 - 否\n 1 - 是'
) AUTO_INCREMENT 10000 COMMENT '用户认证表';


DROP TABLE IF EXISTS subscribe;

CREATE TABLE IF NOT EXISTS subscribe
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关注 ID',
    from_user BIGINT NOT NULL COMMENT '关注用户 ID',
    to_user   BIGINT NOT NULL COMMENT '被关注用户 ID',
    UNIQUE INDEX (from_user, to_user) COMMENT '联合索引'
) AUTO_INCREMENT 10000000 COMMENT '关注表';


CREATE TABLE IF NOT EXISTS article
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文章 ID',
    `title`      VARCHAR(255) NOT NULL COMMENT '标题',
    `primary`    VARCHAR(255) COMMENT '摘要',
    author_id    BIGINT       NOT NULL COMMENT '作者用户 ID',
    content      TEXT         NOT NULL COMMENT '内容',
    post_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布日期',
    edit_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '编辑日期',
    visibility   TINYINT      NOT NULL DEFAULT 0 COMMENT '可见权限\n0 - 公开\n1 - 私人',
    partition_id BIGINT COMMENT '分区 ID',
    tags         JSON COMMENT '文章标签，以 :\\ 分隔',
    com_use_tags JSON COMMENT '常用文章标签，以 :\\ 分隔'
) AUTO_INCREMENT 100000000 COMMENT '文章表';

DROP TABLE IF EXISTS `partition`;

CREATE TABLE IF NOT EXISTS `partition`
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分区 ID',
    `name`     VARCHAR(255) NOT NULL COMMENT '分区名',
    parent_id  BIGINT COMMENT '父分区 ID',
    user_id    BIGINT COMMENT '创建用户 ID',
    `order`    BIGINT                DEFAULT 0 NOT NULL,
    visibility TINYINT      NOT NULL DEFAULT 0 COMMENT '可见权限\n0 - 公开\n1 - 私人'
) AUTO_INCREMENT 100000 COMMENT '分区表';



CREATE TABLE IF NOT EXISTS tag
(
    id     BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签 ID',
    `name` VARCHAR(255) NOT NULL COMMENT '标签名称'
) AUTO_INCREMENT 10000000 COMMENT '常用标签表';

DROP TABLE IF EXISTS user_tag;

CREATE TABLE IF NOT EXISTS user_tag
(
    id      BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户-常用标签 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    tag_id  BIGINT NOT NULL COMMENT '标签 ID',
    UNIQUE INDEX (user_id, tag_id) COMMENT '用户 - 标签 索引'
) AUTO_INCREMENT 10000000 COMMENT '用户-常用标签表';




