package cn.magicwindow.uav.sdk.util;


import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author Tony Shen
 * @date 15/11/12
 */
public class Preconditions {

    /**
     * 可以判断任何一个对象是否为空,包括List Map String 复杂对象等等,
     * 即使对象是null也是安全的
     *
     * @param t   泛型参数
     * @param <T> 泛型参数
     * @return 如果对象为Null或者对象长度为0则为空
     */
    public static <T> boolean isBlank(T t) {

        if (t == null) {
            return true;
        }
        //String判断的最多。放最前面
        if (t instanceof String) {
            return ((String) t).length() == 0;
        } else if (t instanceof List) {
            if (((List) t).size() == 0) {
                return true;
            }
        } else if (t instanceof Map) {
            if (((Map) t).size() == 0) {
                return true;
            }
        } else if (t instanceof JSONObject) {
            if (((JSONObject) t).length() == 0) {
                return true;
            }
        } else if (t instanceof Object[]) {
            if (((Object[]) t).length == 0) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean isNotBlank(T t) {
        return !isBlank(t);
    }

    public static <T> boolean isNotBlanks(Object... objects) {

        if (objects == null) {
            return false;
        }

        for (Object obj : objects) {
            if (isBlank(obj)) {
                return false;
            }
        }

        return true;
    }

    public static void checkNotNull(Object o, String message) {
        if (o == null) {
            throw new NullPointerException(message);
        }
    }

    public static boolean isJsonString(String t) {
        return isNotBlank(t) && t.startsWith("{");
    }
}
