package com.wolfhouse.wolfhouseblog.mail;

import jakarta.mail.MessagingException;

/**
 * @author Rylin Wolf
 */
public interface MailService {
    void sendCode(String email) throws MessagingException;

    Boolean verifyCode(String email, String code);

    String genCode();
}
