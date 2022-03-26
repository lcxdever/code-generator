package com.roc.generator.util;

import com.google.common.collect.Maps;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.util.PsiTreeUtil;
import com.roc.generator.model.TypeInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * Psi 相关的工具类
 *
 * @author 鱼蛮 on 2022/3/12
 **/
public class PsiTool {

    /**
     * 只获取注释的说明文字
     *
     * @param doc doc
     * @return {@link String}
     */
    public static String getCommentSimple(PsiDocComment doc) {
        if (Objects.isNull(doc)) {
            return StringUtils.EMPTY;
        }
        // 获取方法描述
        for (PsiElement element : doc.getDescriptionElements()) {
            if (element instanceof PsiWhiteSpace) {
                continue;
            }
            if (StringUtils.isNoneBlank(element.getText())) {
                return element.getText();
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 解析注释信息，获取文档中参数与描述的对应关系
     *
     * @param doc doc
     * @return {@link Map<String,String>}
     */
    public static Map<String, String> getCommentParamDocMap(PsiDocComment doc) {
        Map<String, String> paramDocMap = Maps.newHashMap();
        if (Objects.isNull(doc)) {
            return paramDocMap;
        }
        // 获取参数映射，用户在 MD 中展示描述
        for (PsiDocTag tag : doc.findTagsByName("param")) {
            PsiElement[] elements = tag.getDataElements();
            if (elements.length > 1) {
                paramDocMap.put(elements[0].getText(), elements[1].getText());
            }
        }
        return paramDocMap;
    }

    /**
     * 获取 action 选择的类
     *
     * @param e action
     * @return {@link PsiClass}
     */
    public static PsiClass getSelectClass(AnActionEvent e) {
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return null;
        }
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return null;
        }
        if (!(psiFile instanceof PsiJavaFile)) {
            return null;
        }
        // 获取选择的类
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        return PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
    }

    /**
     * 设置选择的对象是普通 Class 时候才展现，对 interface、enum 不做展现
     * @param e e
     */
    public static void setNormalClassVisible(AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        PsiClass psiClass = PsiTool.getSelectClass(e);
        if (Objects.isNull(psiClass)) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        if (psiClass.isInterface() || psiClass.isEnum() || psiClass.isAnnotationType()) {
            presentation.setEnabledAndVisible(false);
        }
    }

    /**
     * 判断类是否数组，及集合类型，会递归获取父类来判断
     *
     * @param psiType psiType
     * @return {@link boolean}
     */
    public static boolean isCollection(PsiType psiType) {
        if (psiType instanceof PsiArrayType) {
            return true;
        }
        if (Objects.equals(TypeInfo.fromPsiType(psiType).getNameCanonical(), TypeUtil.TYPE_COLLECTION)) {
            return true;
        }
        PsiType[] superTypes = psiType.getSuperTypes();
        for (PsiType item : superTypes) {
            if (isCollection(item)) {
                return true;
            }
        }
        return false;
    }
}
