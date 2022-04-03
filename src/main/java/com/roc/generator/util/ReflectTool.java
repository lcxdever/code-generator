package com.roc.generator.util;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;

/**
 * 反射工具类
 *
 * @author 鱼蛮 Date 2022/4/3
 */
public class ReflectTool {

    /**
     * 获取类中的所有非静态字段，包括父类
     *
     * @param clazz 类对象
     * @return {@link List<Field>}
     */
    public static List<Field> getClassFields(Class<?> clazz) {
        List<Field> fields = Lists.newArrayList();
        do {
            for (Field field : clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        } while (Objects.nonNull(clazz));
        return fields;
    }

}
