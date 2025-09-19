package api.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ObjectFieldAssertions {

    /**
     * Ассертация: все совпадающие по имени поля должны быть равны.
     * Кидает AssertionError, если поля отличаются или нет общих полей.
     */
    public static void assertFieldsEqual(Object obj1, Object obj2) {
        if (obj1 == null || obj2 == null) {
            throw new AssertionError("Один из объектов равен null: obj1=" + obj1 + ", obj2=" + obj2);
        }

        Map<String, Field> fields1 = getAllFields(obj1.getClass());
        Map<String, Field> fields2 = getAllFields(obj2.getClass());

        boolean hasCommonFields = false;

        for (String fieldName : fields1.keySet()) {
            if (fields2.containsKey(fieldName)) {
                hasCommonFields = true;

                try {
                    Field f1 = fields1.get(fieldName);
                    Field f2 = fields2.get(fieldName);

                    f1.setAccessible(true);
                    f2.setAccessible(true);

                    Object value1 = f1.get(obj1);
                    Object value2 = f2.get(obj2);

                    if (!Objects.equals(value1, value2)) {
                        throw new AssertionError(
                                "Поле '" + fieldName + "' отличается: obj1=" + value1 + ", obj2=" + value2
                        );
                    }

                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Ошибка при сравнении поля: " + fieldName, e);
                }
            }
        }

        if (!hasCommonFields) {
            throw new AssertionError("Нет общих полей между объектами: "
                    + obj1.getClass().getSimpleName() + " и "
                    + obj2.getClass().getSimpleName());
        }
    }

    private static Map<String, Field> getAllFields(Class<?> clazz) {
        Map<String, Field> map = new HashMap<>();
        while (clazz != null) {
            for (Field f : clazz.getDeclaredFields()) {
                map.put(f.getName(), f);
            }
            clazz = clazz.getSuperclass();
        }
        return map;
    }
}

