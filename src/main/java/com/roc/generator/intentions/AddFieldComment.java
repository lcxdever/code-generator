package com.roc.generator.intentions;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.util.IncorrectOperationException;
import com.roc.generator.util.PsiTool;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * 添加字段的注释
 *
 * @author 鱼蛮 on 2022/4/3
 **/
public class AddFieldComment extends PsiElementBaseIntentionAction implements IntentionAction {

    @NotNull
    @Override
    public String getText() {
        return "Add comment to the field";
    }

    @Override
    public @NotNull @Nls(capitalization = Capitalization.Sentence) String getFamilyName() {
        return "Add field comment";
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        PsiElementFactory factory = PsiElementFactory.getInstance(project);
        PsiField field = Objects.requireNonNull(PsiTreeUtil.getContextOfType(element, PsiField.class));

        PsiClass psiClass = PsiUtil.resolveClassInType(field.getType());
        if (Objects.isNull(psiClass)) {
            return;
        }
        String[] commentSimple = PsiTool.getCommentSimple(psiClass.getDocComment());
        PsiDocComment psiDocComment = factory.createDocCommentFromText("/** " + String.join(" ", commentSimple) + " */");
        field.addBefore(psiDocComment, field.getFirstChild());
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (!(element instanceof PsiJavaToken)) {
            return false;
        }
        final PsiJavaToken token = (PsiJavaToken) element;
        if (token.getTokenType() != JavaTokenType.IDENTIFIER) {
            return false;
        }
        PsiField field = PsiTreeUtil.getContextOfType(element, PsiField.class);
        if (Objects.isNull(field)) {
            return false;
        }
        return Objects.isNull(field.getDocComment());
    }

}
