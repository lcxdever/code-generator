package com.roc.generator.util;

import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiClassType.ClassResolveResult;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiUtil;

/**
 * 泛型工具类
 *
 * @author 鱼蛮 on 2022/2/23
 **/
public class GenericsUtil {

    /**
     * 获取泛型类型的实际类型
     *
     * @param genericsHelper genericsHelper
     * @param fieldType      fieldType
     * @return {@link String}
     */
    public static String getGenericsRealType(GenericsHelper genericsHelper, PsiType fieldType) {
        // 原始类型，不需要做处理
        if (fieldType instanceof PsiPrimitiveType) {
            return fieldType.getPresentableText();
        }
        // 数组类型
        if (fieldType instanceof PsiArrayType) {
            PsiType typeTmp = ((PsiArrayType) fieldType).getComponentType();
            StringBuilder suffix = new StringBuilder("[]");
            while (typeTmp instanceof PsiArrayType) {
                typeTmp = ((PsiArrayType) typeTmp).getComponentType();
                suffix.append("[]");
            }
            return genericsHelper.getRelType(typeTmp).getPresentableText() + suffix;
        }
        // class 类型
        // 普通类型，不包含泛型
        PsiClassType psiClassType = (PsiClassType) fieldType;
        PsiType[] types = psiClassType.getParameters();
        if (types.length == 0) {
            return genericsHelper.getRelType(fieldType).getPresentableText();
        }
        // 泛型类型
        StringBuilder sb = new StringBuilder(psiClassType.getClassName())
                .append("<");
        for (PsiType psiType : types) {
            sb.append(genericsHelper.getRelType(psiType).getPresentableText());
        }
        return sb.append(">").toString();
    }
}
