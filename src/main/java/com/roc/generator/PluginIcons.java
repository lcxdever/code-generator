package com.roc.generator;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

/**
 * icon 资源集合
 *
 * @author 鱼蛮 on 2022/2/17
 **/
public interface PluginIcons {

    /** mockito 测试框架图片 */
    Icon TEST_FRAMEWORK_MOCKITO = IconLoader.findIcon("/image/mockito.png", PluginIcons.class);
    /** powermock 测试框架图片 */
    Icon TEST_FRAMEWORK_POWERMOCK = IconLoader.findIcon("/image/powermock.png", PluginIcons.class);
    /** File and Code Template 处的 Icon */
    Icon FILE_TEMPLATE_ICON = IconLoader.findIcon("/image/file-template.svg", PluginIcons.class);
}
