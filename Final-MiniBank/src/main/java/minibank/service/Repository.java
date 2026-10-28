package minibank.service;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import minibank.model.annotation.Id;

public class Repository<T> {
    private final Map<String, T> items = new HashMap<>();

    public T save(T entity) {
        if (entity == null) throw new IllegalArgumentException("Entity cannot be null.");
        String id = readId(entity);
        items.put(id, entity);
        return entity;
    }

    @SafeVarargs
    public final void saveAll(T... entities) {
        for (T entity : entities) save(entity);
    }

    public T findById(String id) { return items.get(id); }
    public ArrayList<T> findAll() { return new ArrayList<>(items.values()); }
    public boolean delete(String id) { return items.remove(id) != null; }
    public int size() { return items.size(); }

    private String readId(T entity) {
        Class<?> type = entity.getClass();
        while (type != null && type != Object.class) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(entity);
                        if (value == null) throw new IllegalArgumentException("@Id field cannot be null.");
                        return value.toString();
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Unable to read @Id field.", e);
                    }
                }
            }
            type = type.getSuperclass();
        }
        throw new IllegalArgumentException("Entity has no field annotated with @Id: " + entity.getClass().getName());
    }
}
