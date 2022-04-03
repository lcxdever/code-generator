package com.roc.generator.util;

import com.google.common.collect.Maps;
import com.roc.generator.model.TypeInfo;
import com.roc.generator.setting.PluginSettingState;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * java 类型工具
 *
 * @author 鱼蛮 on 2022/2/21
 **/
public class TypeUtil {


    public static final Map<String, Object> JAVA_BASE_TYPE;

    public static final Map<String, String> PRIMITIVE_BOX_MAP;

    public static final Map<String, Object> COLLECTION_TYPE;

    public static final String TYPE_COLLECTION = "java.util.Collection";

    static {
        JAVA_BASE_TYPE = Maps.newHashMap();
        JAVA_BASE_TYPE.put("byte", 'b');
        JAVA_BASE_TYPE.put("short", 2);
        JAVA_BASE_TYPE.put("int", 4);
        JAVA_BASE_TYPE.put("long", 8);
        JAVA_BASE_TYPE.put("float", 4f);
        JAVA_BASE_TYPE.put("double", 8d);
        JAVA_BASE_TYPE.put("boolean", true);
        JAVA_BASE_TYPE.put("char", 'c');
        JAVA_BASE_TYPE.put("void", null);
        JAVA_BASE_TYPE.put("java.lang.Byte", 'a');
        JAVA_BASE_TYPE.put("java.lang.Short", 2);
        JAVA_BASE_TYPE.put("java.lang.Integer", 4);
        JAVA_BASE_TYPE.put("java.lang.Long", 8L);
        JAVA_BASE_TYPE.put("java.lang.Float", 4F);
        JAVA_BASE_TYPE.put("java.lang.Double", 8D);
        JAVA_BASE_TYPE.put("java.lang.Boolean", Boolean.TRUE);
        JAVA_BASE_TYPE.put("java.lang.Character", 'c');
        JAVA_BASE_TYPE.put("java.lang.String", "string");
        JAVA_BASE_TYPE.put("java.lang.Object", new Object());
        JAVA_BASE_TYPE.put("java.lang.Void", null);
        JAVA_BASE_TYPE.put("java.util.Date", new Date());
        JAVA_BASE_TYPE.put("java.time.LocalDate", LocalDate.now());
        JAVA_BASE_TYPE.put("java.time.LocalDateTime", LocalDateTime.now());
        JAVA_BASE_TYPE.put("java.math.BigDecimal", new BigDecimal("9.9"));
        JAVA_BASE_TYPE.put("java.util.Map", new HashMap<>());
        JAVA_BASE_TYPE.put("java.util.HashMap", new HashMap<>());
        JAVA_BASE_TYPE.put("com.alibaba.fastjson.JSONObject", new HashMap<>());

        PRIMITIVE_BOX_MAP = new HashMap<>();
        PRIMITIVE_BOX_MAP.put("byte", "java.lang.Byte");
        PRIMITIVE_BOX_MAP.put("short", "java.lang.Short");
        PRIMITIVE_BOX_MAP.put("int", "java.lang.Integer");
        PRIMITIVE_BOX_MAP.put("long", "java.lang.Long");
        PRIMITIVE_BOX_MAP.put("float", "java.lang.Float");
        PRIMITIVE_BOX_MAP.put("double", "java.lang.Double");
        PRIMITIVE_BOX_MAP.put("boolean", "java.lang.Boolean");
        PRIMITIVE_BOX_MAP.put("char", "java.lang.Character");
        PRIMITIVE_BOX_MAP.put("void", "java.lang.Void");

        COLLECTION_TYPE = new HashMap<>();
        COLLECTION_TYPE.put("java.util.Collection", new ArrayList<>());
        COLLECTION_TYPE.put("java.util.List", new ArrayList<>());
        COLLECTION_TYPE.put("java.util.Set", new HashSet<>());
        COLLECTION_TYPE.put("java.util.ArrayList", new HashSet<>());
        COLLECTION_TYPE.put("java.util.HashSet", new HashSet<>());

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
        return JAVA_BASE_TYPE.containsKey(type);
    }

    /**
     * 获取基础类的默认值
     *
     * @param type type
     * @return {@link Object}
     */
    public static Object getDefaultValue(String type) {
        return JAVA_BASE_TYPE.get(type);
    }

    /**
     * 优先根据字段名从配置中获取值，找不到再使用默认值
     *
     * @param fieldName fieldName
     * @param type      type
     * @return {@link Object}
     */
    public static Object getMappedDefaultValue(String fieldName, String type) {
        Map<String, String> fieldValueMap = PluginSettingState.getInstance().getFieldValueMap();
        if (!fieldValueMap.containsKey(fieldName)) {
            return getDefaultValue(type);
        }
        String mapValue = fieldValueMap.get(fieldName);
        if (NumberUtils.isCreatable(mapValue)) {
            return NumberUtils.createNumber(mapValue);
        }
        return mapValue;
    }

    /**
     * 是否集合类型，包含数组类型
     *
     * @param typeInfo type
     * @return {@link boolean}
     */
    public static boolean isCollection(TypeInfo typeInfo) {
        if (Objects.nonNull(typeInfo.getPsiType()) && PsiTool.isCollection(typeInfo.getPsiType())) {
            return true;
        }
        if (typeInfo.getNameGenericsCanonical().endsWith("[]")) {
            return true;
        }
        return COLLECTION_TYPE.containsKey(typeInfo.getNameCanonical());
    }
}
