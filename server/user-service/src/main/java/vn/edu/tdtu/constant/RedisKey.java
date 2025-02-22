package vn.edu.tdtu.constant;

public class RedisKey {
    public static final String USER_STATUS_KEY = "status";

    public static String combineKey(String... args){
        return String.join(":", args);
    }
}
