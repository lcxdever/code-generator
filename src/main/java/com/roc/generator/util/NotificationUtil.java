package com.roc.generator.util;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

import javax.annotation.Nullable;

/**
 * @author 鱼蛮 on 2022/2/19
 **/
public class NotificationUtil {

    /**
     * 信息通知
     *
     * @param project project
     * @param group   group
     * @param content content
     */
    public static void notifyInfo(@Nullable Project project, String group, String content) {
        NotificationGroupManager.getInstance().getNotificationGroup(group)
                .createNotification(content, NotificationType.INFORMATION)
                .notify(project);
    }
}
