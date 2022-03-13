package com.roc.generator.util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiParameter;
import com.roc.generator.javadoc.MdAnnotationFormatter;
import com.roc.generator.javadoc.model.ControllerMethodMd;
import com.roc.generator.model.AnnotationInfo;
import com.roc.generator.model.FieldInfo;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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
            if (NOT_NULL_ANNOTATION.contains(annotationInfo.getTypeInfo().getNameCanonical())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 参数是否非空
     *
     * @param psiParameter psiParameter
     * @return {@link boolean}
     */
    public static boolean notNull(PsiParameter psiParameter) {
        for (PsiAnnotation annotation : psiParameter.getAnnotations()) {
            if (NOT_NULL_ANNOTATION.contains(AnnotationInfo.fromPsiAnnotation(annotation).getTypeInfo().getNameCanonical())) {
                return true;
            }
        }
        // 处理 spring 注解
        PsiAnnotation requestParam = psiParameter.getAnnotation(ControllerMethodMd.REQUEST_PARAM);
        if (Objects.isNull(requestParam)) {
            requestParam = psiParameter.getAnnotation(ControllerMethodMd.REQUEST_BODY);
        }
        if (Objects.isNull(requestParam)) {
            requestParam = psiParameter.getAnnotation(ControllerMethodMd.PATH_VARIABLE);
        }
        if (Objects.nonNull(requestParam)) {
            String value = Optional.ofNullable(requestParam.findAttributeValue("required")).map(PsiAnnotationMemberValue::getText).orElse("");
            return Objects.equals(value, "true");
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
        return getDescribe(fieldInfo.getAnnotations(), fieldInfo.getCommentSimple());
    }

    /**
     * 解析注解，并合并到参数说明中
     *
     * @param parameter parameter
     * @param paramDoc  paramDoc
     * @return {@link String}
     */
    public static String getDescribeWithAnnotation(PsiParameter parameter, String paramDoc) {
        List<AnnotationInfo> annotations = Arrays.stream(parameter.getAnnotations())
                .map(AnnotationInfo::fromPsiAnnotation)
                .collect(Collectors.toList());
        return getDescribe(annotations, paramDoc);
    }

    /**
     * 解析注解，并合并到参数说明中
     *
     * @param annotations annotations
     * @param docOriginal docOriginal
     * @return {@link String}
     */
    public static String getDescribe(List<AnnotationInfo> annotations, String docOriginal) {
        StringBuilder sb = new StringBuilder();
        // 获取支持的格式化工具，并执行格式化
        for (AnnotationInfo annotationInfo : annotations) {
            for (MdAnnotationFormatter formatter : MdAnnotationFormatter.EXTENSION_NAME.getExtensionList()) {
                if (formatter.support().contains(annotationInfo.getTypeInfo().getNameCanonical())) {
                    sb.append(formatter.format(annotationInfo)).append(";");
                }
            }
        }
        // 如果存在可以格式化的注解
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            if (StringUtils.isBlank(docOriginal)) {
                return sb.toString();
            }
            return sb.insert(0, "(").insert(0, docOriginal).append(")").toString();
        }
        return docOriginal;
    }
}
