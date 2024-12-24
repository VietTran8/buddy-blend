package vn.edu.tdtu.constant;

public class RedisKey {
    public static final String OTP_KEY = "otp";

    public static String combineKey(String... args){
        return String.join(":", args);
    }
}
