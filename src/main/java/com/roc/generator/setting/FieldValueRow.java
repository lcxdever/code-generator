package com.roc.generator.setting;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 表格行 字段/值
 *
 * @author 鱼蛮 Date 2022/3/12
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class FieldValueRow {

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 默认值
     */
    private String defaultValue;

    public FieldValueRow(String fieldName, String defaultValue) {
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
    }

    public void apply(FieldValueRow newItem) {
        this.fieldName = newItem.getFieldName();
        this.defaultValue = newItem.getDefaultValue();
    }
}