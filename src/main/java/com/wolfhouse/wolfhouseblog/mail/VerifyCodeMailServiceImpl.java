package com.wolfhouse.wolfhouseblog.mail;

import cn.hutool.core.util.RandomUtil;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 邮箱验证码服务
 *
 * @author Rylin Wolf
 */
@Service
@RequiredArgsConstructor
public class VerifyCodeMailServiceImpl implements MailService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final MailConfig config;
    private final JavaMailSender sender;
    private final freemarker.template.Configuration fmConfig;
    private final String codeKey = "service:verifyCode:%s";

    @Override
    public void sendCode(String email) throws MessagingException {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        // 生成验证码键
        String key = codeKey.formatted(email);
        String code;
        // 检查是否已生成
        if (redisTemplate.hasKey(key)) {
            code = Optional.ofNullable((String) ops.get(key))
                           .orElse(genCode());
        } else {
            // 生成随机验证码（6）
            code = genCode();
        }
        // 保存至 Redis, 过期时间 30 分钟
        int expireMinutes = 30;
        ops.set(key, code, Duration.ofMinutes(expireMinutes));

        // 渲染 HTML 模板
        String html = buildVerifyCodeHtml(email, code, expireMinutes);

        // 构建并发送邮件（HTML）
        MimeMessage msg = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg);
        helper.setFrom(config.getUsername());
        helper.setTo(email);
        helper.setSubject("Wolf Blog 博客注册验证码");
        helper.setText(html, true);
        sender.send(msg);
    }

    private String buildVerifyCodeHtml(String email, String code, int expireMinutes) {
        Map<String, Object> model = new HashMap<>(5);
        model.put("email", email);
        model.put("code", code);
        model.put("expireMinutes", expireMinutes);
        model.put("siteName", "WolfHouse Blog");
        model.put("sendTime",
                  LocalDateTime.now()
                               .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        try {
            Template template = fmConfig.getTemplate("verify-code.ftl");
            try (StringWriter out = new StringWriter()) {
                template.process(model, out);
                return out.toString();
            }
        } catch (IOException | TemplateException e) {
            // 回退到纯文本
            return "验证码：" + code + "，有效期" + expireMinutes + "分钟";
        }
    }

    @Override
    public Boolean verifyCode(String email, String code) {
        var ops = redisTemplate.opsForValue();
        // 构建 key
        String key = codeKey.formatted(email);
        // 从缓存中获取验证码
        if (!redisTemplate.hasKey(key)) {
            // 过期或未发送验证码
            return false;
        }
        // 对比验证
        String cachedCode = (String) ops.get(key);
        if (!StringUtils.equals(cachedCode, code)) {
            // 验证码不正确
            return false;
        }
        // 验证成功则清理缓存
        redisTemplate.delete(key);
        // 返回验证结果
        return true;
    }

    @Override
    public String genCode() {
        return String.valueOf(RandomUtil.randomInt(100000, 999999));
    }
}
