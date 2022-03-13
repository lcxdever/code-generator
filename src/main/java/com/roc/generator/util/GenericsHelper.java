package com.roc.generator.util;

import com.google.common.collect.Maps;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiTypeParameter;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiUtil;
import lombok.Setter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

/**
 * @author 鱼蛮 on 2022/3/12
 **/
public class GenericsHelper {

    /**
     * 泛型名称与实际类型的映射
     */
    @Setter
    private Map<String, PsiType> genericsMap;

    private GenericsHelper(){}

    private Map<String, PsiType> getGenericsMap() {
        return genericsMap;
    }

    /**
     * 获取泛型类型与实际类型的映射
     *
     * @param psiType psiType
     * @return {@link Map <String, PsiType >}
     */
    public static GenericsHelper getInstance(PsiType psiType) {
        Map<String, PsiType>  genericsMap = Maps.newHashMap();
        GenericsHelper genericsHelper = new GenericsHelper();
        genericsHelper.setGenericsMap(genericsMap);

        if (!(psiType instanceof PsiClassReferenceType)) {
            return genericsHelper;
        }
        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
        if (Objects.isNull(psiClass)) {
            return genericsHelper;
        }

        PsiType[] fieldTypeParameters = ((PsiClassReferenceType) psiType).getParameters();
        PsiTypeParameter[] classTypeParameters = psiClass.getTypeParameters();
        for (int i = 0; i < fieldTypeParameters.length; i++) {
            // 加下修饰，防止重名出错
            genericsMap.put(getGenericsDecorate(classTypeParameters[i].getName()), fieldTypeParameters[i]);
        }
        return genericsHelper;
    }

    /**
     * 获取 PsiClass 中泛型 Type 中包含的真实类型， 如 T a = ; List<T> list = ;，如果找不到将返回传入的 Type
     * @param psiType psiType
     * @return {@link PsiType}
     */
    public PsiType getRelType(PsiType psiType) {
        return Optional.ofNullable(genericsMap.get(getGenericsDecorate(psiType.getCanonicalText()))).orElse(psiType);
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

    /**
     * 聚合父类中的泛型映射
     *
     * @param parentGenerics parentGenerics
     */
    public void union(GenericsHelper parentGenerics) {
        if (Objects.isNull(parentGenerics)) {
            return ;
        }
        for (Entry<String, PsiType> entry : parentGenerics.getGenericsMap().entrySet()) {
            String key = entry.getKey();
            PsiType childType = this.genericsMap.get(key);
            // 如果当前不包含，直接添加
            if (Objects.isNull(childType)) {
                this.genericsMap.put(key, entry.getValue());
                continue;
            }
            // 否则判断下当前的是否跟 key 一致，是的话用父的
            if (Objects.equals(getGenericsDecorate(childType.getCanonicalText()), key)) {
                this.genericsMap.put(key, entry.getValue());
            }
        }
    }
}
