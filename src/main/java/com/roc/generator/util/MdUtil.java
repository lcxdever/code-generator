package com.roc.generator.util;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.util.PsiUtil;
import com.roc.generator.javadoc.model.ControllerMethodMd;
import com.roc.generator.javadoc.model.FieldMd;
import com.roc.generator.model.AnnotationInfo;
import com.roc.generator.model.TypeInfo;
import com.roc.generator.model.MethodInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * markdown 工具类
 *
 * @author 鱼蛮 on 2022/2/21
 **/
public class MdUtil {

    public static final String BODY = "BODY";

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
     * 获取方法参数 MD 文档，由于是方法参数，只能从注释中获取字段描述信息
     * 如果是 Controller 方法，需要考虑实际参数名
     *
     * @param methodInfo methodInfo
     * @return {@link List<FieldMd>}
     */
    public static List<FieldMd> getMethodParamMd(MethodInfo methodInfo) {
        // 判断是否 controller 类
        boolean isController = isController(PsiUtil.getTopLevelClass(methodInfo.getPsiMethod()));
        // 解析文档中对参数的描述
        Map<String, String> paramDocMap = PsiTool.getCommentParamDocMap(methodInfo.getPsiMethod().getDocComment());
        // 解析参数信息，组装成 MD
        List<FieldMd> fields = Lists.newArrayList();
        for (PsiParameter parameter : methodInfo.getParameters()) {
            // 过滤掉 controller 中的无效参数
            if (isController && !isValidControllerParma(parameter)) {
                continue;
            }
            FieldMd fieldMd = new FieldMd();
            // RequestBody 的参数没有字段名
            fieldMd.setFieldName(parameter.hasAnnotation(ControllerMethodMd.REQUEST_BODY) ? "" : parameter.getName());
            // controller RequestParam 参数特殊处理
            if (isController) {
                Optional.ofNullable(getControllerParamName(parameter)).ifPresent(fieldMd::setFieldName);
            }
            fieldMd.setFieldType(MdUtil.spChartReplace(TypeInfo.fromPsiType(parameter.getType()).getNameGenericsSimple()));
            fieldMd.setCanNull(MdAnnotationUtil.notNull(parameter) ? "N" : "Y");

            String paramDoc = Optional.ofNullable(paramDocMap.get(parameter.getName())).orElse("");
            fieldMd.setDescribe(MdUtil.spChartReplace(MdAnnotationUtil.getDescribeWithAnnotation(parameter, paramDoc)));
            fields.add(fieldMd);
        }
        return fields;
    }

    /**
     * 获取 spring controller 参数的真实请求值
     *
     * @param parameter parameter
     * @return {@link String}
     */
    public static String getControllerParamName(PsiParameter parameter) {
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
     * PsiClass 是否 Controller
     *
     * @param psiClass psiClass
     * @return {@link boolean}
     */
    public static boolean isController(PsiClass psiClass) {
        Objects.requireNonNull(psiClass);
        return psiClass.hasAnnotation(ControllerMethodMd.CONTROLLER)
                || psiClass.hasAnnotation(ControllerMethodMd.REST_CONTROLLER);
    }

    /**
     * 判断是否 Controller 的有效参数
     * @param psiParameter psiParameter
     * @return {@link boolean}
     */
    public static boolean isValidControllerParma(PsiParameter psiParameter) {
        return psiParameter.hasAnnotation(ControllerMethodMd.REQUEST_BODY)
                || psiParameter.hasAnnotation(ControllerMethodMd.REQUEST_PARAM)
                || psiParameter.hasAnnotation(ControllerMethodMd.PATH_VARIABLE);
    }

    /**
     * 获取请求类型
     *
     * @param methodInfo methodInfo
     * @return {@link String}
     */
    public static String getRequestType(MethodInfo methodInfo) {
        for (PsiParameter parameter : methodInfo.getParameters()) {
            if (Objects.nonNull(parameter.getAnnotation(ControllerMethodMd.REQUEST_BODY))) {
                return BODY;
            }
        }
        return "";
    }

    /**
     * 描述中需要忽略的类型
     *
     * @param typeInfo 类型信息
     * @return {@link boolean}
     */
    public static boolean ignoreType(TypeInfo typeInfo) {
        return TypeUtil.isJavaBaseType(typeInfo.getNameCanonical()) || TypeUtil.isCollection(typeInfo);
    }
}
