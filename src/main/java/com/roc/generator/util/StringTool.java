package com.roc.generator.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * String 工具类
 *
 * @author 鱼蛮 on 2022/4/3
 **/
public class StringTool {

    /**
     * 判断字符串是否全部都是 *
     *
     * @param str 字符串
     * @return {@link boolean}
     */
    public static boolean isAllStar(String str) {
        for (int i = 0, len = str.length(); i < len; i++) {
            if (str.charAt(i) != '*') {
                return false;
            }
        }
        return true;
    }

    /**
     * 转换驼峰字符串为下划线形式
     *
     * @param str 字符串
     * @return {@link String}
     */
    public static String camelToUnderline(String str) {
        return Arrays.stream(StringUtils.splitByCharacterTypeCamelCase(str)).map(String::toUpperCase).collect(Collectors.joining("_"));
    }

}
