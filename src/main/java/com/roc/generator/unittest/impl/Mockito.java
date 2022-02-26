package com.roc.generator.unittest.impl;

import com.roc.generator.model.ClassInfo;
import com.roc.generator.unittest.TestFramework;
import com.roc.generator.PluginIcons;

import javax.swing.*;

/**
 * Mockito 测试框架
 *
 * @author 鱼蛮 on 2022/2/14
 **/
public class Mockito implements TestFramework {

    @Override
    public String getName() {
        return "Mockito";
    }

    @Override
    public Icon getIcon() {
        return PluginIcons.TEST_FRAMEWORK_MOCKITO;
    }

    @Override
    public ClassInfo getRunnerClass() {
        return ClassInfo.fromClassNameText("org.mockito.junit.MockitoJUnitRunner");
    }
}
