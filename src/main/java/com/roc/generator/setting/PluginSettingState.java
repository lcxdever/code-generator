package com.roc.generator.setting;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 插件配置信息 State
 *
 * @author 鱼蛮 on 2022/3/12
 **/
@State(
        name = "com.roc.generator.setting.PluginSettingState",
        storages = @Storage("RocCodePluginSettings.xml")
)
public class PluginSettingState implements PersistentStateComponent<PluginSettingState> {

    /**
     * markdown 配置信息
     */
    @Getter
    @Setter
    private Map<String, String> fieldValueMap = new HashMap<>();

    public static PluginSettingState getInstance() {
        return ApplicationManager.getApplication().getService(PluginSettingState.class);
    }

    @Override
    public @Nullable PluginSettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull PluginSettingState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
