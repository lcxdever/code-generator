package com.roc.generator.javadoc;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.roc.generator.model.AnnotationInfo;

import java.util.List;

/**
 * AnnotationInfo 格式化
 *
 * @author 鱼蛮 on 2022/2/19
 **/
public interface MdAnnotationFormatter {

    ExtensionPointName<MdAnnotationFormatter> EXTENSION_NAME = ExtensionPointName.create("com.roc.code-generator.mdAnnotationFormatter");

    /**
     * 将注解信息格式化
     *
     * @param annotationInfo annotationInfo
     * @return {@link java.lang.String}
     */
    String format(AnnotationInfo annotationInfo);

    /**
     * 支持的注解
     *
     * @return {@link List<String>}
     */
    List<String> support();
}
