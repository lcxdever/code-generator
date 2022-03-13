package com.roc.generator.util;

import com.google.common.collect.Lists;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.roc.generator.model.TypeInfo;
import com.roc.generator.model.FieldInfo;

import java.util.*;

/**
 * @author 鱼蛮 on 2022/3/12
 **/
public class JavaJsonUtil {

    /**
     * 根据 PsiType 生成 JSON 结构数据
     *
     * @param psiType psiType
     * @return {@link Object}
     */
    public static Object genJsonFromPsiType(PsiType psiType) {
        return genJsonFromPsiType(psiType, null);
    }

    /**
     * 根据 PsiType 生成 JSON 结构数据
     *
     * @param psiType psiType
     * @return {@link Object}
     */
    public static Object genJsonFromPsiType(PsiType psiType, GenericsHelper parentGenerics ) {
        // 首先尝试获取真实类型
        if (Objects.nonNull(parentGenerics)) {
            psiType = parentGenerics.getRelType(psiType);
        }
        TypeInfo typeInfo = TypeInfo.fromPsiType(psiType);
        String nameFull = typeInfo.getNameCanonical();
        // 基础类型，直接返回
        if (TypeUtil.isJavaBaseType(nameFull)) {
            return TypeUtil.getDefaultValue(nameFull);
        }
        // 集合类型返回集合数据
        else if (TypeUtil.isCollection(typeInfo)) {
            return Lists.newArrayList(genJsonFromPsiType(getCollectionContainType(psiType), parentGenerics));
        }
        // 其他类型，获取原始 class 信息，并获取字段信息进行组装
        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
        if (Objects.isNull(psiClass)) {
            return null;
        }
        Map<String, Object> jsonMap = new HashMap<>(8);
        // 获取当前类型的的泛型对应 map，在泛型类中可以找到真实类型
        GenericsHelper genericsHelper = GenericsHelper.getInstance(psiType);
        // 聚合下父类型中的 genericsMap
        genericsHelper.union(parentGenerics);

        for (PsiField field : psiClass.getAllFields()) {
            // 过滤掉静态字段
            if (FieldInfoUtil.isStatic(field)) {
                continue;
            }
            String fieldName = field.getName();
            // 防止无限循环
            if (field.getType() == psiType) {
                jsonMap.put(fieldName, null);
                continue;
            }

            FieldInfo fieldInfo = FieldInfo.fromPsiField(field);
            String fieldTypeNameCanonical = fieldInfo.getTypeInfo().getNameCanonical();

            // java 基础类型
            if (TypeUtil.isJavaBaseType(fieldTypeNameCanonical)) {
                jsonMap.put(fieldName, TypeUtil.getMappedDefaultValue(field.getName(), fieldTypeNameCanonical));
            } else {
                jsonMap.put(fieldName, genJsonFromPsiType(field.getType(), genericsHelper));
            }
        }
        return jsonMap;
    }

    /**
     * 从 PsiClass 生成 JSON 结构数据
     *
     * @param psiClass psiClass
     * @return {@link Object}
     */
    public static Object genJsonFromPsiClass(PsiClass psiClass) {
        TypeInfo typeInfo = TypeInfo.fromPsiClass(psiClass);
        String nameFull = typeInfo.getNameCanonical();

        // 基础类型，直接返回
        if (TypeUtil.isJavaBaseType(nameFull)) {
            return TypeUtil.getDefaultValue(nameFull);
        }
        // 集合类返回集合数据
        else if (TypeUtil.isCollection(typeInfo)) {
            return Lists.newArrayList();
        }
        // 其他类型，获取原始 class 信息，并获取字段信息进行组装
        Map<String, Object> jsonMap = new HashMap<>(8);
        for (PsiField field : psiClass.getAllFields()) {
            // 过滤掉静态字段
            if (FieldInfoUtil.isStatic(field)) {
                continue;
            }
            FieldInfo fieldInfo = FieldInfo.fromPsiField(field);
            String fieldClassNameFull = fieldInfo.getTypeInfo().getNameCanonical();
            String fieldName = fieldInfo.getFieldName();
            // java 基础类型
            if (TypeUtil.isJavaBaseType(fieldClassNameFull)) {
                jsonMap.put(fieldName, TypeUtil.getMappedDefaultValue(field.getName(), fieldClassNameFull));
            }
            // 其他类型
            else {
                jsonMap.put(fieldName, genJsonFromPsiType(field.getType()));
            }
        }
        return jsonMap;
    }

    /**
     * 获取集合类型（包含数组）包含的类型
     *
     * @param psiType psiType
     * @return {@link PsiType}
     */
    private static PsiType getCollectionContainType(PsiType psiType) {
        if (psiType instanceof PsiArrayType) {
            return ((PsiArrayType)psiType).getComponentType();
        }
        return ((PsiClassType) psiType).getParameters()[0];
    }
}
