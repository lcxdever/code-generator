package com.roc.generator.javadoc.impl;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import com.roc.generator.model.AnnotationInfo;
import com.roc.generator.model.TypeInfo;

import java.util.List;
import java.util.Objects;

/**
 * javax.validation.constraints.Size
 * 格式化
 *
 * @author 鱼蛮 on 2022/2/19
 **/
public class AnnoSize extends AbstractRangeAnno {

    public static final String SIZE = "javax.validation.constraints.Size";

    @Override
    protected String getDescribeType(AnnotationInfo annotationInfo) {
        PsiElement element = Objects.requireNonNull(Objects.requireNonNull(annotationInfo.getPsiAnnotation().getParent()).getParent());
        PsiType psiType = ((PsiVariable)element).getType();
        TypeInfo typeInfo = TypeInfo.fromPsiType(psiType);
        if (Objects.equals(typeInfo.getNameCanonical(), "java.lang.String")) {
            return "长度";
        } else {
            return "数量";
        }
    }

    @Override
    public List<String> support() {
        return Lists.newArrayList(SIZE);
    }

}
