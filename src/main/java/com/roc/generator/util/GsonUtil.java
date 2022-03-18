package com.roc.generator.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.util.Date;

/**
 * @author 鱼蛮 on 2022/3/12
 **/
public class GsonUtil {

    public static final Gson PRETTY_GSON;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, (JsonSerializer<Date>) (date, type, context) -> new JsonPrimitive(date.getTime()));
        gsonBuilder.setPrettyPrinting().serializeNulls();
        PRETTY_GSON = gsonBuilder.create();
    }

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
