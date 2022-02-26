package com.roc.generator.util;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtil;
import com.roc.generator.model.FieldInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author 鱼蛮 on 2022/2/21
 **/
public class FieldInfoUtil {

    /**
     * 从 PsiType 中获取字段信息
     *
     * @param psiType     psiType
     * @param fieldFilter fieldFilter
     * @return {@link java.util.List<com.roc.generator.model.FieldInfo>}
     */
    public static List<FieldInfo> getFieldInfoFromPsiType(PsiType psiType, FieldFilter fieldFilter) {
        List<FieldInfo> fields = new ArrayList<>();
        PsiClass psiClass = PsiUtil.resolveClassInType(psiType);
        if (Objects.isNull(psiClass)) {
            return fields;
        }
        return getFieldInfoFromPsiClass(psiClass, fieldFilter);
    }

    /**
     * 从 PsiClass 中获取字段信息
     *
     * @param psiClass    psiClass
     * @param fieldFilter fieldFilter
     * @return {@link java.util.List<com.roc.generator.model.FieldInfo>}
     */
    public static List<FieldInfo> getFieldInfoFromPsiClass(PsiClass psiClass, FieldFilter fieldFilter) {
        List<FieldInfo> fields = new ArrayList<>();
        for (PsiField psiField : psiClass.getAllFields()) {
            if (fieldFilter.needFilter(psiField)) {
                continue;
            }
            fields.add(FieldInfo.fromPsiField(psiField));
        }
        return fields;
    }

    /**
     * static 或者 final 修饰的字段进行过滤
     */
    public static class StaticFinalFilter implements FieldFilter {

        @Override
        public boolean needFilter(PsiField psiField) {
            PsiModifierList modifierList = psiField.getModifierList();
            if (Objects.isNull(modifierList)) {
                return false;
            }
            return modifierList.hasModifierProperty(PsiModifier.FINAL) || modifierList.hasModifierProperty(PsiModifier.STATIC);
        }
    }

    public interface FieldFilter {

        /**
         * 需要过滤掉的字段
         *
         * @param psiField psiField
         * @return {@link boolean}
         */
        boolean needFilter(PsiField psiField);
    }
}
