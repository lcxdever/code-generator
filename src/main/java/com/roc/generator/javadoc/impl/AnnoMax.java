package com.roc.generator.javadoc.impl;

import com.google.common.collect.Lists;
import com.roc.generator.javadoc.MdAnnotationFormatter;
import com.roc.generator.model.AnnotationInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * javax.validation.constraints.Max 格式化
 *
 * @author 鱼蛮 on 2022/2/19
 **/
public class AnnoMax implements MdAnnotationFormatter {

    public static final String MIN = "javax.validation.constraints.Max";
    public static final String VALUE = "value";

    @Override
    public String format(AnnotationInfo annotationInfo) {
        String max = annotationInfo.getAttributeValue(VALUE);
        if (StringUtils.isBlank(max)) {
            return "";
        }
        return "值最大为" + max;
    }

    @Override
    public List<String> support() {
        return Lists.newArrayList(MIN);
    }
}
