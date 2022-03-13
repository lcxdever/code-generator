package com.roc.generator.model;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 字段信息封装
 *
 * @author 鱼蛮 Date 2022/2/14
 */
@Getter
@Setter
@ToString
public class FieldInfo {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 注释信息
     */
    private String comment;

    /**
     * 类型信息
     */
    private TypeInfo typeInfo;

    /**
     * 注解信息
     */
    private List<AnnotationInfo> annotations;

    /**
     * PsiField
     */
    private PsiField psiField;

    /**
     * 从 PsiField 类型创建
     *
     * @param psiField psiField
     * @return {@link FieldInfo}
     */
    public static FieldInfo fromPsiField(PsiField psiField) {
        FieldInfo field = new FieldInfo();
        field.setFieldName(psiField.getName());
        field.setComment(Optional.ofNullable(psiField.getDocComment()).map(PsiElement::getText).orElse(""));
        field.setTypeInfo(TypeInfo.fromPsiType(psiField.getType()));
        List<AnnotationInfo> annotations = new ArrayList<>();
        for (PsiAnnotation psiAnnotation : psiField.getAnnotations()) {
            annotations.add(AnnotationInfo.fromPsiAnnotation(psiAnnotation));
        }
        field.setAnnotations(annotations);
        field.setPsiField(psiField);
        return field;
    }

    /**
     * 获取简单的注释信息，不带 * 号
     *
     * @return {@link java.lang.String}
     */
    public String getCommentSimple() {
        if (StringUtils.isBlank(comment)) {
            return "";
        }
        String result = StringUtils.replace(comment, "*", "");
        result = StringUtils.replace(result, "/", "");
        return Arrays.stream(result.split("\n")).filter(StringUtils::isNoneBlank).map(String::trim).collect(Collectors.joining("\n"));
    }

}
