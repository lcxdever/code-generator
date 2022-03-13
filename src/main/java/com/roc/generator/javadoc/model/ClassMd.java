package com.roc.generator.javadoc.model;

import com.roc.generator.model.TypeInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 类 markdown 描述
 *
 * @author 鱼蛮 on 2022/2/20
 **/
@Getter
@Setter
@ToString
public class ClassMd {

    /**
     * 类名
     */
    private String className;

    /**
     * 字段信息
     */
    private List<FieldMd> fields;

    /**
     * 从 ClassInfo 类创建
     *
     * @param typeInfo classInfo
     * @return {@link com.roc.generator.javadoc.model.ClassMd}
     */
    public static ClassMd fromClassInfo(TypeInfo typeInfo) {
        ClassMd classMd = new ClassMd();
        classMd.setClassName(typeInfo.getNameSimple());
        classMd.setFields(new ArrayList<>());
        return classMd;
    }
}
