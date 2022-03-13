package com.roc.generator.javadoc.impl;

import com.roc.generator.javadoc.MdAnnotationFormatter;
import com.roc.generator.model.AnnotationInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 范围型注解格式化基类
 *
 * @author 鱼蛮 on 2022/3/12
 **/
public abstract class AbstractRangeAnno implements MdAnnotationFormatter {

    public static final String MIN = "min";
    public static final String MAX = "max";

    /**
     * 获取描述的类型
     *
     * @param annotationInfo annotationInfo
     * @return {@link String}
     */
    protected abstract String getDescribeType(AnnotationInfo annotationInfo);

    @Override
    public String format(AnnotationInfo annotationInfo) {
        String min = annotationInfo.getAttributeValue(MIN);
        String max = annotationInfo.getAttributeValue(MAX);
        if (StringUtils.isBlank(min) && StringUtils.isBlank(max)) {
            return "";
        }
        StringBuilder sb = new StringBuilder(getDescribeType(annotationInfo));
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
}
