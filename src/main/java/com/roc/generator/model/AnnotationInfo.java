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
     * 类型信息
     */
    private TypeInfo typeInfo;

    /**
     * 注解文本
     */
    private String text;

    /**
     * 属性
     */
    private Map<String, String> attributes;

    /**
     * PsiAnnotation
     */
    private PsiAnnotation psiAnnotation;

    /**
     * 获取属性值
     *
     * @param attribute attribute
     * @return {@link String}
     */
    public String getAttributeValue(String attribute) {
        return attributes.get(attribute);
    }

    private AnnotationInfo() {}

    /**
     * 根据 PsiAnnotation 类型创建
     *
     * @param psiAnnotation psiAnnotation
     * @return {@link AnnotationInfo}
     */
    public static AnnotationInfo fromPsiAnnotation(PsiAnnotation psiAnnotation) {
        AnnotationInfo annotationInfo = new AnnotationInfo();
        annotationInfo.setTypeInfo(TypeInfo.fromNameGenericsCanonical(psiAnnotation.getQualifiedName()));
        annotationInfo.setText(psiAnnotation.getText());
        Map<String, String> attributes = new HashMap<>();
        for (PsiNameValuePair nameValuePair : psiAnnotation.getParameterList().getAttributes()) {
            PsiAnnotationMemberValue value = nameValuePair.getValue();
            if (Objects.isNull(value)) {
                continue;
            }
            String text = value.getText();
            if (text.startsWith("\"") && text.endsWith("\"")) {
                text = text.substring(1, text.length() - 1);
            }
            attributes.put(nameValuePair.getAttributeName(), text);
        }
        annotationInfo.setAttributes(attributes);
        annotationInfo.setPsiAnnotation(psiAnnotation);
        return annotationInfo;
    }
}
