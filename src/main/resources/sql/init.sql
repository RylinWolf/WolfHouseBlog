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

    INSERT INTO wolfBlog.user_auth(id, password, is_enabled, is_deleted)
    VALUES (10000,
            '$2a$10$Gb/XxFHjzBIHwmdXev42/OEBRtRYq8lGqZ1cHx3N0M85JiL7d8TLq',
            1, 0);

    INSERT INTO wolfBlog.user(id, username, `account`, `email`)
    VALUES (10000, 'admin', 'admin', 'wolfblog_root@blog.com');

    INSERT INTO wolfBlog.admin(id, user_id, `name`)
    VALUES (10000, 10000, 'admin');
    COMMIT;

END //
DELIMITER ;

CALL init();
