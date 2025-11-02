package com.wolfhouse.wolfhouseblog.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author Rylin Wolf
 */
@Configuration
@ConfigurationProperties(prefix = "custom.mail")
@Data
public class MailConfig {
    private String host;
    private String password = System.getenv("MAIL_PASSWORD");
    private String username = System.getenv("MAIL_USERNAME");

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPassword(password);
        sender.setUsername(username);
        return sender;
    }
}
