package com.roc.generator.javadoc.model;

import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiType;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
     * 简要注释信息
     */
    private String commentSimple;

    /**
     * 参数描述信息
     */
    private List<FieldMd> parameter;

    /**
     * 参数类型信息，将参数涉及到的类打平到List做了描述
     */
    private List<ClassMd> parameters;

    /**
     * 返回类型信息，将返回参数涉及到的类打平到List做了描述
     */
    private List<ClassMd> returnTypes;

    public MethodMd() {}

    public MethodMd(MethodInfo methodInfo) {
        this(methodInfo, false);
    }

    public MethodMd(MethodInfo methodInfo, boolean isController) {
        this.classNameFull = methodInfo.getClassInfo().getClassNameFull();
        this.methodTextFull = MdUtil.commentFormat(methodInfo.getTextFull());
        // 获取注释信息
        this.commentSimple = MdUtil.getCommentSimple(methodInfo.getPsiDocComment());
        // 构建方法参数 MD 对象
        this.parameter = MdUtil.getMethodParamMd(methodInfo, isController);
        // 请求参数列表
        List<ClassMd> params = new ArrayList<>();
        this.parameters = params;
        PsiParameterList parameterList = methodInfo.getParameterList();
        for (int i = 0; i < parameterList.getParametersCount(); i++) {
            PsiParameter psiParameter = parameterList.getParameter(i);
            if (Objects.isNull(psiParameter)) {
                continue;
            }
            if (isController && !MdUtil.isValidControllerParameter(psiParameter)) {
                continue;
            }
            addGenerics(params, psiParameter.getType());
        }
        // 返回参数列表
        List<ClassMd> returns = new ArrayList<>();
        this.returnTypes = returns;
        addGenerics(returns, methodInfo.getReturnType());
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
