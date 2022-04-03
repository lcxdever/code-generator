package com.roc.generator.javadoc;

import com.google.common.collect.Maps;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ui.TextTransferable;
import com.roc.generator.javadoc.model.ClassMd;
import com.roc.generator.javadoc.model.ControllerMethodMd;
import com.roc.generator.javadoc.model.FieldMd;
import com.roc.generator.javadoc.model.MethodMd;
import com.roc.generator.model.FieldInfo;
import com.roc.generator.model.MethodInfo;
import com.roc.generator.util.*;
import com.roc.generator.util.FieldInfoUtil.StaticFilter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Md 格式文档生成
 *
 * @author 鱼蛮 Date 2022/2/18
 */
public class MdDocGenerateAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiClass psiClass = PsiTool.getSelectClass(e);
        e.getPresentation().setEnabledAndVisible(Objects.nonNull(psiClass));
    }

    @SneakyThrows
    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project == null) {
            return;
        }
        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }
        if (!(psiFile instanceof PsiJavaFile)) {
            return;
        }
        // 获取选择的类
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class);
        if (Objects.isNull(selectedClass)) {
            return;
        }
        String mdStr;
        // 选择的类如果是接口类，执行接口方法的生成
        if (selectedClass.isInterface()) {
            PsiMethod selectedMethod = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
            if (Objects.isNull(selectedMethod)) {
                return;
            }
            mdStr = getInterfaceMdTextFromPsiMethod(selectedMethod);
        }
        // 选择的是 Controller 接口
        else if(MdUtil.isController(selectedClass)) {
            PsiMethod selectedMethod = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
            if (Objects.isNull(selectedMethod)) {
                return;
            }
            mdStr = getControllerMdTextFromPsiMethod(selectedMethod);
        }
        // 选择的是普通类
        else {
            // 只生成类的字段
            mdStr = getMdTextFromFields(selectedClass);
        }

        // 将结果 copy 到粘贴板
        CopyPasteManager.getInstance().setContents(new TextTransferable(mdStr));

        // 发送消息通知
        NotificationUtil.notifyInfo(project, "md_doc_generator_notification", "文档已经复制到黏贴板了,快去黏贴吧");
    }

    /**
     * 获取 spring controller 类中方法的 markdown 文档
     *
     * @param selectedMethod selectedMethod
     * @return {@link String}
     */
    private String getControllerMdTextFromPsiMethod(PsiMethod selectedMethod) throws IOException, IllegalAccessException {
        MethodInfo methodInfo = MethodInfo.fromPsiMethod(selectedMethod);
        ControllerMethodMd controllerMethodMd = new ControllerMethodMd(methodInfo);
        return getMdTextFromMethodMd(controllerMethodMd, FileTemplateGroupFactory.MD_CONTROLLER_METHOD_DOC,  selectedMethod.getProject());
    }

    /**
     * 获取接口类方法的 markdown 文档
     *
     * @param selectedMethod selectedMethod
     * @return {@link String}
     */
    private String getInterfaceMdTextFromPsiMethod(PsiMethod selectedMethod) throws IOException, IllegalAccessException {
        MethodInfo methodInfo = MethodInfo.fromPsiMethod(selectedMethod);
        MethodMd methodMd = new MethodMd(methodInfo);
        return getMdTextFromMethodMd(methodMd, FileTemplateGroupFactory.MD_INTERFACE_METHOD_DOC, selectedMethod.getProject());
    }

    /**
     * 获取 class 文件的 markdown 文档
     *
     * @param psiClass psiClass
     * @return {@link String}
     */
    private String getMdTextFromFields(PsiClass psiClass) throws IOException, IllegalAccessException {
        List<FieldInfo> fields = FieldInfoUtil.getFieldInfoFromPsiClass(psiClass, new StaticFilter());
        List<FieldMd> fieldMds = fields.stream().map(FieldMd::fromFieldInfo).collect(Collectors.toList());

        ClassMd classMd = new ClassMd();
        classMd.setClassNameSimple(psiClass.getName());
        classMd.setFields(fieldMds);

        return getMdTextFromMethodMd(classMd, FileTemplateGroupFactory.MD_DOMAIN_DOC, psiClass.getProject());
    }

    private String getMdTextFromMethodMd(Object md, String templateName, Project project) throws IOException, IllegalAccessException {

        FileTemplate fileTemplate = FileTemplateManager.getInstance(project).getJ2eeTemplate(templateName);

        Map<String, Object> map = Maps.newHashMap();
        for (Field field : ReflectTool.getClassFields(md.getClass())) {
            field.setAccessible(true);
            map.put(StringTool.camelToUnderline(field.getName()), field.get(md));
        }
        return fileTemplate.getText(map);
    }

}