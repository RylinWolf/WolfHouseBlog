package com.wolfhouse.wolfhouseblog.common.utils;

import com.wolfhouse.wolfhouseblog.common.exceptions.BeanUtilException;
import io.netty.util.internal.StringUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author linexsong
 */
public class BeanUtil {

    /**
     * 对象属性复制
     *
     * @param source 源对象
     * @param target 目标对象的 Class
     * @param <T>    源对象的类型
     * @param <V>    目标对象的类型
     * @return 目标对象
     */
    public static <T, V> V copyProperties(T source, Class<V> target, boolean ignoreBlank) {
        // 如果源对象为空,则返回 null
        if (source == null) {
            return null;
        }

        V v;
        try {
            // 通过反射获取目标对象的无参数构造函数并实例化
            v = target.getDeclaredConstructor()
                      .newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            // 如果反射实例化失败,则抛出 BeanUtilException 异常
            throw BeanUtilException.propertiesCopyFailed(e);
        }

        // 需要忽略空字段
        if (ignoreBlank) {
            // 获取 source 对象的字段
            Field[] fields = source.getClass()
                                   .getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    if (BeanUtil.isBlank(field.get(source))) {
                        // 对于空字段进行处理
                        field.set(source, null);
                    }
                } catch (IllegalAccessException e) {
                    // throw new RuntimeException("Error accessing field: " + field.getName(), e)
                }
            }
        }
        // 将源对象的所有字段复制到目标对象
        cn.hutool.core.bean.BeanUtil.copyProperties(source, v);

        // 返回目标对象
        return v;
    }

    public static <T, V> V copyProperties(T source, Class<V> target) {
        return BeanUtil.copyProperties(source, target, false);
    }

    public static <T, V> List<V> copyList(List<T> source, Class<V> target) {
        return source.stream()
                     .map(t -> BeanUtil.copyProperties(t, target))
                     .toList();
    }


    /**
     * 为空判断
     *
     * @param obj 对象
     * @param <T> 对象类型
     * @return 为空 true,否则 false
     */
    public static <T> Boolean isBlank(T obj) {
        if (obj instanceof Collection<?>) {
            // 如果是集合,则使用 Apache Commons CollectionUtils isEmpty() 判断
            // isEmpty() 方法判断集合是否为空
            return CollectionUtils.isEmpty((Collection<?>) obj);
        }
        if (obj instanceof Map<?, ?>) {
            // 如果是 Map,则使用 Apache Commons CollectionUtils isEmpty() 判断
            // isEmpty() 方法判断集合是否为空
            return CollectionUtils.isEmpty((Map<?, ?>) obj);
        }
        if (obj instanceof String) {
            // 如果是字符串,则使用 Netty StringUtil isNullOrEmpty() 判断
            // isNullOrEmpty() 方法判断字符串是否为空
            return StringUtil.isNullOrEmpty((String) obj) || ((String) obj).isBlank();
        }
        if (obj instanceof Boolean) {
            // 如果是 bool，则返回其本身取反
            return !(Boolean) obj;
        }

        // 对于其他对象,则使用 Java8 的 Objects.isNull() 方法判断
        // Objects.isNull() 方法判断对象是否为空
        return Objects.isNull(obj);
    }

    public static Boolean isAnyBlank(Object... objs) {
        for (Object obj : objs) {
            if (BeanUtil.isBlank(obj)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean isAnyNotBlank(Object... objs) {
        for (Object obj : objs) {
            if (!BeanUtil.isBlank(obj)) {
                return true;
            }
        }
        return false;
    }

    public static Boolean[] checkBlank(Object... objs) {
        Boolean[] blanks = new Boolean[objs.length];
        for (int i = 0; i < objs.length; i++) {
            if (isBlank(objs[i])) {
                blanks[i] = true;
                continue;
            }
            blanks[i] = false;
        }
        return blanks;
    }

    /**
     * 对象属性是否有空
     * 注意，要求需要查空的属性具备 getter 方法，否则无法判断
     *
     * @param obj 要判断的对象
     * @return 为空的字段名列表
     */
    public static List<String> blankFieldsFrom(Object obj) {
        Class<?> clazz = obj.getClass();
        List<String> blankFields = new ArrayList<>();
        // 获取字段名，调用 Getter 方法
        for (Field field : clazz.getDeclaredFields()) {
            String name = field.getName();
            try {
                Object fieldObj = clazz.getDeclaredMethod("get" + StringUtils.capitalize(name))
                                       .invoke(obj);
                // 字段为空
                if (isBlank(fieldObj)) {
                    blankFields.add(name);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException ignored) {
            }
        }
        return blankFields;
    }

}
