package com.roc.generator.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
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
}
