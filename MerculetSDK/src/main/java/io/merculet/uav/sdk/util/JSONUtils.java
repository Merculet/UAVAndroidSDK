package io.merculet.uav.sdk.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.merculet.uav.sdk.reflect.Reflect;

/**
 * Created by Tony Shen on 15/10/8.
 */
public class JSONUtils {

    /**
     * 将JSONObject转换成对应的对象
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T convertToObj(JSONObject json, Class<T> clazz) throws Exception {
        // 首先得到clazz所定义的字段,包括父类中的字段
        Field[] fields = getAllField(clazz);
        // 根据传入的Class动态生成clazz对象
        T t = clazz.newInstance();

        String name = null;
        if (Preconditions.isNotBlank(fields)) {
            for (Field field : fields) {
                // 设置字段可访问（必须，否则报错）
                field.setAccessible(true);
                // 得到字段的属性名
                name = field.getName();

                if (field.getType() != null && Preconditions.isNotBlank(name)) {
                    // 根据字段的类型将值转化为相应的类型，并设置到生成的对象中。
                    if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
                        if (Preconditions.isNotBlank(json.optLong(name)))
                            field.set(t, json.optLong(name));
                    } else if (field.getType().equals(String.class)) {
                        if (Preconditions.isNotBlank(json.optString(name)))
                            field.set(t, json.optString(name));
                    } else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
                        if (Preconditions.isNotBlank(json.optDouble(name)))
                            field.set(t, json.optDouble(name));
                    } else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                        if (Preconditions.isNotBlank(json.optBoolean(name)))
                            field.set(t, json.optBoolean(name));
                    } else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
                        if (Preconditions.isNotBlank(json.optInt(name)))
                            field.set(t, json.optInt(name));
                    } else if (field.getType().equals(Date.class)) {
                        field.set(t, Date.parse(json.optString(name)));
                    } else if (field.getType().equals(Map.class)) {
                        if (json.has(name)) {
                            field.set(t, convertToMap(json.optJSONObject(name)));
                        }
                    } else if (field.getType().equals(List.class)) {

                        Type genericType = field.getGenericType();
                        Class fieldArgClass = null;
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType aType = (ParameterizedType) genericType;
                            Type[] fieldArgTypes = aType.getActualTypeArguments();

                            if (fieldArgTypes != null && fieldArgTypes.length > 0) {
                                // 获取List的泛型对象
                                fieldArgClass = (Class) fieldArgTypes[0];
                            }
                        }

                        if (fieldArgClass == null)
                            continue;

                        if (json.optJSONArray(name) != null) {
                            field.set(t, convertToList(json.optJSONArray(name), fieldArgClass));
                        }
                    } else if (!field.getType().getName().startsWith("[Z") && json.optJSONObject(name) != null) {
                        field.set(t, convertToObj(json.optJSONObject(name), field.getType()));
                    }
                }
            }
        }

        return t;
    }

    /**
     * 将json对象转换成Map对象
     *
     * @param jsonObject
     * @return
     */
    public static Map<String, String> convertToMap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        Map<String, String> result = new HashMap<String, String>();
        Iterator<String> iterator = jsonObject.keys();
        String key = null;
        String value = null;
        while (iterator.hasNext()) {
            key = iterator.next();
            try {
                value = jsonObject.getString(key);
            } catch (JSONException ignored) {
            }
            result.put(key, value);
        }
        return result;
    }

    /**
     * 将json数组转换成List集合
     *
     * @param jsonArray
     * @param cla
     * @return
     */
    public static List convertToList(JSONArray jsonArray, Class cla) {

        List list = new ArrayList();
        if (jsonArray == null)
            return list;

        try {
            JSONObject jsonObject = null;
            Object t = null;
//            for (int i = 0; i < jsonArray.length(); i++) {
//                jsonObject = jsonArray.getJSONObject(i);
//                t = JSONUtils.convertToObj(jsonObject, cla);
//                list.add(t);
//            }
            for (int i = 0; i < jsonArray.length(); i++) {
                Object item = jsonArray.opt(i);

                if (item.getClass().equals(String.class)) {
                    list.add(jsonArray.optString(i));
                } else if (item.getClass().equals(int.class) || item.getClass().equals(Integer.class)) {
                    list.add(jsonArray.optInt(i));
                } else if (item.getClass().equals(long.class) || item.getClass().equals(Long.class)) {
                    list.add(jsonArray.optLong(i));
                } else if (item.getClass().equals(boolean.class) || item.getClass().equals(Boolean.class)) {
                    list.add(jsonArray.optBoolean(i));
                } else if (item.getClass().equals(double.class) || item.getClass().equals(Double.class)) {
                    list.add(jsonArray.optDouble(i));
                } else {
                    jsonObject = jsonArray.getJSONObject(i);
                    t = JSONUtils.convertToObj(jsonObject, cla);
                    list.add(t);
                }

            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 将对象转换成json字符串
     *
     * @param obj
     * @return
     */
    public static String objectToJsonString(Object obj) {

        StringBuilder json = new StringBuilder();

        if (obj == null) {
            json.append("\"\"");
        } else if (obj instanceof String || obj instanceof Integer || obj instanceof Float || obj instanceof Boolean
                || obj instanceof Short || obj instanceof Double || obj instanceof Long || obj instanceof BigDecimal
                || obj instanceof BigInteger || obj instanceof Byte) {
            json.append("\"").append(stringToJson(obj.toString())).append("\"");
        } else if (obj instanceof Object[]) {
            json.append(arrayToJson((Object[]) obj));
        } else if (obj instanceof List) {
            json.append(listToJson((List<?>) obj));
        } else if (obj instanceof Map) {
            json.append(mapToJson((Map<?, ?>) obj));
        } else {
            json.append(beanToJson(obj));
        }

        return json.toString();
    }

    /**
     * @param str 字符串对象
     * @return
     */
    private static String stringToJson(String str) {
        if (Preconditions.isBlank(str)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        char ch;
        for (int i = 0; i < str.length(); i++) {
            ch = str.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    if (ch <= '\u001F') {
                        String ss = Integer.toHexString(ch);
                        sb.append("\\u");
                        for (int k = 0; k < 4 - ss.length(); k++) {
                            sb.append('0');
                        }
                        sb.append(ss.toUpperCase());
                    } else {
                        sb.append(ch);
                    }
            }
        }

        return sb.toString();
    }

    /**
     * @param array 对象数组
     * @return
     */
    private static String arrayToJson(Object[] array) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (Preconditions.isNotBlank(array)) {
            for (Object obj : array) {
                json.append(objectToJsonString(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }

        return json.toString();
    }

    /**
     * @param list List对象
     * @return
     */
    private static String listToJson(List<?> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (Preconditions.isNotBlank(list)) {
            for (Object obj : list) {
                json.append(objectToJsonString(obj));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }

        return json.toString();
    }

    /**
     * @param map Map对象
     * @return
     */
    private static String mapToJson(Map<?, ?> map) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        if (Preconditions.isNotBlank(map)) {
            for (Map.Entry entry : map.entrySet()) {
                json.append(objectToJsonString(entry.getKey()));
                json.append(":");
                json.append(objectToJsonString(entry.getValue()));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, '}');
        } else {
            json.append("}");
        }

        return json.toString();
    }

    /**
     * 将java bean转换成json字符串
     *
     * @param bean
     * @return
     */
    private static String beanToJson(Object bean) {
        if (bean == null) {
            return null;
        }
        StringBuilder json = new StringBuilder();
        json.append("{");

        Field[] fields = getAllField(bean.getClass());

        String name = null;
        if (Preconditions.isNotBlank(fields)) {
            for (Field field : fields) {
                try {
                    name = field.getName();

                    if (field.getType() != null && Preconditions.isNotBlank(name)) {
                        // 根据字段的类型将值转化为相应的类型，并设置到生成的对象中。
                        if (field.getType().equals(Long.class) || field.getType().equals(long.class)) {
                            long value = Reflect.on(bean).get(name);
                            json.append("\"");
                            json.append(name);
                            json.append("\"");
                            json.append(":");
                            json.append(value);
                            json.append(",");
                        } else if (field.getType().equals(String.class)) {
                            String value = Reflect.on(bean).get(name);

                            if (Preconditions.isNotBlank(value)) {
                                json.append("\"");
                                json.append(name);
                                json.append("\"");
                                json.append(":");
                                json.append("\"");
                                json.append(value);
                                json.append("\"");
                                json.append(",");
                            }
                        } else if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                            boolean value = Reflect.on(bean).get(name);

                            if (Preconditions.isNotBlank(value)) {
                                json.append("\"");
                                json.append(name);
                                json.append("\"");
                                json.append(":");
                                json.append("\"");
                                json.append(value);
                                json.append("\"");
                                json.append(",");
                            }
                        } else if (field.getType().equals(Double.class) || field.getType().equals(double.class)) {
                            double value = Reflect.on(bean).get(name);
                            json.append("\"");
                            json.append(name);
                            json.append("\"");
                            json.append(":");
                            json.append(value);
                            json.append(",");
                        } else if (field.getType().equals(Integer.class) || field.getType().equals(int.class)) {
                            int value = Reflect.on(bean).get(name);
                            json.append("\"");
                            json.append(name);
                            json.append("\"");
                            json.append(":");
                            json.append(value);
                            json.append(",");
                        } else if (field.getType().equals(Date.class)) {

                        } else if (field.getType().equals(List.class)) {
                            json.append("\"");
                            json.append(name);
                            json.append("\"");
                            json.append(":");
                            json.append(listToJson((List) Reflect.on(bean).get(name)));
                            json.append(",");
                        } else if (field.getType().equals(Map.class)) {
                            json.append("\"");
                            json.append(name);
                            json.append("\"");
                            json.append(":");
                            json.append(mapToJson((Map) Reflect.on(bean).get(name)));
                            json.append(",");
                        } else {
                            String value = beanToJson(Reflect.on(bean).get(name));

                            if (Preconditions.isNotBlank(value)) {
                                json.append("\"");
                                json.append(name);
                                json.append("\"");
                                json.append(":");
                                json.append(value);
                                json.append(",");
                            }

                        }
                    }
                } catch (Exception ignored) {
                }
            }
            json.setCharAt(json.length() - 1, '}');
        } else {
            json.append("}");
        }

        return json.toString();
    }

    /**
     * 获取类clazz的所有Field，包括其父类的Field，如果重名，以子类Field为准。
     *
     * @param clazz
     * @return Field数组
     */
    private static Field[] getAllField(Class<?> clazz) {
        ArrayList<Field> fieldList = new ArrayList<>();
        Field[] dFields = clazz.getDeclaredFields();
        if (Preconditions.isNotBlank(dFields)) {
            fieldList.addAll(Arrays.asList(dFields));
        }

        // 循环获取所有的父类
        clazz = clazz.getSuperclass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            Field[] superFields = getAllField(clazz);
            if (Preconditions.isNotBlank(superFields)) {
                for (Field field : superFields) {
                    if (!isContain(fieldList, field)) {
                        fieldList.add(field);
                    }
                }
            }
        }
        Field[] result = new Field[fieldList.size()];
        fieldList.toArray(result);
        return result;
    }

    /**
     * 检测Field List中是否已经包含了目标field
     *
     * @param fieldList
     * @param field     带检测field
     * @return
     */
    private static boolean isContain(ArrayList<Field> fieldList, Field field) {

        if (Preconditions.isNotBlank(fieldList)) {
            for (Field temp : fieldList) {
                if (temp.getName().equals(field.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isBlank(JSONObject jsonObject) {

        return jsonObject == null || jsonObject.length() == 0;
    }

    public static boolean isNotBlank(JSONObject jsonObject) {
        return !isBlank(jsonObject);
    }

    public static boolean isBlank(JSONArray jsonArray) {

        return jsonArray == null || jsonArray.length() == 0;
    }
}
