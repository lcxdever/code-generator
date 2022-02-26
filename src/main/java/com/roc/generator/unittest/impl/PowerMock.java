package com.roc.generator.unittest.impl;

import com.roc.generator.unittest.TestFramework;
import com.roc.generator.model.ClassInfo;
import com.roc.generator.PluginIcons;

import javax.swing.*;

/**
 * PowerMock 测试框架
 *
 * @author 鱼蛮 on 2022/2/14
 **/
public class PowerMock implements TestFramework {

    @Override
    public String getName() {
        return "PowerMock";
    }

    @Override
    public Icon getIcon() {
        return PluginIcons.TEST_FRAMEWORK_POWERMOCK;
    }

    @Override
    public ClassInfo getRunnerClass() {
        return ClassInfo.fromClassNameText("org.powermock.modules.junit4.PowerMockRunner");
    }
}
