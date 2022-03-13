package com.roc.generator.javadoc.model;

import com.intellij.psi.*;
import com.roc.generator.model.TypeInfo;
import com.roc.generator.model.FieldInfo;
import com.roc.generator.model.MethodInfo;
import com.roc.generator.util.*;
import com.roc.generator.util.FieldInfoUtil.StaticFinalFilter;
import com.roc.generator.util.GenericsUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 方法 markdown 描述
 *
 * @author 鱼蛮 on 2022/2/20
 **/
@Getter
@Setter
@ToString
public class MethodMd {

    /**
     * 全类名
     */
    private String classNameCanonical;

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
     * 返回的数据类型
     */
    private String returnTypeNameGenericsSimple;

    /**
     * 返回类型信息，将返回参数涉及到的类打平到List做了描述
     */
    private List<ClassMd> returnTypes;

    /**
     * 返回参数示例
     */
    private String returnEg;

    public MethodMd(MethodInfo methodInfo) {
        PsiMethod psiMethod = methodInfo.getPsiMethod();
        this.classNameCanonical = methodInfo.getTypeInfo().getNameCanonical();
        this.methodTextFull = MdUtil.commentFormat(psiMethod.getText());
        // 获取注释信息
        this.commentSimple = PsiTool.getCommentSimple(psiMethod.getDocComment());
        // 构建方法参数 MD 对象
        this.parameter = MdUtil.getMethodParamMd(methodInfo);
        // 请求参数列表
        this.parameters = new ArrayList<>();
        for (PsiParameter psiParameter : methodInfo.getParameters()) {
            // 判断是合法的参数才进行添加
            if (isValidParameter(psiParameter)) {
                addGenerics(this.parameters, psiParameter.getType());
            }
        }

        PsiType returnType = Objects.requireNonNull(psiMethod.getReturnType());

        this.returnTypeNameGenericsSimple = MdUtil.spChartReplace(TypeInfo.fromPsiType(returnType).getNameGenericsSimple());
        // 返回参数列表
        this.returnTypes = new ArrayList<>();
        addGenerics(this.returnTypes, returnType);
        // 返回示例
        this.returnEg = GsonUtil.prettyJson(JavaJsonUtil.genJsonFromPsiType(returnType));
    }

    /**
     * 是否合法参数
     *
     * @param psiParameter psiParameter
     * @return {@link boolean}
     */
    protected boolean isValidParameter(PsiParameter psiParameter) {
        return true;
    }

    /**
     * 递归添加泛型类型
     *
     * @param params  params
     * @param psiType psiType
     */
    private void addGenerics(List<ClassMd> params, PsiType psiType) {
        // 为了兼容 array 类型，使用 getDeepComponentType
        TypeInfo typeInfo = TypeInfo.fromPsiType(psiType.getDeepComponentType());
        // java 基础类型不做描述
        if (!MdUtil.ignoreType(typeInfo)) {
            ClassMd classMd = ClassMd.fromClassInfo(typeInfo);
            GenericsHelper genericsHelper = GenericsHelper.getInstance(psiType);

            List<FieldInfo> fields = FieldInfoUtil.getFieldInfoFromPsiType(psiType, new StaticFinalFilter());
            // 做字段转换，字段类型处理等
            for (FieldInfo fieldInfo : fields) {
                FieldMd fieldMd = FieldMd.fromFieldInfo(fieldInfo);
                fieldMd.setFieldTypeWithReplace(GenericsUtil.getGenericsRealType(genericsHelper, fieldInfo.getPsiField().getType()));
                classMd.getFields().add(fieldMd);
            }
            params.add(classMd);
        }
        // 如果是 class 类型，并且是泛型，将递归添加泛型类型
        if (psiType instanceof PsiClassType) {
            for (PsiType type : ((PsiClassType) psiType).getParameters()) {
                addGenerics(params, type);
            }
        }
    }
}
