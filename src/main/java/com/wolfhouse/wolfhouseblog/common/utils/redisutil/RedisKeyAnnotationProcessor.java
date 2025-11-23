package com.wolfhouse.wolfhouseblog.common.utils.redisutil;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 启动期处理 @RedisKey 注解：
 * - 类级别：当 isKeysConstant=true 时，扫描所有静态常量字段并注册到 RedisKeyUtil
 * - 同时根据注解设置 RedisKeyUtil 的 prefix / separator
 *
 * @author Rylin Wolf
 */
@Slf4j
@Component
public class RedisKeyAnnotationProcessor implements BeanPostProcessor {

    private final RedisKeyUtil redisKeyUtil;

    @Lazy
    @Autowired
    public RedisKeyAnnotationProcessor(RedisKeyUtil redisKeyUtil) {
        this.redisKeyUtil = redisKeyUtil;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        // 处理修饰类
        RedisKey redisKey = AnnotationUtils.findAnnotation(targetClass, RedisKey.class);
        if (redisKey != null) {
            // 修饰类时，必须指定常量类，否则不生效
            if (redisKey.isKeysConstant()) {
                // 设置前缀与分隔符（幂等：RedisKeyUtil 内部 keyMap 使用 computeIfAbsent，不会覆盖已有 key）
                redisKeyUtil.setPrefix(redisKey.prefix());
                redisKeyUtil.setSeparator(redisKey.separator());

                processKeyConstant(targetClass, redisKey);
            }
        }

        // 处理类中的字段
        processAnnotatedFields(bean, targetClass, redisKey);
        return bean;
    }

    private void processKeyConstant(Class<?> targetClass, RedisKey redisKey) {
        try {
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())
                    && Modifier.isFinal(field.getModifiers())
                    && field.getType() == String.class) {
                    // 如果字段有被 RedisKey 修饰，则为防止重复加载，跳过
                    if (field.getAnnotation(RedisKey.class) != null) {
                        continue;
                    }

                    // 如果字段标明非 RedisKey，跳过
                    if (field.getAnnotation(Except.class) != null) {
                        continue;
                    }

                    field.setAccessible(true);
                    // 常量值 为 键
                    String key = (String) field.get(null);
                    // 常量名称分隔后作为二级前缀拼接
                    String fieldName = field.getName();
                    String fieldNameSecPrefix = Arrays.stream(fieldName.split("_"))
                                                      .filter(s -> !s.isBlank())
                                                      .map(String::toLowerCase)
                                                      .collect(Collectors.joining(redisKey.separator()));
                    // 若 asName，则常量名称也作为 name
                    String name = redisKey.asName() ? fieldName : null;
                    redisKeyUtil.registerKeyWithSecPrefix(key, name, redisKey.secondPrefix(), fieldNameSecPrefix);
                }
            }
            log.info("[RedisKey] 类 {} 的 RedisKey 已注册: {}",
                     targetClass.getSimpleName(),
                     redisKeyUtil.getKeyMap());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法访问常量字段", e);
        }
    }

    /**
     * 处理类中标注了 @RedisKey 的字段。
     * 约定：
     * - 字段级注解生效范围：secondPrefix 与 asName；prefix/separator 采用类级注解（若无类级注解，则沿用 RedisKeyUtil 当前全局设置）。
     * - 对于字段来说，次级前缀与字段名称无关，由 [注解.secondPrefix] 与 [字段名按 '_' 拆分并转小写] 通过分隔符拼接得到。
     * - 注册用的 key 优先取字段值；若为空则退回字段名的小写形式，并给出 warn 日志。
     */
    private void processAnnotatedFields(Object bean, Class<?> targetClass, RedisKey classAnno) {
        Object targetBean = unwrapTarget(bean);
        Field[] fields = targetClass.getDeclaredFields();
        for (Field field : fields) {
            RedisKey fieldAnno = field.getAnnotation(RedisKey.class);
            // 非 RedisKey
            if (fieldAnno == null) {
                continue;
            }

            try {
                field.setAccessible(true);
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                // 根据是否静态字段获取对应值，作为 key
                Object rawVal = isStatic ? field.get(null) : field.get(targetBean);
                String key = rawVal instanceof String ? (String) rawVal : null;
                if (key == null || key.isBlank()) {
                    key = field.getName()
                               .toLowerCase();
                    log.warn("[RedisKey] 字段 {}.{} 值为空或为 null，使用字段名作为 key: {}",
                             targetClass.getSimpleName(),
                             field.getName(),
                             key);
                }

                // 使用类级注解的 prefix 与 separator（若类级注解不存在，则使用 RedisKeyUtil 当前配置）
                String secPrefix = fieldAnno.secondPrefix();
                if (classAnno != null) {
                    // 幂等：多次 set 对已注册键无影响
                    redisKeyUtil.setPrefix(classAnno.prefix());
                    redisKeyUtil.setSeparator(classAnno.separator());
                    secPrefix = secPrefix.isEmpty() ? classAnno.secondPrefix() : secPrefix;
                }
                // 分隔符
                String sep = fieldAnno.separator();

                // 拼接基准 prefix，获得完整的前缀
                String prefix = Stream.of(fieldAnno.prefix(), secPrefix)
                                      .filter(s -> !s.isEmpty())
                                      .collect(Collectors.joining(sep));


                // 根据 asName 属性分别构建 name
                String name = fieldAnno.name();
                if (fieldAnno.asName()) {
                    // 根据字段名与配置的 name 进行组合
                    ArrayList<String> names = new ArrayList<>(Arrays.stream(field.getName()
                                                                                 .split("_"))
                                                                    .toList());
                    if (!name.isEmpty()) {names.add(name);}
                    name = Arrays.stream(names.toArray(new String[0]))
                                 .filter(s -> !s.isBlank())
                                 .map(String::toLowerCase)
                                 .collect(Collectors.joining(sep));
                }

                redisKeyUtil.registerKeyWithPrefix(key, name, prefix);
                log.info("[RedisKey] 字段注册完成 {}.{} -> {}",
                         targetClass.getSimpleName(),
                         field.getName(),
                         redisKeyUtil.getKey(key));
            } catch (IllegalAccessException e) {
                log.error("[RedisKey] 处理字段失败 {}.{}", targetClass.getSimpleName(), field.getName(), e);
            }
        }
    }

    /**
     * 尝试对传入的 bean 进行解封装，返回其代理对象的目标对象。
     * 如果传入的 bean 是一个 AOP 代理对象，则尝试获取其目标对象。
     * 如果解封装失败，或者目标对象为空，将返回原始的 bean。
     *
     * @param bean 需要进行解封装的对象，可以是普通对象或 AOP 代理对象
     * @return 解封装后的目标对象，如果无法获取目标对象则返回原始的 bean
     */
    private Object unwrapTarget(Object bean) {
        try {
            if (AopUtils.isAopProxy(bean) && bean instanceof Advised advised) {
                Object target = advised.getTargetSource()
                                       .getTarget();
                if (target != null) {
                    return target;
                }
            }
        } catch (Exception e) {
            log.debug("[RedisKey] 解除代理获取目标对象失败，使用原始 bean", e);
        }
        return bean;
    }
}
