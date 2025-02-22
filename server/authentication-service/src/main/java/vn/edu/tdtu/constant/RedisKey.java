package vn.edu.tdtu.constant;

public class RedisKey {
    public static final String OTP_KEY = "otp";
    public static final String PASSWORD_CHECKING_TOKEN_KEY = "passwordCheckingToken";

    public static String combineKey(String... args){
        return String.join(":", args);
    }
}
