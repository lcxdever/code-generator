package com.roc.generator.json;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.psi.PsiClass;
import com.intellij.util.ui.TextTransferable;
import com.roc.generator.util.GsonUtil;
import com.roc.generator.util.JavaJsonUtil;
import com.roc.generator.util.NotificationUtil;
import com.roc.generator.util.PsiTool;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author 鱼蛮 on 2022/3/13
 **/
public class GenerateJsonAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        PsiTool.setNormalClassVisible(e);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        PsiClass selectedClass = PsiTool.getSelectClass(e);
        if (Objects.isNull(selectedClass)) {
            return;
        }

        Object generated = JavaJsonUtil.genJsonFromPsiClass(selectedClass);

        // 将结果 copy 到粘贴板
        CopyPasteManager.getInstance().setContents(new TextTransferable(GsonUtil.prettyJson(generated)));

        // 发送消息通知
        NotificationUtil.notifyInfo(e.getProject(), "generator_json_notification", "JSON已经复制到黏贴板了,快去黏贴吧");

    }
}
