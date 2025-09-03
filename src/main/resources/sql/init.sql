USE wolfBlog;

DELIMITER //

DROP PROCEDURE IF EXISTS init //

CREATE PROCEDURE init()
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
        BEGIN
            ROLLBACK;
        END;

    START TRANSACTION;

    INSERT INTO wolfBlog.authority(id, permission_code, permission_name)
    VALUES (1, 'ROLE_SUPER_ADMIN', '超级管理员身份'),
           (2, 'admin:create', '创建管理员'),
           (3, 'admin:edit', '修改管理员'),
           (4, 'admin:delete', '删除管理员'),
           (5, 'blog:user:disable', '禁用用户');

    INSERT INTO wolfBlog.user_auth(id, password, is_enabled, is_deleted)
    VALUES (10000,
            '$2a$10$ejleLVTJYSFdyQKxjb0UI.2PB7tDpH6ZJJ7K7zXxedjApxZRxuLsO',
            1, 0);

    INSERT INTO wolfBlog.user(id, username, `account`, `email`)
    VALUES (10000, 'admin', 'admin', 'wolfblog_root@blog.com');

    INSERT INTO wolfBlog.admin(id, user_id, `name`)
    VALUES (10000, 10000, 'admin');

    INSERT INTO wolfBlog.admin_authority(admin_id, authority_id)
    VALUES (10000, 1),
           (10000, 2),
           (10000, 3),
           (10000, 4),
           (10000, 5);
    COMMIT;


END //
DELIMITER ;

CALL init();
