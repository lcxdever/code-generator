package com.roc.generator.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author 鱼蛮 on 2022/3/12
 **/
public class GsonUtil {

    public static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    /**
     * 输出格式化后的 JSON
     *
     * @param object object
     * @return {@link String}
     */
    public static String prettyJson(Object object) {
        return PRETTY_GSON.toJson(object);
    }
}
