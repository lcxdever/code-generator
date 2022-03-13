package com.roc.generator.setting;

import com.intellij.util.Function;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.table.TableModelEditor;
import com.intellij.util.ui.table.TableModelEditor.DialogItemEditor;
import com.intellij.util.ui.table.TableModelEditor.EditableColumnInfo;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

/**
 * markdown 生成工具，配置窗口布局
 *
 * @author 鱼蛮 on 2022/3/12
 **/
public class MdDocToolWindow {
    @Getter
    private JPanel container;
    private JPanel markdownConfTablePanel;

    @Getter
    private final TableModelEditor<FieldValueRow> browsersEditor;

    private static final EditableColumnInfo<FieldValueRow, String> FIELD_NAME_COLUMN = new EditableColumnInfo<>("FIELD_NAME") {
        @Override
        public String valueOf(FieldValueRow item) {
            return item.getFieldName();
        }

        @Override
        public void setValue(FieldValueRow item, String value) {
            item.setFieldName(value);
        }
    };

    private static final EditableColumnInfo<FieldValueRow, String> DEFAULT_VALUE_COLUMN = new EditableColumnInfo<>("DEFAULT_VALUE") {
        @Override
        public String valueOf(FieldValueRow item) {
            return item.getDefaultValue();
        }

        @Override
        public void setValue(FieldValueRow item, String value) {
            item.setDefaultValue(value);
        }
    };

    private static final ColumnInfo[] COLUMNS = {FIELD_NAME_COLUMN, DEFAULT_VALUE_COLUMN};

    public MdDocToolWindow() {
        DialogItemEditor<FieldValueRow> itemEditor = new DialogItemEditor<>() {
            @NotNull
            @Override
            public Class<FieldValueRow> getItemClass() {
                return FieldValueRow.class;
            }

            @Override
            public FieldValueRow clone(@NotNull FieldValueRow item, boolean forInPlaceEditing) {
                return new FieldValueRow(item.getFieldName(), item.getDefaultValue());
            }

            @Override
            public void edit(@NotNull FieldValueRow browser, @NotNull Function<FieldValueRow, FieldValueRow> mutator, boolean isAdd) {
            }

            @Override
            public void applyEdited(@NotNull FieldValueRow oldItem, @NotNull FieldValueRow newItem) {
                oldItem.apply(newItem);
            }

            @Override
            public boolean isEditable(@NotNull FieldValueRow browser) {
                return false;
            }
        };

        browsersEditor = new TableModelEditor<>(COLUMNS, itemEditor, "No panel configured");
        markdownConfTablePanel.add(browsersEditor.createComponent(), BorderLayout.CENTER);
    }
}
