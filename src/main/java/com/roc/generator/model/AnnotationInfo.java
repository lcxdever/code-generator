package com.roc.generator.model;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiNameValuePair;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

/**
 * 枚举封装信息
 *
 * @author 鱼蛮 on 2022/2/19
 **/
@Getter
@Setter
@ToString
public class AnnotationInfo {

    /**
     * 类信息
     */
    private ClassInfo classInfo;

    /**
     * 注解文本
     */
    private String text;

    /**
     * 属性
     */
    private Map<String, String> attributes;

    /**
     * 根据 PsiAnnotation 类型创建
     *
     * @param psiAnnotation psiAnnotation
     * @return {@link AnnotationInfo}
     */
    public static AnnotationInfo fromPsiAnnotation(PsiAnnotation psiAnnotation) {
        AnnotationInfo annotationInfo = new AnnotationInfo();
        annotationInfo.setClassInfo(ClassInfo.fromClassNameText(psiAnnotation.getQualifiedName()));
        annotationInfo.setText(psiAnnotation.getText());
        Map<String, String> attributes = new HashMap<>();
        for (PsiNameValuePair nameValuePair : psiAnnotation.getParameterList().getAttributes()) {
            attributes.put(nameValuePair.getAttributeName(),
                    Optional.ofNullable(nameValuePair.getValue()).map(PsiAnnotationMemberValue::getText).orElse(""));
        }
        annotationInfo.setAttributes(attributes);
        return annotationInfo;
    }
}
