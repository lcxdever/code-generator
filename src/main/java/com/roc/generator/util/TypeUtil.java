package com.roc.generator.util;

import org.apache.commons.compress.utils.Sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author 鱼蛮 on 2022/2/21
 **/
public class TypeUtil {


    public static final Set<String> JAVA_BASE_TYPE = Sets.newHashSet(
            "byte",
            "short",
            "int",
            "long",
            "float",
            "double",
            "boolean",
            "char",
            "void",
            "java.lang.Byte",
            "java.lang.Short",
            "java.lang.Integer",
            "java.lang.Long",
            "java.lang.Float",
            "java.lang.Double",
            "java.lang.Boolean",
            "java.lang.Character",
            "java.lang.String",
            "java.lang.Object",
            "java.lang.Void",
            "java.util.Date",
            "java.time.LocalDate",
            "java.time.LocalDateTime",
            "java.math.BigDecimal",
            "java.util.Collection",
            "java.util.List",
            "java.util.Map",
            "java.util.Set"

    );

    public static final Map<String, String> PRIMITIVE_BOX_MAP;

    static {
        PRIMITIVE_BOX_MAP = new HashMap<>();
        PRIMITIVE_BOX_MAP.put("byte", "java.lang.Byte");
        PRIMITIVE_BOX_MAP.put("short", "java.lang.Short");
        PRIMITIVE_BOX_MAP.put("int", "java.lang.Integer");
        PRIMITIVE_BOX_MAP.put("long", "java.lang.Long");
        PRIMITIVE_BOX_MAP.put("float", "java.lang.Float");
        PRIMITIVE_BOX_MAP.put("double", "java.lang.Double");
        PRIMITIVE_BOX_MAP.put("boolean", "java.lang.Boolean");
        PRIMITIVE_BOX_MAP.put("char", "java.lang.Character");
    }

    /**
     * 转换基础类型到包装类型
     *
     * @param type type
     * @return {@link java.lang.String}
     */
    public static String convertTypePrimitiveToBox(String type) {
        return Optional.ofNullable(PRIMITIVE_BOX_MAP.get(type)).orElse(type);
    }

    /**
     * 是否是java基础类型
     *
     * @param type type
     * @return {@link boolean}
     */
    public static boolean isJavaBaseType(String type) {
        return JAVA_BASE_TYPE.contains(type);
    }
}
