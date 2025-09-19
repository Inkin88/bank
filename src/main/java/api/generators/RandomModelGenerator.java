package api.generators;

import com.github.curiousoddman.rgxgen.RgxGen;

import java.lang.reflect.Field;

public class RandomModelGenerator {

    public static <T> T generate(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                GeneratingRule rule = field.getAnnotation(GeneratingRule.class);
                if (rule != null) {
                    String regExp = rule.regExp();

                    RgxGen rgxGen = new RgxGen(regExp);
                    String randomValue = rgxGen.generate();

                    field.setAccessible(true);
                    field.set(instance, randomValue);
                }
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации модели", e);
        }
    }
}
