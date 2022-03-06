package com.roc.generator.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.roc.generator.javadoc.model.ControllerMethodMd;
import com.roc.generator.javadoc.model.FieldMd;
import com.roc.generator.model.AnnotationInfo;
import com.roc.generator.model.ClassInfo;
import com.roc.generator.model.MethodInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 鱼蛮 on 2022/2/21
 **/
public class MdUtil {

    /**
     * 获取格式化后的注释
     *
     * @param original original
     * @return {@link java.lang.String}
     */
    public static String commentFormat(String original) {
        if (StringUtils.isBlank(original)) {
            return "";
        }
        return Arrays.stream(original.split("\n")).map(e -> {
            int index = 0;
            for (int i = 0; i < e.length(); i++) {
                if (e.charAt(i) != ' ') {
                    index = i;
                    break;
                }
            }
            String newLine = StringUtils.substring(e, index, e.length());
            if (newLine.startsWith("*")) {
                newLine = " " + newLine;
            }
            return newLine;
        }).collect(Collectors.joining("\n"));
    }

    /**
     * 特殊字符替换
     *
     * @param str str
     * @return {@link java.lang.String}
     */
    public static String spChartReplace(String str) {
        return StringUtils.replace(StringUtils.replace(str, "<", "&lt;"), ">", "&gt;");
    }

    /**
     * 值获取文档的说明部分
     *
     * @param doc doc
     * @return {@link String}
     */
    public static String getCommentSimple(PsiDocComment doc) {
        if (Objects.isNull(doc)) {
            return StringUtils.EMPTY;
        }
        // 获取方法描述
        for (PsiElement element : doc.getDescriptionElements()) {
            if (element instanceof PsiWhiteSpace) {
                continue;
            }
            if (StringUtils.isNoneBlank(element.getText())) {
                return element.getText();
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * 获取方法的参数 MD 文档
     *
     * @param methodInfo methodInfo
     * @param isController 是否 controller
     * @return {@link List<FieldMd>}
     */
    public static List<FieldMd> getMethodParamMd(MethodInfo methodInfo, boolean isController) {
        // RequestBody 型请求参数，不需要显示 RequestBody 参数名
        if (isController && isRequestBody(methodInfo)) {
            return Lists.newArrayList();
        }

        Map<String, String> paramDocMap = Maps.newHashMap();
        PsiDocComment doc = methodInfo.getPsiDocComment();
        if (Objects.nonNull(doc)) {
            // 获取参数映射，用户在 MD 中展示描述
            for (PsiDocTag tag : doc.findTagsByName("param")) {
                PsiElement[] elements = tag.getDataElements();
                if (elements.length > 1) {
                    paramDocMap.put(elements[0].getText(), elements[1].getText());
                }
            }
        }
        // 解析参数信息，组装成 MD
        PsiParameterList parameterList = methodInfo.getParameterList();
        List<FieldMd> fields = Lists.newArrayListWithCapacity(parameterList.getParametersCount());
        for (int i = 0; i < parameterList.getParametersCount(); i++) {
            PsiParameter parameter = parameterList.getParameter(i);
            if (Objects.isNull(parameter)) {
                continue;
            }
            // 过滤掉 controller 里面的非前端传递参数
            if (isController && !(isValidControllerParameter(parameter))) {
                continue;
            }
            FieldMd fieldMd = new FieldMd();
            fieldMd.setFieldName(parameter.getName());
            // controller RequestParam 参数特殊处理
            if (isController) {
                Optional.ofNullable(getSpringParamName(parameter)).ifPresent(fieldMd::setFieldName);
            }
            fieldMd.setFieldType(MdUtil.spChartReplace(ClassInfo.fromClassPsiType(parameter.getType()).getClassNameGenerics()));
            fieldMd.setCanNull(MdAnnotationUtil.notNull(parameter) ? "否" : "是");

            String paramDoc = Optional.ofNullable(paramDocMap.get(parameter.getName())).orElse("");
            fieldMd.setDescribe(MdAnnotationUtil.getDescribeWithAnnotation(parameter, paramDoc));
            fieldMd.setDescribe(MdUtil.spChartReplace(fieldMd.getDescribe()));
            fields.add(fieldMd);
        }
        return fields;
    }

    /**
     * 获取 spring 参数的真实请求值
     *
     * @param parameter parameter
     * @return {@link String}
     */
    public static String getSpringParamName(PsiParameter parameter) {
        PsiAnnotation requestParam = parameter.getAnnotation(ControllerMethodMd.REQUEST_PARAM);
        if (Objects.isNull(requestParam)) {
            requestParam = parameter.getAnnotation(ControllerMethodMd.PATH_VARIABLE);
        }
        if (Objects.nonNull(requestParam)) {
            String value = AnnotationInfo.fromPsiAnnotation(requestParam).getAttributeValue("value");
            if (StringUtils.isNoneBlank(value)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 是否 spring controller 有效参数
     *
     * @param parameter parameter
     * @return {@link boolean}
     */
    public static boolean isValidControllerParameter(PsiParameter parameter) {
        return parameter.hasAnnotation(ControllerMethodMd.REQUEST_BODY)
                || parameter.hasAnnotation(ControllerMethodMd.REQUEST_PARAM)
                || parameter.hasAnnotation(ControllerMethodMd.PATH_VARIABLE);
    }

    /**
     * 判断是否 RequestBody 方法
     * @param methodInfo methodInfo
     * @return {@link boolean}
     */
    public static boolean isRequestBody(MethodInfo methodInfo) {
        PsiParameterList parameterList = methodInfo.getParameterList();
        for (int i = 0; i < parameterList.getParametersCount(); i++) {
            PsiParameter parameter = parameterList.getParameter(i);
            if (Objects.isNull(parameter)) {
                continue;
            }
            if (Objects.nonNull(parameter.getAnnotation(ControllerMethodMd.REQUEST_BODY))) {
                return true;
            }
        }
        return false;
    }
}
