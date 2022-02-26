package com.roc.generator.model;

import com.intellij.psi.PsiType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Java 类信息封装
 *
 * @author 鱼蛮 on 2022/2/12
 **/
@Getter
@Setter
@ToString
public class ClassInfo {

    /**
     * 包名
     */
    @Nonnull
    private String packageName;

    /**
     * 类名
     */
    @Nonnull
    private String className;

    /**
     * 全类名
     */
    @Nonnull
    private String classNameFull;

    /**
     * 类名，包含泛型
     */
    @Nonnull
    private String classNameGenerics;

    /**
     * 全类名，包含泛型
     */
    @Nonnull
    private String classNameGenericsFull;

    /**
     * 根据全类名创建
     *
     * @param classNameGenericsFull classNameGenericsFull
     * @return {@link ClassInfo}
     */
    @Nonnull
    public static ClassInfo fromClassNameText(String classNameGenericsFull) {
        if (StringUtils.isBlank(classNameGenericsFull)) {
            throw new IllegalArgumentException("classNameText不可为空");
        }
        ClassInfo classInfo = new ClassInfo();
        classInfo.setClassNameGenericsFull(classNameGenericsFull);
        classInfo.setClassNameFull(StringUtils.substringBefore(classNameGenericsFull, "<"));
        classInfo.setPackageName(StringUtils.substringBeforeLast(classInfo.getClassNameFull(), "."));
        classInfo.setClassName(StringUtils.substringAfterLast(classInfo.getClassNameFull(), "."));
        classInfo.setClassNameGenerics(getClassNameGenerics(classNameGenericsFull));

        return classInfo;
    }

    /**
     * 根据 PsiType 创建
     *
     * @param psiType psiType
     * @return {@link ClassInfo}
     */
    public static ClassInfo fromClassPsiType(PsiType psiType) {
        return fromClassNameText(psiType.getCanonicalText());
    }

    /**
     * 返回本身及泛型包含的类
     *
     * @return {@link java.util.List<java.lang.String>}
     */
    public List<String> getClassList() {
        List<String> list = new ArrayList<>();
        int flag = 0;
        char[] chars = classNameGenericsFull.toCharArray();
        for (int i = 0, len = chars.length, end = len - 1; i < len; i++) {
            char c = chars[i];
            if (c == ',' || c == ' ' || c == '?') {
                flag = i + 1;
            }
            if (c == '<' || c == '>') {
                if (flag < i) {
                    list.add(new String(Arrays.copyOfRange(chars, flag, i)));
                }
                flag = i + 1;
            }
            if (i == end) {
                if (flag < i + 1) {
                    list.add(new String(Arrays.copyOfRange(chars, flag, i + 1)));
                }
                break;
            }
        }
        return list;
    }

    public static String getClassNameGenerics(String classNameGenericsFull) {
        StringBuilder sb = new StringBuilder();
        int flag = 0;
        char[] chars = classNameGenericsFull.toCharArray();
        for (int i = 0, len = chars.length, end = len - 1; i < len; i++) {
            char c = chars[i];
            if (c == '.') {
                flag = i + 1;
            }
            if (flag != -1) {
                if (c == '<' || c == '>') {
                    sb.append(Arrays.copyOfRange(chars, flag, i));
                    flag = -1;
                } else if (i == end) {
                    sb.append(Arrays.copyOfRange(chars, flag, i + 1));
                    flag = -1;
                }
                // 处理没有包的类型
                if (c == '<') {
                    flag = i + 1;
                }
            }
            if (c == ',' || c == '<' || c == '>' || c == '?') {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
