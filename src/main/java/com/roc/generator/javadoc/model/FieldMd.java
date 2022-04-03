package com.roc.generator.javadoc.model;

import com.roc.generator.model.FieldInfo;
import com.roc.generator.util.MdAnnotationUtil;
import com.roc.generator.util.MdUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 字段 markdown 描述
 *
 * @author 鱼蛮 on 2022/2/19
 **/
@Getter
@Setter
@Data
public class FieldMd {

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 是否可空
     */
    private String canNull;

    /**
     * 描述/说明
     */
    private String describe;

    /**
     * 从 FieldInfo 中创建
     *
     * @param fieldInfo fieldInfo
     * @return {@link com.roc.generator.javadoc.model.FieldMd}
     */
    public static FieldMd fromFieldInfo(FieldInfo fieldInfo) {
        FieldMd fieldMd = new FieldMd();
        fieldMd.setFieldName(MdUtil.spChartReplace(fieldInfo.getFieldName()));
        fieldMd.setFieldType(MdUtil.spChartReplace(fieldInfo.getTypeInfo().getNameGenericsSimple()));
        fieldMd.setCanNull(MdAnnotationUtil.notNull(fieldInfo) ? "N" : "Y");
        fieldMd.setDescribe(MdAnnotationUtil.getDescribeWithAnnotation(fieldInfo));
        fieldMd.setDescribe(MdUtil.spChartReplace(fieldMd.getDescribe()));
        return fieldMd;
    }

    public void setFieldTypeWithReplace(String fieldType) {
        this.fieldType = MdUtil.spChartReplace(fieldType);
    }
}
