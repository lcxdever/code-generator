package com.roc.generator.javadoc.model;

import com.google.common.collect.Maps;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiUtil;
import com.roc.generator.model.ClassInfo;
import com.roc.generator.model.FieldInfo;
import com.roc.generator.model.MethodInfo;
import com.roc.generator.util.FieldInfoUtil;
import com.roc.generator.util.FieldInfoUtil.StaticFinalFilter;
import com.roc.generator.util.GenericsUtil;
import com.roc.generator.util.MdUtil;
import com.roc.generator.util.TypeUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

/**
 * @author 鱼蛮 on 2022/2/20
 **/
@Getter
@Setter
@ToString
public class MethodMd {

    /**
     * 全类名
     */
    private String classNameFull;

    /**
     * 全方法信息
     */
    private String methodTextFull;

    /**
     * 参数类型信息
     */
    private List<ClassMd> parameters;

    /**
     * 返回类型信息
     */
    private List<ClassMd> returnTypes;

    /**
     * 从 MethodInfo 中创建
     *
     * @param methodInfo methodInfo
     * @return {@link com.roc.generator.javadoc.model.MethodMd}
     */
    public static MethodMd fromMethodInfo(MethodInfo methodInfo) {
        MethodMd methodMd = new MethodMd();
        methodMd.setClassNameFull(methodInfo.getClassInfo().getClassNameFull());
        methodMd.setMethodTextFull(MdUtil.commentFormat(methodInfo.getTextFull()));
        List<ClassMd> params = new ArrayList<>();
        methodMd.setParameters(params);
        PsiParameterList parameterList = methodInfo.getParameterList();
        for (int i = 0; i < parameterList.getParametersCount(); i++) {
            PsiParameter psiParameter = parameterList.getParameter(i);
            if (Objects.isNull(psiParameter)) {
                continue;
            }
            addGenerics(params, psiParameter.getType());
        }

        List<ClassMd> returns = new ArrayList<>();
        methodMd.setReturnTypes(returns);
        addGenerics(returns, methodInfo.getReturnType());

        return methodMd;
    }

    /**
     * 递归添加泛型类型
     *
     * @param params  params
     * @param psiType psiType
     */
    private static void addGenerics(List<ClassMd> params, PsiType psiType) {
        ClassInfo classInfo = ClassInfo.fromClassPsiType(psiType);
        // java 基础类型不做描述
        if (!TypeUtil.isJavaBaseType(classInfo.getClassNameFull())) {
            List<FieldInfo> fields = FieldInfoUtil.getFieldInfoFromPsiType(psiType, new StaticFinalFilter());
            ClassMd classMd = ClassMd.fromClassInfo(classInfo);
            classMd.setFields(new ArrayList<>());

            Map<String, PsiType> genericsMap = GenericsUtil.getGenericsMap(psiType);
            // 做字段转换，字段类型处理等
            for (FieldInfo fieldInfo : fields) {
                FieldMd fieldMd = FieldMd.fromFieldInfo(fieldInfo);
                fieldMd.setFieldTypeWithReplace(GenericsUtil.getGenericsRealType(genericsMap, fieldInfo.getPsiField().getType()));
                classMd.getFields().add(fieldMd);
            }
            params.add(classMd);
        }
        if (psiType instanceof PsiClassType) {
            PsiClassType psiClassType = (PsiClassType) psiType;
            if (psiClassType.getParameters().length == 0) {
                return;
            }
            for (PsiType type : psiClassType.getParameters()) {
                addGenerics(params, type);
            }
        }
    }

}
