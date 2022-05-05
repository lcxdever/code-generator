package com.roc.generator.util;

import com.google.common.collect.Maps;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.PackageWrapper;
import com.intellij.refactoring.util.RefactoringUtil;
import com.roc.generator.model.TypeInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import javax.annotation.Nullable;
import java.util.Arrays;
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
    public static String[] getCommentSimple(PsiDocComment doc) {
        if (Objects.isNull(doc)) {
            return new String[]{};
        }
        // 获取方法描述
        return Arrays.stream(doc.getDescriptionElements())
                .filter(e -> e instanceof PsiDocToken && ((PsiDocToken)e).getTokenType() == JavaDocTokenType.DOC_COMMENT_DATA)
                .map(PsiElement::getText)
                .map(String::trim)
                .filter(StringUtils::isNoneBlank)
                .filter(e -> !StringTool.isAllStar(e))
                .toArray(String[]::new);
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

    /**
     * 获取 java 文件所在模块的测试包文件夹
     *
     * @param psiJavaFile psiJavaFile
     * @return {@link PsiDirectory}
     */
    @Nullable
    public static PsiDirectory findTestPackage(PsiJavaFile psiJavaFile) {
        Module srcModule = ModuleUtilCore.findModuleForPsiElement(psiJavaFile);
        if (Objects.isNull(srcModule)) {
            return null;
        }
        PsiManager psiManager = PsiManager.getInstance(psiJavaFile.getProject());

       return ModuleRootManager.getInstance(srcModule).getSourceRoots(JavaSourceRootType.TEST_SOURCE)
               .stream()
               .map(e -> RefactoringUtil.findPackageDirectoryInSourceRoot(
                       new PackageWrapper(psiManager, psiJavaFile.getPackageName()), e))
               .filter(Objects::nonNull)
               .findFirst()
               .orElse(null);
    }
}
