package org.rocket.order.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangxin
 * 1/24/21 4:32 PM
 */
@SuppressWarnings("SpellCheckingInspection")
public class FastJsonConvertUtil {

    private static final SerializerFeature[] FEATURES_WITH_NULL_VALUE = {SerializerFeature.WriteMapNullValue,
            SerializerFeature.WriteNullBooleanAsFalse,
            SerializerFeature.WriteNullListAsEmpty,
            SerializerFeature.WriteNullNumberAsZero,
            SerializerFeature.WriteNullStringAsEmpty};

    /**
     * <B>方法名称：</B>将JSON字符串转换为实体对象<BR>
     * <B>概要说明：</B>将JSON字符串转换为实体对象<BR>
     *
     * @param data  JSON字符串
     * @param clzss 转换对象
     * @return T
     */
    public static <T> T convertJson2Object(String data, Class<T> clzss) {
        try {
            return JSON.parseObject(data, clzss);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <B>方法名称：</B>将JSONObject对象转换为实体对象<BR>
     * <B>概要说明：</B>将JSONObject对象转换为实体对象<BR>
     *
     * @param data  JSONObject对象
     * @param clzss 转换对象
     * @return T
     */
    public static <T> T convertJson2Object(JSONObject data, Class<T> clzss) {
        try {
            return JSONObject.toJavaObject(data, clzss);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <B>方法名称：</B>将JSON字符串数组转为List集合对象<BR>
     * <B>概要说明：</B>将JSON字符串数组转为List集合对象<BR>
     *
     * @param data  JSON字符串数组
     * @param clzss 转换对象
     * @return List<T>集合对象
     */
    public static <T> List<T> convertJson2Array(String data, Class<T> clzss) {
        try {
            return JSON.parseArray(data, clzss);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <B>方法名称：</B>将List<JSONObject>转为List集合对象<BR>
     * <B>概要说明：</B>将List<JSONObject>转为List集合对象<BR>
     *
     * @param data  List<JSONObject>
     * @param clzss 转换对象
     * @return List<T>集合对象
     */
    public static <T> List<T> convertJson2Array(List<JSONObject> data, Class<T> clzss) {
        try {
            List<T> t = new ArrayList<>();
            for (JSONObject jsonObject : data) {
                t.add(convertJson2Object(jsonObject, clzss));
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <B>方法名称：</B>将对象转为JSON字符串<BR>
     * <B>概要说明：</B>将对象转为JSON字符串<BR>
     *
     * @param obj 任意对象
     * @return JSON字符串
     */
    public static String convertObject2Json(Object obj) {
        try {
            return JSON.toJSONString(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <B>方法名称：</B>将对象转为JSONObject对象<BR>
     * <B>概要说明：</B>将对象转为JSONObject对象<BR>
     *
     * @param obj 任意对象
     * @return JSONObject对象
     */
    public static JSONObject convertObject2JsonObject(Object obj) {
        try {
            return (JSONObject) JSONObject.toJSON(obj);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * <B>方法名称：</B><BR>
     * <B>概要说明：</B><BR>
     *
     * @param obj obj
     * @return string
     */
    public static String convertObject2JsonWithNullValue(Object obj) {
        try {
            return JSON.toJSONString(obj, FEATURES_WITH_NULL_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
