package com.roc.generator.javadoc.impl;

import com.google.common.collect.Lists;
import com.roc.generator.model.AnnotationInfo;

import java.util.List;

/**
 * org.hibernate.validator.constraints.Length
 * 格式化
 *
 * @author 鱼蛮 on 2022/2/19
 **/
public class AnnoLength extends AbstractRangeAnno {

    public static final String LENGTH = "org.hibernate.validator.constraints.Length";

    @Override
    protected String getDescribeType(AnnotationInfo annotationInfo) {
        return "长度";
    }

    @Override
    public List<String> support() {
        return Lists.newArrayList(LENGTH);
    }

}
