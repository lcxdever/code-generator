package com.roc.generator.unittest;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.roc.generator.model.TypeInfo;

import javax.swing.*;

/**
 * 测试框架接口
 *
 * @author 鱼蛮 Date 2022/2/12
 */
public interface TestFramework {

    ExtensionPointName<TestFramework> EXTENSION_NAME = ExtensionPointName.create("com.roc.code-generator.testFramework");

    /**
     * 测试框架名称
     *
     * @return {@link java.lang.String}
     */
    String getName();

    /**
     * 测试框架图标
     *
     * @return {@link javax.swing.Icon}
     */
    Icon getIcon();

    /**
     * 测试框架的  runnerClass
     *
     * @return {@link TypeInfo}
     */
    TypeInfo getRunnerClass();

}