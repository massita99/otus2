package com.massita;

import com.massita.util.ReflectionUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ClassUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public class Serializer {

    public String toJson(Object serializedObject) {

        return serializedObject != null ? parseFieldValue(serializedObject.getClass(), serializedObject).toString() : "null";
    }

    private Object parseFieldValue(Class<?> convertedFieldType, Object convertedFieldValue) {

        if (ClassUtils.isPrimitiveOrWrapper(convertedFieldType)) {
            return convertedFieldValue;

        } else if (String.class.isAssignableFrom(convertedFieldType)) {
            return convertedFieldValue;

        } else if (convertedFieldType.isArray()) {
            return parseArray(convertedFieldValue);

        } else if (Collection.class.isAssignableFrom(convertedFieldType)) {
            return parseCollection(convertedFieldValue);

        } else if (convertedFieldValue == null) {
            return "null";

        } else {
            return parseObject(convertedFieldValue);
        }
    }

    @SneakyThrows({IllegalAccessException.class})
    //Cant throw because earlier make all field accessible
    private JSONObject parseObject(Object serializedObject) {

        JSONObject result = new JSONObject();

        List<Field> fields = ReflectionUtil.getAllSerializableFields(serializedObject.getClass());

        for (Field currentField : fields) {
            if (!currentField.canAccess(serializedObject)) {
                currentField.trySetAccessible();
            }

            Class<?> currentFieldType = currentField.getType();
            Object currentFieldValue = currentField.get(serializedObject);

            result.put(currentField.getName(), parseFieldValue(currentFieldType, currentFieldValue));

        }
        return result;
    }
    


    private Object parseCollection(Object currentFieldValue) {
        JSONArray array = new JSONArray();
        Collection collection = (Collection) currentFieldValue;
        for (Object collectionElement : collection) {
            array.add(parseFieldValue(collectionElement.getClass(), collectionElement));
        }
        return array;
    }

    private Object parseArray(Object currentFieldValue) {
        JSONArray array = new JSONArray();
        int length = Array.getLength(currentFieldValue);
        for (int i = 0; i < length; i ++) {
            Object arrayElement = Array.get(currentFieldValue, i);
            array.add(parseFieldValue(arrayElement.getClass(), arrayElement));
        }
        return array;
    }
}
