package minibank.util;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import minibank.model.annotation.MaxLength;
import minibank.model.annotation.Positive;

public final class AnnotationValidator {
    private AnnotationValidator() {}
    public static String[] validate(Object obj) {
        List<String> errors = new ArrayList<>();
        if (obj == null) return new String[]{"Object cannot be null"};
        Class<?> type = obj.getClass();
        while (type != null && type != Object.class) {
            for (Field field : type.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (field.isAnnotationPresent(Positive.class) && value instanceof Number number && number.longValue() <= 0) {
                        errors.add(field.getName() + ": " + field.getAnnotation(Positive.class).message());
                    }
                    if (field.isAnnotationPresent(MaxLength.class) && value instanceof String s &&
                        s.length() > field.getAnnotation(MaxLength.class).value()) {
                        errors.add(field.getName() + ": maximum length is " + field.getAnnotation(MaxLength.class).value());
                    }
                } catch (ReflectiveOperationException | RuntimeException e) {
                    errors.add("Could not validate field " + field.getName());
                }
            }
            type = type.getSuperclass();
        }
        return errors.toArray(new String[0]);
    }
}
