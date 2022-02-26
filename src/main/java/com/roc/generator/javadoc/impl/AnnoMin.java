package com.roc.generator.javadoc.impl;

import com.google.common.collect.Lists;
import com.roc.generator.javadoc.MdAnnotationFormatter;
import com.roc.generator.model.AnnotationInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * javax.validation.constraints.Min 格式化
 *
 * @author 鱼蛮 on 2022/2/19
 **/
public class AnnoMin implements MdAnnotationFormatter {

    public static final String MIN = "javax.validation.constraints.Min";
    public static final String VALUE = "value";

    @Override
    public String format(AnnotationInfo annotationInfo) {
        String min = annotationInfo.getAttributes().get(VALUE);
        if (StringUtils.isBlank(min)) {
            return "";
        }
        return "值最小为" + min;
    }

    @Override
    public List<String> support() {
        return Lists.newArrayList(MIN);
    }
}
