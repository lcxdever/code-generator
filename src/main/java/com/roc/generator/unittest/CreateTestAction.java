package com.roc.generator.unittest;

import com.google.common.collect.Maps;
import com.intellij.codeInsight.navigation.NavigationUtil;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.ide.fileTemplates.impl.CustomFileTemplate;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiJavaFileImpl;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.util.classMembers.MemberInfo;
import com.intellij.util.ResourceUtil;
import com.roc.generator.model.FieldInfo;
import com.roc.generator.model.MethodInfo;
import com.roc.generator.model.TypeInfo;
import com.roc.generator.util.PsiTool;
import com.roc.generator.util.StringTool;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 创建 Test 类 Action
 *
 * @author 鱼蛮 Date 2022/2/14
 */
public class CreateTestAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        Presentation presentation = e.getPresentation();
        PsiClass psiClass = PsiTool.getSelectClass(e);
        if (Objects.isNull(psiClass)) {
            presentation.setEnabledAndVisible(false);
            return;
        }
        if (psiClass.isInterface() || psiClass.isAnnotationType()) {
            presentation.setEnabledAndVisible(false);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // 准备数据
        Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return;
        }
        if (!(psiFile instanceof PsiJavaFile)) {
            return;
        }
        PsiJavaFile psiJavaFile = (PsiJavaFile)psiFile;
        PsiDirectory srcDir = psiJavaFile.getContainingDirectory();
        PsiPackage srcPackage = JavaDirectoryService.getInstance().getPackage(srcDir);

        Editor editor = e.getDataContext().getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return;
        }

        // 是否只需要创建测试方法
        if (createTestMethodOnly(project, psiJavaFile, editor)) {
            return;
        }

        // 展示 Form 界面
        CreateTestDialog dialog = new CreateTestDialog(project, "Create Unit Test",
                psiJavaFile.getClasses()[0], srcPackage, ModuleUtilCore.findModuleForPsiElement(psiJavaFile));
        if (!dialog.showAndGet()) {
            return;
        }

        // 拼参数
        Map<String, Object> params = getParamMap(psiJavaFile, dialog);
        try {
            PsiClass psiClass = createUnitTestFile(dialog, params);
            // 跳转到类上
            NavigationUtil.activateFileWithPsiElement(psiClass, true);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 只创建测试方法
     *
     * @param project     project
     * @param psiJavaFile psiJavaFile
     * @param editor      editor
     * @return {@link boolean}
     */
    private boolean createTestMethodOnly(Project project, PsiJavaFile psiJavaFile, Editor editor) {
        PsiElement referenceAt = psiJavaFile.findElementAt(editor.getCaretModel().getOffset());
        PsiClass selectedClass = Objects.requireNonNull(PsiTreeUtil.getContextOfType(referenceAt, PsiClass.class));
        String testClassName = CreateTestDialog.suggestTestClassName(selectedClass);

        // 判断光标位置是否在 method 中
        PsiMethod selectedMethod = PsiTreeUtil.getContextOfType(referenceAt, PsiMethod.class);
        if (Objects.isNull(selectedMethod)) {
            return false;
        }
        // 判断测试文件夹是否存在
        PsiDirectory testDir = PsiTool.findTestPackage(psiJavaFile);
        if (Objects.isNull(testDir)) {
            return false;
        }
        // 判断是否已经生成过这个类的 UnitTest 文件
        PsiFile testFile = testDir.findFile(testClassName + ".java");
        if (Objects.isNull(testFile)) {
            return false;
        }
        // 获取 PsiClass
        PsiClass[] classes = ((PsiJavaFile)testFile).getClasses();
        if (classes.length == 0) {
            return false;
        }

        String testMethodName = "test" + StringTool.upperFirstChar(selectedMethod.getName());
        PsiClass testClass = classes[0];
        Optional<PsiMethod> existMethod = Arrays.stream(testClass.getMethods())
                .filter(item -> Objects.equals(item.getName(), testMethodName))
                .findAny();
        // 如果方法已经存在，直接跳转到方法上
        if (existMethod.isPresent()) {
            NavigationUtil.activateFileWithPsiElement(existMethod.get());
        } else {
            PsiElementFactory factory = PsiElementFactory.getInstance(project);
            // 创建一个 test Method 并写入测试类
            PsiMethod method = factory.createMethod(testMethodName, PsiType.VOID);
            PsiAnnotation testAnno = factory.createAnnotationFromText("@Test", testClass);
            method.addBefore(testAnno, method.getFirstChild());
            WriteCommandAction.writeCommandAction(project).run(() -> testClass.addBefore(method, testClass.getLastChild()));
            // 写入的 Method 好像并不是传入的，需要重新获取下才可以 navigation 到
            existMethod = Arrays.stream(testClass.getMethods())
                    .filter(item -> Objects.equals(item.getName(), testMethodName))
                    .findAny();
            if (existMethod.isPresent()) {
                NavigationUtil.activateFileWithPsiElement(existMethod.get());
            } else {
                NavigationUtil.activateFileWithPsiElement(testClass);
            }
        }
        return true;
    }

    /**
     * 创建单元测试文件
     *
     * @param params 参数个数
     * @throws Exception 异常
     */
    private PsiClass createUnitTestFile(CreateTestDialog dialog, Map<String, Object> params) throws Exception {
        PsiDirectory directory = dialog.getTargetDirectory();
        String className = dialog.getClassName();
        String text = ResourceUtil.loadText(ResourceUtil.getResource(this.getClass().getClassLoader(), "template", "unit-test.vm"));

        CustomFileTemplate template = new CustomFileTemplate("java", "java");
        template.setText(text);
        VirtualFile exists = directory.getVirtualFile().findChild(className + ".java");
        if (Objects.nonNull(exists)) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    exists.delete(this);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
        }
        return (PsiClass)FileTemplateUtil.createFromTemplate(template, className, params, directory, null);
    }

    /**
     * 得到参数
     *
     * @param psiJavaFile psi java文件
     * @param dialog 对话框
     * @return 参数
     */
    private Map<String, Object> getParamMap(PsiJavaFile psiJavaFile, CreateTestDialog dialog) {
        Set<String> imports = new HashSet<>();
        // 注入属性
        PsiField[] allFields = psiJavaFile.getClasses()[0].getAllFields();
        List<FieldInfo> fields = Lists.newArrayList();
        for (PsiField field : allFields) {
            if (field.getAnnotation("javax.annotation.Resource") != null
                    || field.getAnnotation("org.springframework.beans.factory.annotation.Autowired") != null) {
                FieldInfo templateField = new FieldInfo();
                templateField.setFieldName(field.getName());
                templateField.setComment(Optional.ofNullable(field.getDocComment()).map(PsiElement::getText).orElse(""));
                TypeInfo typeInfo = TypeInfo.fromPsiType(field.getType());
                templateField.setTypeInfo(typeInfo);
                fields.add(templateField);
                imports.addAll(typeInfo.getClassList());
            }
        }

        // 注入方法
        Collection<MemberInfo> selectedMemberList = dialog.getSelectedMethods();
        List<PsiMethod> methodList = selectedMemberList.stream()
                .map(memberInfo -> (PsiMethod)memberInfo.getMember()).collect(Collectors.toList());
        List<MethodInfo> methods = Lists.newArrayList();
        for (PsiMethod psiMethod : methodList) {
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMethodName(psiMethod.getName());
            methodInfo.setMethodNameUp(StringTool.upperFirstChar(psiMethod.getName()));
            methods.add(methodInfo);
        }

        String testClassName = dialog.getClassName();
        String className = dialog.getTargetClass().getName();
        assert Objects.nonNull(className);
        if (StringUtils.isBlank(className)) {
            testClassName = className + "UnitTest";
        }

        TypeInfo superClass = null;
        if (StringUtils.isNoneBlank(dialog.getSuperClassName())) {
            superClass = TypeInfo.fromNameGenericsCanonical(dialog.getSuperClassName());
            imports.addAll(superClass.getClassList());
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put("user", System.getProperty("user.name"));
        params.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")));
        params.put("testFramework", dialog.getSelectedTestFramework());
        params.put("testClassName", testClassName);
        params.put("hasSuperClass", StringUtils.isNoneBlank(dialog.getSuperClassName()));
        params.put("superClass", superClass);
        params.put("className", className);
        params.put("package", ((PsiJavaFileImpl)dialog.getTargetClass().getParent()).getPackageName());
        params.put("instanceName", StringUtils.substring(className, 0, 1).toLowerCase() + StringUtils.substring(className, 1));
        params.put("fieldList", fields);
        params.put("methodList", methods);
        params.put("hasBefore", dialog.shouldGeneratedBefore());
        params.put("hasAfter", dialog.shouldGeneratedAfter());
        params.put("imports", imports);
        return params;
    }
}

