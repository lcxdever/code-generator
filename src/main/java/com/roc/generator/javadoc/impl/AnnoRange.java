package com.roc.generator.javadoc.impl;

import com.google.common.collect.Lists;
import com.roc.generator.model.AnnotationInfo;

import java.util.List;

/**
 * org.hibernate.validator.constraints.Range
 * 格式化
 *
 * @author 鱼蛮 on 2022/2/19
 **/
public class AnnoRange extends AbstractRangeAnno {

    public static final String RANGE = "org.hibernate.validator.constraints.Range";

    @Override
    protected String getDescribeType(AnnotationInfo annotationInfo) {
        return "值";
    }

    @Override
    public List<String> support() {
        return Lists.newArrayList(RANGE);
    }

}
