package vn.edu.tdtu.service.interfaces;

public interface RedisService<V> {
    void set(String key, String field, V value);

    void set(String key, String field, V value, long ttlInSeconds);

    V get(String key, String field);

    void set(String key, V value, long ttlInSeconds);

    void set(String key, V value);

    V get(String key);

    void delete(String key);

    void delete(String key, String field);

    void extendsTtl(String key, long ttlInSeconds);
}
