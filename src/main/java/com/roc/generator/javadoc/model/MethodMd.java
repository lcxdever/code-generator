package com.roc.generator.javadoc.model;

import com.google.common.collect.Lists;
import com.intellij.psi.*;
import com.roc.generator.model.TypeInfo;
import com.roc.generator.model.FieldInfo;
import com.roc.generator.model.MethodInfo;
import com.roc.generator.util.*;
import com.roc.generator.util.FieldInfoUtil.StaticFilter;
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
     * 方法全部信息
     */
    private String methodTextFull;

    /**
     * 方法简要注释信息
     */
    private String methodCommentSimple;

    /**
     * 参数类型信息
     */
    private List<FieldMd> parameterTypes;

    /**
     * 参数类型描述信息，将参数涉及到的类打平到 List 做了描述
     */
    private List<ClassMd> parameterTypeDescribe;

    /**
     * 参数示例
     */
    private String parameterEg;

    /**
     * 返回数据类型，包含泛型
     */
    private String returnTypeNameGenericsSimple;

    /**
     * 返回数据类型描述信息，将返回参数涉及到的类打平到 List 做了描述
     */
    private List<ClassMd> returnTypeDescribe;

    /**
     * 返回参数示例
     */
    private String returnEg;

    public MethodMd(MethodInfo methodInfo) {
        PsiMethod psiMethod = methodInfo.getPsiMethod();
        this.classNameCanonical = methodInfo.getTypeInfo().getNameCanonical();
        this.methodTextFull = MdUtil.commentFormat(psiMethod.getText());
        // 获取注释信息
        this.methodCommentSimple = String.join("\n", PsiTool.getCommentSimple(psiMethod.getDocComment()));
        // 构建方法参数 MD 对象
        this.parameterTypes = MdUtil.getMethodParamMd(methodInfo);
        // 请求参数列表
        this.parameterTypeDescribe = new ArrayList<>();
        for (PsiParameter psiParameter : methodInfo.getParameters()) {
            // 判断是合法的参数才进行添加
            if (isValidParameter(psiParameter)) {
                addGenerics(this.parameterTypeDescribe, psiParameter.getType(), null);
            }
        }

        PsiType returnType = Objects.requireNonNull(psiMethod.getReturnType());
        // 返回参数类型
        this.returnTypeNameGenericsSimple = MdUtil.spChartReplace(TypeInfo.fromPsiType(returnType).getNameGenericsSimple());
        // 返回参数列表
        this.returnTypeDescribe = new ArrayList<>();
        addGenerics(this.returnTypeDescribe, returnType, null);
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
    private void addGenerics(List<ClassMd> params, PsiType psiType, GenericsHelper parentGenericsHelper) {
        // 为了兼容 array 类型，使用 getDeepComponentType，需要描述的基础类型
        psiType = psiType.getDeepComponentType();
        // 如果是泛型尝试获取真实类型
        if (Objects.nonNull(parentGenericsHelper)) {
            psiType = parentGenericsHelper.getRelType(psiType);
        }

        TypeInfo typeInfo = TypeInfo.fromPsiType(psiType);
        List<FieldInfo> fields = Lists.newArrayList();

        // 泛型帮助类
        GenericsHelper genericsHelper = GenericsHelper.getInstance(psiType);
        genericsHelper.union(parentGenericsHelper);

        // java 基础类型、集合类型不需要做描述
        if (!MdUtil.ignoreType(typeInfo) && !contains(params, typeInfo)) {
            ClassMd classMd = ClassMd.fromClassInfo(typeInfo);
            params.add(classMd);

            fields = FieldInfoUtil.getFieldInfoFromPsiType(psiType, new StaticFilter());
            // 做字段转换，字段类型处理等
            for (FieldInfo fieldInfo : fields) {
                FieldMd fieldMd = FieldMd.fromFieldInfo(fieldInfo);
                fieldMd.setFieldTypeWithReplace(GenericsUtil.getGenericsRealType(genericsHelper, fieldInfo.getPsiField().getType()));
                classMd.getFields().add(fieldMd);
            }
        }
        // 递归添加字段类型描述
        for (FieldInfo fieldInfo : fields) {
            addGenerics(params, fieldInfo.getPsiField().getType(), genericsHelper);
        }
        // 如果是 class 泛型，如 List<OpUser>，将递归添加泛型类型
        if (psiType instanceof PsiClassType) {
            for (PsiType type : ((PsiClassType) psiType).getParameters()) {
                addGenerics(params, type, genericsHelper);
            }
        }

    }

    /**
     * 是否已经包含的类型
     *
     * @param params   params
     * @param typeInfo typeInfo
     * @return {@link boolean}
     */
    private boolean contains(List<ClassMd> params, TypeInfo typeInfo) {
        return params.stream().anyMatch(e -> Objects.equals(e.getClassNameSimple(), typeInfo.getNameSimple()));
    }
}
