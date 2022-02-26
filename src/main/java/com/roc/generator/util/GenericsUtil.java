package com.roc.generator.util;

import com.google.common.collect.Maps;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiUtil;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 泛型工具类
 *
 * @author 鱼蛮 on 2022/2/23
 **/
public class GenericsUtil {

    /**
     * 获取泛型类型的实际类型
     *
     * @param map       map
     * @param fieldType fieldType
     * @return {@link String}
     */
    public static String getGenericsRealType(Map<String, PsiType> map, PsiType fieldType) {
        if (!(fieldType instanceof PsiClassType)) {
            return fieldType.getPresentableText();
        }
        PsiClassType psiClassType = (PsiClassType) fieldType;
        // 首先看字段本身是否泛型，如 T data
        StringBuilder sb = new StringBuilder(
                Optional.ofNullable(map.get(getGenericsDecorate(psiClassType.getPresentableText())))
                        .map(PsiType::getPresentableText)
                        .orElse(psiClassType.getClassName())
        );
        // 再看字段是否包含泛型，如 List<T> data
        PsiType[] types = psiClassType.getParameters();
        if (types.length > 0) {
            sb.append("<");
        }
        for (PsiType psiType : types) {
            sb.append(
                    Optional.ofNullable(map.get(getGenericsDecorate(psiType.getCanonicalText())))
                            .map(PsiType::getPresentableText)
                            .orElse(psiType.getPresentableText()));
        }
        if (types.length > 0) {
            sb.append(">");
        }
        return sb.toString();
    }

    /**
     * 获取泛型类型与实际类型的映射
     *
     * @param psiType psiType
     * @return {@link Map <String, PsiType >}
     */
    public static Map<String, PsiType> getGenericsMap(PsiType psiType) {
        Map<String, PsiType> genericsMap = Maps.newHashMap();
        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
        if (Objects.isNull(psiClass)) {
            return genericsMap;
        }
        if (!(psiType instanceof PsiClassReferenceType)) {
            return genericsMap;
        }
        PsiType[] fieldTypeParameters = ((PsiClassReferenceType) psiType).getParameters();
        PsiTypeParameter[] classTypeParameters = psiClass.getTypeParameters();
        for (int i = 0; i < fieldTypeParameters.length; i++) {
            // 加下修饰，防止重名出错
            genericsMap.put(getGenericsDecorate(classTypeParameters[i].getName()), fieldTypeParameters[i]);
        }
        return genericsMap;
    }

    /**
     * 获取带泛型修饰的样式，主要防止重名类
     *
     * @param name name
     * @return {@link java.lang.String}
     */
    private static String getGenericsDecorate(String name) {
        return "<" + name + ">";
    }
}
