package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class DataHolder {
    public static Map<String, Object> data = new ConcurrentHashMap<>();
    public static Map<String, List<Object>> listData = new ConcurrentHashMap<>();

    public static <T> T getData(String key, Class<T> clazz) {
        Object value = data.get(key);
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }

    public static void addToListData(String key, Object value) {
        listData.putIfAbsent(key, new ArrayList<>());
        listData.get(key).add(value);
    }

    public static void addToListData(String key, List<Object> values) {
        listData.putIfAbsent(key, new ArrayList<>());
        listData.get(key).addAll(values);
    }

    public static String getData(String key) {
        Object value = data.get(key);
        if (value != null) {
            return value.toString();
        }
        return null;
    }
}
