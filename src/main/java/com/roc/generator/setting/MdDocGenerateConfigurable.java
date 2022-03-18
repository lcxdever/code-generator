package com.roc.generator.setting;

import com.intellij.openapi.options.Configurable;
import com.intellij.util.ui.ListTableModel;
import org.apache.commons.collections.MapUtils;
import org.jetbrains.annotations.Nls;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * markdown 文档配置 Configurable
 *
 * @author 鱼蛮 Date 2022/3/10
 */
public class MdDocGenerateConfigurable implements Configurable {

    private MdDocToolWindow toolWindow;

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "Markdown Doc";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return toolWindow.getContainer();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        toolWindow = new MdDocToolWindow();
        PluginSettingState settings = PluginSettingState.getInstance();
        // 持久值为空，直接返回 window
        if (MapUtils.isEmpty(settings.getFieldValueMap())) {
            return toolWindow.getContainer();
        }
        // 持久值不为空，读取并填充表格行后再展示
        ListTableModel<FieldValueRow> tableModel = toolWindow.getBrowsersEditor().getModel();
        List<FieldValueRow> rows = settings.getFieldValueMap().entrySet().stream()
                .map(e -> new FieldValueRow(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        tableModel.addRows(rows);
        return toolWindow.getContainer();
    }

    @Override
    public boolean isModified() {
        PluginSettingState settings = PluginSettingState.getInstance();
        Map<String, String> fieldValueMap = settings.getFieldValueMap();
        ListTableModel<FieldValueRow> tableModel = toolWindow.getBrowsersEditor().getModel();
        // 行不一样时候肯定修改
        if (fieldValueMap.size() != tableModel.getRowCount()) {
            return true;
        }
        for (FieldValueRow row : tableModel.getItems()) {
            // 持久值不包含的 key 肯定修改，有可能修改值为 null
            if (!fieldValueMap.containsKey(row.getFieldName())) {
                return true;
            }
            // key 相同，值不同的为修改
            if (!Objects.equals(fieldValueMap.get(row.getFieldName()), row.getDefaultValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply() {
        // 读取表格中的所有行，持久到 state
        PluginSettingState settings = PluginSettingState.getInstance();
        ListTableModel<FieldValueRow> tableModel = toolWindow.getBrowsersEditor().getModel();
        Map<String, String> map = new HashMap<>(tableModel.getRowCount());
        for (FieldValueRow row : tableModel.getItems()) {
            map.put(row.getFieldName(), row.getDefaultValue());
        }
        settings.setFieldValueMap(map);
    }

    @Override
    public void reset() {}

    @Override
    public void disposeUIResources() {
        toolWindow = null;
    }

}