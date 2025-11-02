package com.wolfhouse.wolfhouseblog.mail;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * @author Rylin Wolf
 */
@Configuration
@ConfigurationProperties(prefix = "custom.mail")
@Data
public class MailConfig {
    private String host;
    private int port;
    private String password = System.getenv("MAIL_PASSWORD");
    private String username = System.getenv("MAIL_USERNAME");

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setPassword(password);
        sender.setUsername(username);
        sender.setDefaultEncoding("UTF-8");

        // Enable SMTP over SSL for port 465
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.port", String.valueOf(port));
        props.put("mail.smtp.ssl.trust", host);
        // props.put("mail.debug", "true"); // uncomment for debugging

        return sender;
    }
}
