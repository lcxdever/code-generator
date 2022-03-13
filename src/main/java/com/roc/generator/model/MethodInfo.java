package com.roc.generator.model;

import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
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
    private TypeInfo typeInfo;

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
     * 参数列表
     */
    private PsiParameter[] parameters;

    /**
     * PsiMethod
     */
    private PsiMethod psiMethod;

    public static MethodInfo fromPsiMethod(PsiMethod psiMethod) {
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setTypeInfo(TypeInfo.fromPsiClass(Objects.requireNonNull(PsiUtil.getTopLevelClass(psiMethod))));
        methodInfo.setMethodName(psiMethod.getName());
        methodInfo.setComment(Optional.ofNullable(psiMethod.getDocComment()).map(PsiDocComment::getText).orElse(""));
        methodInfo.setParameters(psiMethod.getParameterList().getParameters());
        methodInfo.setPsiMethod(psiMethod);
        return methodInfo;
    }
}