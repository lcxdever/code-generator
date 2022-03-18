package com.roc.generator.model;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Java 类型信息封装
 *
 * @author 鱼蛮 on 2022/2/12
 **/
@Getter
@Setter
@ToString
public class TypeInfo {

    /**
     * 包名
     */
    @Nonnull
    private String packageName;

    /**
     * 类名
     */
    @Nonnull
    private String nameSimple;

    /**
     * 全类名Canonical
     */
    @Nonnull
    private String nameCanonical;

    /**
     * 类名，包含泛型
     */
    @Nonnull
    private String nameGenericsSimple;

    /**
     * 全类名，包含泛型
     */
    @Nonnull
    private String nameGenericsCanonical;

    /**
     * PsiType
     */
    @Nullable
    private PsiType psiType;

    /**
     * 根据全类名创建
     *
     * @param nameGenericsCanonical nameGenericsCanonical
     * @return {@link TypeInfo}
     */
    @Nonnull
    public static TypeInfo fromNameGenericsCanonical(String nameGenericsCanonical) {
        TypeInfo typeInfo = fromName(nameGenericsCanonical);
        typeInfo.setNameGenericsSimple(getNameGenericsSimple(nameGenericsCanonical));
        return typeInfo;
    }

    /**
     * 根据 PsiType 创建
     *
     * @param psiType psiType
     * @return {@link TypeInfo}
     */
    public static TypeInfo fromPsiType(PsiType psiType) {
        TypeInfo typeInfo = fromName(psiType.getCanonicalText());
        typeInfo.setNameGenericsSimple(psiType.getPresentableText());
        typeInfo.setPsiType(psiType);
        return typeInfo;
    }

    /**
     * 根据 PsiType 创建
     *
     * @param psiClass psiClass
     * @return {@link TypeInfo}
     */
    public static TypeInfo fromPsiClass(PsiClass psiClass) {
        TypeInfo typeInfo = fromName(psiClass.getQualifiedName());
        typeInfo.setNameGenericsSimple(Objects.requireNonNull(psiClass.getName()));
        return typeInfo;
    }

    private static TypeInfo fromName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("classNameText不可为空");
        }
        TypeInfo typeInfo = new TypeInfo();
        typeInfo.setNameGenericsCanonical(name);
        typeInfo.setNameCanonical(StringUtils.substringBefore(name, "<"));
        typeInfo.setNameCanonical(StringUtils.substringBefore(typeInfo.getNameCanonical(), "["));
        typeInfo.setPackageName(StringUtils.substringBeforeLast(typeInfo.getNameCanonical(), "."));
        typeInfo.setNameSimple(StringUtils.substringAfterLast(typeInfo.getNameCanonical(), "."));
        return typeInfo;
    }

    /**
     * 返回本身及泛型包含的类
     *
     * @return {@link java.util.List<java.lang.String>}
     */
    public List<String> getClassList() {
        List<String> list = new ArrayList<>();
        int flag = 0;
        char[] chars = nameGenericsCanonical.toCharArray();
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

    private static String getNameGenericsSimple(String classNameGenericsFull) {
        StringBuilder sb = new StringBuilder();
        int flag = 0;
        char[] chars = classNameGenericsFull.toCharArray();
        for (int i = 0, len = chars.length, end = len - 1; i < len; i++) {
            char c = chars[i];
            if (c == '.') {
                flag = i + 1;
            }
            if (flag != -1) {
                if (c == '<' || c == '>' || c == ',') {
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
