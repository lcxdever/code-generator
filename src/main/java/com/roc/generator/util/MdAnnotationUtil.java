package com.roc.generator.util;

import com.roc.generator.javadoc.MdAnnotationFormatter;
import com.roc.generator.model.AnnotationInfo;
import com.roc.generator.model.FieldInfo;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * @author 鱼蛮 on 2022/2/19
 **/
public class MdAnnotationUtil {

    /**
     * 非空的注解集合
     */
    public static final Set<String> NOT_NULL_ANNOTATION = Sets.newHashSet(
            "javax.validation.constraints.NotBlank",
            "javax.validation.constraints.NotEmpty",
            "javax.validation.constraints.NotNull"
            );

    /**
     * 字段是否非空
     *
     * @param fieldInfo fieldInfo
     * @return {@link boolean}
     */
    public static boolean notNull(FieldInfo fieldInfo) {
        for (AnnotationInfo annotationInfo : fieldInfo.getAnnotations()) {
            if (NOT_NULL_ANNOTATION.contains(annotationInfo.getClassInfo().getClassNameFull())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解析注解，并合并到参数说明中
     *
     * @param fieldInfo fieldInfo
     * @return {@link java.lang.String}
     */
    public static String getDescribeWithAnnotation(FieldInfo fieldInfo) {
        StringBuilder sb = new StringBuilder();
        // 获取支持的格式化工具，并执行格式化
        for (AnnotationInfo annotationInfo : fieldInfo.getAnnotations()) {
            for (MdAnnotationFormatter formatter : MdAnnotationFormatter.EXTENSION_NAME.getExtensionList()) {
                if (formatter.support().contains(annotationInfo.getClassInfo().getClassNameFull())) {
                    sb.append(formatter.format(annotationInfo)).append(";");
                }
            }
        }
        // 如果存在可以格式化的注解
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            if (StringUtils.isBlank(fieldInfo.getCommentSimple())) {
                return sb.toString();
            }
            return sb.insert(0, "(").insert(0, fieldInfo.getCommentSimple()).append(")").toString();
        }
        return fieldInfo.getCommentSimple();
    }
}
