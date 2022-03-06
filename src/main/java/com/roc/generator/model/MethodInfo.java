package com.roc.generator.model;

import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

/**
 * 方法信息封装
 *
 * @author 鱼蛮 Date 2022/2/14
 */
@Getter
@Setter
@ToString
public class MethodInfo {

    /**
     * 归属类信息
     */
    private ClassInfo classInfo;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 方法名称(首字母大写)
     */
    private String methodNameUp;

    /**
     * 注释信息
     */
    private String comment;

    /**
     * 方法的所有文本信息
     */
    private String textFull;

    /**
     * 原始注释信息
     */
    private PsiDocComment psiDocComment;

    /**
     * 参数列表
     */
    private PsiParameterList parameterList;

    /**
     * 返回类型
     */
    private PsiType returnType;

    /**
     * PsiMethod
     */
    private PsiMethod psiMethod;

    public static MethodInfo fromPsiMethod(PsiMethod psiMethod) {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setClassInfo(ClassInfo.fromClassNameText(((PsiClass)psiMethod.getParent()).getQualifiedName()));
        methodInfo.setMethodName(psiMethod.getName());
        methodInfo.setPsiDocComment(psiMethod.getDocComment());
        methodInfo.setComment(Optional.ofNullable(psiMethod.getDocComment()).map(PsiDocComment::getText).orElse(""));
        methodInfo.setTextFull(psiMethod.getText());
        methodInfo.setReturnType(psiMethod.getReturnType());
        methodInfo.setParameterList(psiMethod.getParameterList());
        methodInfo.setPsiMethod(psiMethod);
        return methodInfo;
    }
}