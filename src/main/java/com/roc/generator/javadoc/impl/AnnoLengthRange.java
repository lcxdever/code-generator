package com.roc.generator.javadoc.impl;

import com.google.common.collect.Lists;
import com.roc.generator.javadoc.MdAnnotationFormatter;
import com.roc.generator.model.AnnotationInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * org.hibernate.validator.constraints.Length
 * org.hibernate.validator.constraints.Range
 * 格式化
 *
 * @author 鱼蛮 on 2022/2/19
 **/
public class AnnoLengthRange implements MdAnnotationFormatter {

    public static final String LENGTH = "org.hibernate.validator.constraints.Length";
    public static final String RANGE = "org.hibernate.validator.constraints.Range";
    public static final String MIN = "min";
    public static final String MAX = "max";

    @Override
    public String format(AnnotationInfo annotationInfo) {
        String min = annotationInfo.getAttributes().get(MIN);
        String max = annotationInfo.getAttributes().get(MAX);
        if (StringUtils.isBlank(min) || StringUtils.isBlank(max)) {
            return "";
        }
        String text = Objects.equals(LENGTH, annotationInfo.getClassInfo().getClassNameFull()) ? "长度" : "值";
        StringBuilder sb = new StringBuilder(text);
        if (Objects.equals(min, max)) {
            sb.append("为").append(min);
            return sb.toString();
        }
        if (StringUtils.isNoneBlank(min)) {
            sb.append("最小为").append(min);
        }
        if (StringUtils.isNoneBlank(max)) {
            sb.append("最大为").append(max);
        }
        return sb.toString();
    }

    @Override
    public List<String> support() {
        return Lists.newArrayList(LENGTH, RANGE);
    }
}
